package gcmserver.controllers;

import gcmserver.controllers.model.MessageViewModel;
import gcmserver.core.Constants;
import gcmserver.core.DeviceManager;
import gcmserver.core.Sender;
import gcmserver.model.Message;
import gcmserver.model.MulticastResult;
import gcmserver.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private final Logger logger = LoggerFactory
			.getLogger(NotificationController.class);

	private static final int MULTICAST_SIZE = 1000;

	private Sender sender;
	private MessageViewModel messagePlain;
	private MessageViewModel messageJson;
	private MessageViewModel messageJsonMulti;
	private MessageViewModel messageXMPP;
	private DeviceManager deviceMngr;
	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	// API key obtained through the Google API Console.
	@Value("${gcm.attributeAccesKey}")
	private String attribute_acces_key;

	@PostConstruct
	public void init() {

		sender = new Sender(attribute_acces_key);
		messagePlain = new MessageViewModel();
		messageJson = new MessageViewModel();
		messageJsonMulti = new MessageViewModel();
		messageXMPP = new MessageViewModel();
		deviceMngr = DeviceManager.getInstance();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {
		modelo.addAttribute("messagePlain", messagePlain);
		modelo.addAttribute("messageJson", messageJson);
		modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		modelo.addAttribute("messageXMPP", messageXMPP);
		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("request", "");
		modelo.addAttribute("response", "");
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
			@ModelAttribute MessageViewModel message, HttpSession sesion,
			Model modelo) throws IOException {
		/*
		 * Send http plain text notification
		 */

		String registrationId = message.getTarget();
		String status;
		Result result = null;
		if (registrationId.isEmpty()) {
			status = "Message ignored as there is no target device id!";
		} else {
			final Message.Builder messageBuilder = new Message.Builder();
			String collapseKey = message.getCollapseKey();
			if (!collapseKey.isEmpty()) {
				messageBuilder.collapseKey(collapseKey.trim());
			}
			String restrictedPackageName = message.getRestrictedPackageName();
			if (!restrictedPackageName.isEmpty()) {
				messageBuilder.restrictedPackageName(restrictedPackageName
						.trim());
			}
			Integer timeToLive = message.getTimeToLive();
			if (timeToLive != null) {
				messageBuilder.timeToLive(timeToLive);
			}

			// Boolean
			messageBuilder.delayWhileIdle(message.getDelayWhileIdle());
			// Boolean
			messageBuilder.dryRun(message.getDryRun());
			// TODO add the data to message
			/*
			 * EditableMapView data = (EditableMapView) activity
			 * .findViewById(R.id.downstream_data); for
			 * (EditableMapView.MapEntry mapEntry : data.getMapEntries()) { if
			 * (isNotEmpty(mapEntry.key) && isNotEmpty(mapEntry.value)) {
			 * messageBuilder.addData(mapEntry.key, mapEntry.value); } }
			 */
			// send a single message using plain post
			result = this.sender.sendHttpPlainText(messageBuilder.build(),
					registrationId, 5);
			status = "Sent message to one device: " + result.toString();
		}

		// Logging the status
		logger.info(status);

		modelo.addAttribute("messagePlain", messagePlain);
		modelo.addAttribute("messageJson", messageJson);
		modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		modelo.addAttribute("messageXMPP", messageXMPP);
		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("request", message.toString());
		modelo.addAttribute("response", result.toString());
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", modelo);
	}

	/**
	 * Send a message to a single target via HTTP using JSON
	 * 
	 * @param targets
	 * @param message
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/notificationHttpJson", method = RequestMethod.POST)
	protected ModelAndView notificationHttpJson(
			@ModelAttribute MessageViewModel message, HttpSession sesion,
			Model modelo) {

		// Send single http JSON notification
		// NOTE: a real application
		// could always send a multicast, even for just one recipient.
		// Thus if we want to send a single message we pass one device only
		// The targetList will contain only 1 target since is a single JSON
		// message

		List<String> targetList = new ArrayList<String>(1);
		targetList.add(message.getTarget());
		String status;
		MulticastResult multicastResult = null;
		if (targetList.isEmpty()) {
			status = "Message ignored as there is no target devices!";
		} else {
			final Message.Builder messageBuilder = new Message.Builder();
			String collapseKey = message.getCollapseKey();
			if (!collapseKey.isEmpty()) {
				messageBuilder.collapseKey(collapseKey.trim());
			}
			String restrictedPackageName = message.getRestrictedPackageName();
			if (!restrictedPackageName.isEmpty()) {
				messageBuilder.restrictedPackageName(restrictedPackageName
						.trim());
			}
			Integer timeToLive = message.getTimeToLive();
			if (timeToLive != null) {
				messageBuilder.timeToLive(timeToLive);
			}
			Integer prority = message.getPriority();
			if (prority != null) {
				messageBuilder.priority(prority);
			}
			// Boolean
			messageBuilder.delayWhileIdle(message.getDelayWhileIdle());
			// Boolean
			messageBuilder.dryRun(message.getDryRun());
			// Boolean
			messageBuilder.contentAvailable(message.getContentAvailable());

			if (message.getEnableNotification()) {
				HashMap<String, String> noti = new HashMap<String, String>(
						message.getNotification());
				for (Map.Entry<String, String> entry : noti.entrySet()) {
					if (entry.getValue() != "") {
						messageBuilder.addNotification(entry.getKey(),
								entry.getValue());
					}
				}

			}

			// TODO Add data fields
			try {
				multicastResult = sender.sendHttpJson(messageBuilder.build(),
						targetList, 5);
				List<Result> results = multicastResult.getResults();
				// analyze the results

				String regId = targetList.get(0);
				Result result = results.get(0);
				String messageId = result.getMessageId();
				if (messageId != null) {
					logger.info("Succesfully sent message to device: " + regId
							+ "; messageId = " + messageId);
					String canonicalRegId = result.getCanonicalRegistrationId();
					if (canonicalRegId != null) {
						// same device has more than on registration id:
						// update it
						logger.info("Found canonicalRegId " + canonicalRegId);
						deviceMngr.updateRegistration(regId, canonicalRegId);
					}
				} else {
					String error = result.getErrorCodeName();
					if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
						// application has been removed from device -
						// unregister it
						logger.info("Unregistered device: " + regId);
						// Datastore.unregister(regId);
					} else {
						logger.error("Error sending message to " + regId + ": "
								+ error);
					}
				}

			} catch (IOException e) {
				logger.error("Error posting messages", e);
			}

		}

		status = "Message sent to device";

		logger.info(status);

		modelo.addAttribute("messagePlain", messagePlain);
		modelo.addAttribute("messageJson", messageJson);
		modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		modelo.addAttribute("messageXMPP", messageXMPP);
		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("request", message.toString());
		modelo.addAttribute("response", multicastResult.toString());
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", modelo);
	}

	/**
	 * Send a message to multiples targets via HTTP using JSON
	 * 
	 * @param targets
	 * @param message
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/notificationHttpJsonMulti", method = RequestMethod.POST)
	protected ModelAndView notificationHttpJsonMulti(
			@ModelAttribute MessageViewModel message, HttpSession sesion,
			Model modelo) {
		/*
		 * TODO Send http JSON notification to multiple devices. DEPRECATED
		 */
		List<String> targetList = message.getListTargets();
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

			// Build message
			final Message.Builder messageBuilder = new Message.Builder();
			String collapseKey = message.getCollapseKey();
			if (!collapseKey.isEmpty()) {
				messageBuilder.collapseKey(collapseKey.trim());
			}
			String restrictedPackageName = message.getRestrictedPackageName();
			if (!restrictedPackageName.isEmpty()) {
				messageBuilder.restrictedPackageName(restrictedPackageName
						.trim());
			}
			Integer timeToLive = message.getTimeToLive();
			if (timeToLive != null) {
				messageBuilder.timeToLive(timeToLive);
			}
			Integer prority = message.getPriority();
			if (prority != null) {
				messageBuilder.priority(prority);
			}
			// Boolean
			messageBuilder.delayWhileIdle(message.getDelayWhileIdle());
			// Boolean
			messageBuilder.dryRun(message.getDryRun());
			// Boolean
			messageBuilder.contentAvailable(message.getContentAvailable());

			int total = targetList.size();
			List<String> partialTargets = new ArrayList<String>(total);
			int counter = 0;
			int tasks = 0;
			for (String target : targetList) {
				counter++;
				partialTargets.add(target);
				int partialSize = partialTargets.size();
				if (partialSize == MULTICAST_SIZE || counter == total) {
					asyncSend(partialTargets, messageBuilder.build());
					partialTargets.clear();
					tasks++;
				}
			}
			status = "Asynchronously sending " + tasks
					+ " multicast messages to " + total + " devices";
		}
		logger.info(status);
		modelo.addAttribute("messagePlain", messagePlain);
		modelo.addAttribute("messageJson", messageJson);
		modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		modelo.addAttribute("messageXMPP", messageXMPP);
		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("request", message.toString());
		modelo.addAttribute("response", status.toString());
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", modelo);
	}

	@RequestMapping(value = "/notificationXMPP", method = RequestMethod.POST)
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
	private void asyncSend(List<String> partialTargets, final Message message) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialTargets);

		threadPool.execute(new Runnable() {

			public void run() {

				MulticastResult multicastResult;
				try {
					multicastResult = sender.sendHttpJson(message, devices, 5);
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
							// Datastore.unregister(regId);
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
