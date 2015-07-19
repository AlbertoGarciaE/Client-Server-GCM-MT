package gcmserver.controladores;

import gcmserver.core.Constants;
import gcmserver.core.Device;
import gcmserver.core.DeviceManager;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	protected final Logger logger = LoggerFactory.getLogger(Sender.class);

	private static final int MULTICAST_SIZE = 1000;

	private Sender sender;
	private Message messagePlain;
	private Message messageJson;
	private Message messageJsonMulti;
	private Message messageXMPP;
	private DeviceManager deviceMngr;
	private List<Device> deviceList;

	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	@Value("${gcm.attributeAccesKey}")
	private String attribute_acces_key;

	@PostConstruct
	public void init() {

		sender = new Sender(attribute_acces_key);
		messagePlain = new Message();
		messageJson = new Message();
		messageJsonMulti = new Message();
		messageXMPP = new Message();
		deviceMngr = DeviceManager.getInstance();
		deviceList = deviceMngr.getdeviceList();
		//TODO init list of regIds
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {
		modelo.addAttribute("messagePlain", messagePlain);
		modelo.addAttribute("messageJson", messageJson);
		modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		modelo.addAttribute("messageXMPP", messageXMPP);
		modelo.addAttribute("deviceList", deviceMngr.getdeviceList());
		logger.info("Modelo: " + modelo.toString());
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
		logger.info(status);

		modelo.addAttribute("message", message);
		modelo.addAttribute("response", result.toString());
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", modelo);
	}

	/**
	 * Send a message to a single target via HTTP using JSON
	 * 
	 * @param targets
	 * @param messageSingleJSON
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/notificationHttpJson", method = RequestMethod.GET)
	protected ModelAndView notificationHttpJson(
			@ModelAttribute Message messageSingleJSON, HttpSession sesion,
			Model modelo) {
		/*
		 * TODO Send http JSON notification
		 */
		List<String> targetList = new ArrayList<String>();
		targetList.add(messageSingleJSON.getTarget());
		String status;
		if (targetList.isEmpty()) {
			status = "Message ignored as there is no target devices!";
		} else {
			
			//TODO simplify the logic in order to no use the asynchsend()
			// send a multicast or single message using JSON
			// NOTE: a real application
			// could always send a multicast, even for just one recipient.
			// Thus if we want to send a single message we pass one device only
			// If we are sending a multicast message using JSON
			// we must split in chunks of 1000 devices due to GCM limit

			int total = targetList.size();
			List<String> partialTargets = new ArrayList<String>(total);
			int counter = 0;
			int tasks = 0;
			for (String target : targetList) {
				counter++;
				partialTargets.add(target);
				int partialSize = partialTargets.size();
				if (partialSize == MULTICAST_SIZE || counter == total) {
					asyncSend(partialTargets, messageSingleJSON);
					partialTargets.clear();
					tasks++;
				}
			}
			status = "Asynchronously sending " + tasks
					+ " multicast messages to " + total + " devices";
		}
		logger.info(status);
		// TODO recuperar la multiastResponse
		modelo.addAttribute("message", messageSingleJSON);
		// modelo.addAttribute("response", result.toString());
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", modelo);
	}

	/**
	 * Send a message to multiples targets via HTTP using JSON
	 * 
	 * @param targets
	 * @param messageMultiJSON
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/notificationHttpJsonMulti", method = RequestMethod.GET)
	protected ModelAndView notificationHttpJsonMulti(
			@ModelAttribute Message messageMultiJSON, HttpSession sesion,
			Model modelo) {
		/*
		 * TODO Send http JSON notification
		 */
		List<String> targetList = messageMultiJSON.getListTargets();
		String status;
		if (targetList.isEmpty()) {
			status = "Message ignored as there is no target devices!";
		} else {
			// send a multicast or single message using JSON
			// NOTE: a real application
			// could always send a multicast, even for just one recipient.
			// Thus if we want to send a single message we pass one device only
			// If we are sending a multicast message using JSON
			// we must split in chunks of 1000 devices due to GCM limit

			int total = targetList.size();
			List<String> partialTargets = new ArrayList<String>(total);
			int counter = 0;
			int tasks = 0;
			for (String target : targetList) {
				counter++;
				partialTargets.add(target);
				int partialSize = partialTargets.size();
				if (partialSize == MULTICAST_SIZE || counter == total) {
					asyncSend(partialTargets, messageMultiJSON);
					partialTargets.clear();
					tasks++;
				}
			}
			status = "Asynchronously sending " + tasks
					+ " multicast messages to " + total + " devices";
		}
		logger.info(status);
		// TODO recuperar la multiastResponse
		modelo.addAttribute("message", messageMultiJSON);
		// modelo.addAttribute("response", result.toString());
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", modelo);
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
	 * @param partialTargets
	 * @param message
	 */
	private void asyncSend(List<String> partialTargets, Message message) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialTargets);

		threadPool.execute(new Runnable() {

			public void run() {

				MulticastResult multicastResult;
				try {
					multicastResult = sender.send(message, devices, 5);
				} catch (IOException e) {
					logger.error("Error posting messages", e);
					return;
				}
				List<Result> results = multicastResult.getResults();
				// analyze the results
				for (int i = 0; i < devices.size(); i++) {
					String regId = devices.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						logger.info("Succesfully sent message to device: "
								+ regId + "; messageId = " + messageId);
						String canonicalRegId = result
								.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id:
							// update it
							logger.info("canonicalRegId " + canonicalRegId);
							deviceMngr
									.updateRegistration(regId, canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							logger.info("Unregistered device: " + regId);
							Datastore.unregister(regId);
						} else {
							logger.error("Error sending message to " + regId
									+ ": " + error);
						}
					}
				}
			}
		});
	}

}
