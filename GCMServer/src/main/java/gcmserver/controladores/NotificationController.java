package gcmserver.controladores;

import gcmserver.core.Constants;
import gcmserver.core.Message;
import gcmserver.core.MulticastResult;
import gcmserver.core.Result;
import gcmserver.core.Sender;
import gcmserver.serverlets.Datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Notification")
public class NotificationController {

	protected final Logger logger = Logger.getLogger(getClass().getName());

	private static final int MULTICAST_SIZE = 1000;

	private Sender sender;
	private Message message;

	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	@Value("${gcm.attributeAccesKey}")
	private String attribute_acces_key;

	@PostConstruct
	public void init() {

		sender = new Sender(attribute_acces_key);
		message = new Message();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {
		modelo.addAttribute("message", message);
		return new ModelAndView("Notifications", "modelo", modelo);
	}

	/**
	 * Send a message to a single target device via HTTP using plain text
	 * 
	 * @param regId
	 * @param message
	 * @param sesion
	 * @param modelo
	 * @throws IOException
	 */
	@RequestMapping(value = "/notificationHttpPlain", method = RequestMethod.POST)
	protected ModelAndView notificationHttpPlain(
			@ModelAttribute Message message, HttpSession sesion, Model modelo)
			throws IOException {
		/*
		 * TODO Send http plain text notification
		 */

		String registrationId = message.getTarget();
		String status;
		Result result = null;
		if (registrationId.isEmpty()) {
			status = "Message ignored as there is no target device id!";
		} else {
			// send a single message using plain post
			result = this.sender.send(message, registrationId, 5);
			status = "Sent message to one device: " + result;
		}

		// Logging the status to system log y web log
		logger.fine(status);

		modelo.addAttribute("message", message);
		modelo.addAttribute("response", result.toString());
		return new ModelAndView("Notifications", "modelo", modelo);
	}

	/**
	 * Send a message to a single target, a notification group or a multiple
	 * targets via HTTP using JSON
	 * 
	 * @param targets
	 * @param message
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/notificationHttpJson", method = RequestMethod.GET)
	protected void notificationHttpJson(@RequestParam List<String> targets,
			@ModelAttribute Message message, HttpSession sesion, Model modelo) {
		/*
		 * TODO Send http JSON notification
		 */
		List<String> devices = targets;
		String status;
		if (devices.isEmpty()) {
			status = "Message ignored as there is no target devices!";
		} else {
			// send a multicast or single message using JSON
			// NOTE: a real application
			// could always send a multicast, even for just one recipient.
			// Thus if we want to send a single message we pass one device only
			// If we are sending a multicast message using JSON
			// we must split in chunks of 1000 devices due to GCM limit

			int total = devices.size();
			List<String> partialDevices = new ArrayList<String>(total);
			int counter = 0;
			int tasks = 0;
			for (String device : devices) {
				counter++;
				partialDevices.add(device);
				int partialSize = partialDevices.size();
				if (partialSize == MULTICAST_SIZE || counter == total) {
					asyncSend(partialDevices, message);
					partialDevices.clear();
					tasks++;
				}
			}
			status = "Asynchronously sending " + tasks
					+ " multicast messages to " + total + " devices";
		}
		logger.fine(status);
	}

	@RequestMapping(value = "/notificationXMPP", method = RequestMethod.GET)
	protected void notificationXMPP(@RequestParam String regId,
			HttpSession sesion, Model modelo) {
		/*
		 * TODO Send XMPP JSON notification
		 */
	}

	/**
	 * Send asynchronously the message to the partial list of devices
	 * 
	 * @param partialDevices
	 * @param message
	 */
	private void asyncSend(List<String> partialDevices, Message message) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialDevices);

		threadPool.execute(new Runnable() {

			public void run() {

				MulticastResult multicastResult;
				try {
					multicastResult = sender.send(message, devices, 5);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error posting messages", e);
					return;
				}
				List<Result> results = multicastResult.getResults();
				// analyze the results
				for (int i = 0; i < devices.size(); i++) {
					String regId = devices.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						logger.fine("Succesfully sent message to device: "
								+ regId + "; messageId = " + messageId);
						String canonicalRegId = result
								.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id:
							// update it
							logger.info("canonicalRegId " + canonicalRegId);
							Datastore.updateRegistration(regId, canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							logger.info("Unregistered device: " + regId);
							Datastore.unregister(regId);
						} else {
							logger.severe("Error sending message to " + regId
									+ ": " + error);
						}
					}
				}
			}
		});
	}

}
