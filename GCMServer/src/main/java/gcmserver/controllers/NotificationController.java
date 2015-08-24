package gcmserver.controllers;

import gcmserver.controllers.model.MessageViewModel;
import gcmserver.core.Constants;
import gcmserver.core.DeviceManager;
import gcmserver.core.GroupManager;
import gcmserver.core.Sender;
import gcmserver.core.TopicManager;
import gcmserver.model.GroupMessageResult;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class NotificationController {

	private final Logger logger = LoggerFactory
			.getLogger(NotificationController.class);

	private static final int MULTICAST_SIZE = 1000;

	private Sender sender;
	private MessageViewModel messagePlain;
	private MessageViewModel messageJson;
	private MessageViewModel messageTopic;
	private MessageViewModel messageGroup;
	private MessageViewModel messageJsonMulti;
	private MessageViewModel messageXMPP;
	private DeviceManager deviceMngr;
	private TopicManager topicMngr;
	private GroupManager groupMngr;
	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	// API key obtained through the Google API Console.
	@Value("${gcm.attributeAccesKey}")
	private String attribute_acces_key;

	@PostConstruct
	public void init() {

		sender = new Sender(attribute_acces_key);
		messagePlain = new MessageViewModel();
		messageJson = new MessageViewModel();
		messageTopic = new MessageViewModel();
		messageGroup = new MessageViewModel();
		messageJsonMulti = new MessageViewModel();
		messageXMPP = new MessageViewModel();
		deviceMngr = DeviceManager.getInstance();
		topicMngr = TopicManager.getInstance();
		groupMngr = GroupManager.getInstance();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {

		// modelo.addAttribute("messagePlain", messagePlain);
		// modelo.addAttribute("messageJson", messageJson);
		// modelo.addAttribute("messageTopic", messageTopic);
		// modelo.addAttribute("messageGroup", messageGroup);
		// modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		// modelo.addAttribute("messageXMPP", messageXMPP);
		// modelo.addAttribute("deviceList",
		// deviceMngr.getdeviceListSnapshot());
		// modelo.addAttribute("topicList",topicMngr.getTopicsListSnapshot());
		// modelo.addAttribute("groupList",groupMngr.getGroupListSnapshot());
		modelo.addAttribute("request", "");
		modelo.addAttribute("response", "");
		// logger.info("Modelo: " +modelo.toString());

		return new ModelAndView("Notifications", "modelo", createModel(modelo));
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
	@RequestMapping(value = "/Notification/notificationHttpPlain", method = RequestMethod.POST)
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

			Boolean delayWhileIdle = message.getDelayWhileIdle();
			if (delayWhileIdle != null) {
				messageBuilder.delayWhileIdle(delayWhileIdle);
			}

			Boolean dryRun = message.getDryRun();
			if (dryRun != null) {
				messageBuilder.dryRun(dryRun);
			}

			String data = message.getData();
			if (!data.isEmpty()) {
				// build hasmap from plain text string
				Map<String, String> mapData = new HashMap<String, String>();

				for (String keyValue : data.split(" *& *")) {
					String[] pairs = keyValue.split(" *= *", 2);
					mapData.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
				}
				// pass data to message class
				for (Map.Entry<String, String> entry : mapData.entrySet()) {
					if (entry.getValue() != "") {
						messageBuilder
								.addData(entry.getKey(), entry.getValue());
					}
				}
			}
			// send a single message using plain post
			result = this.sender.sendHttpPlainText(messageBuilder.build(),
					registrationId, 5);
			status = "Sent message to one device: " + result.toString();
		}

		// Logging the status
		logger.info(status);

		// modelo.addAttribute("messagePlain", messagePlain);
		// modelo.addAttribute("messageJson", messageJson);
		// modelo.addAttribute("messageTopic", messageTopic);
		// modelo.addAttribute("messageGroup", messageGroup);
		// modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		// modelo.addAttribute("messageXMPP", messageXMPP);
		// modelo.addAttribute("deviceList",deviceMngr.getdeviceListSnapshot());
		// modelo.addAttribute("topicList",topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("request", message.toString());
		modelo.addAttribute("response", result.toString());
		// logger.info("Modelo: " + modelo.toString());

		return new ModelAndView("Notifications", "modelo", createModel(modelo));
	}

	/**
	 * Send a message to a single target via HTTP using JSON
	 * 
	 * @param targets
	 * @param message
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/Notification/notificationHttpJson", method = RequestMethod.POST)
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

			String prority = message.getPriority();
			if (!prority.isEmpty()) {
				messageBuilder.priority(prority);
			}

			Boolean delayWhileIdle = message.getDelayWhileIdle();
			if (delayWhileIdle != null) {
				messageBuilder.delayWhileIdle(delayWhileIdle);
			}

			Boolean dryRun = message.getDryRun();
			if (dryRun != null) {
				messageBuilder.dryRun(dryRun);
			}

			Boolean contentAvailable = message.getContentAvailable();
			if (contentAvailable != null) {
				messageBuilder.contentAvailable(contentAvailable);
			}

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

			// Convert the json string stored in data to a HashMap and build
			// data in message class
			String data = message.getData();
			if (!data.isEmpty()) {
				Map<String, String> mapData = new HashMap<String, String>();
				ObjectMapper mapper = new ObjectMapper();
				try {
					// convert JSON string to Map
					mapData = mapper.readValue(data,
							new TypeReference<HashMap<String, String>>() {
							});
					for (Map.Entry<String, String> entry : mapData.entrySet()) {
						if (entry.getValue() != "") {
							messageBuilder.addData(entry.getKey(),
									entry.getValue());
						}
					}
				} catch (Exception e) {
					logger.error("Error while creating HasMap from data string "
							+ e.toString());
					e.printStackTrace();
				}
			}

			// Send the message to the target device
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

		// modelo.addAttribute("messagePlain", messagePlain);
		// modelo.addAttribute("messageJson", messageJson);
		// modelo.addAttribute("messageTopic", messageTopic);
		// modelo.addAttribute("messageGroup", messageGroup);
		// modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		// modelo.addAttribute("messageXMPP", messageXMPP);
		// modelo.addAttribute("deviceList",
		// deviceMngr.getdeviceListSnapshot());
		// modelo.addAttribute("topicList", topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("request", message.toString());
		modelo.addAttribute("response", multicastResult.toString());
		// logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", createModel(modelo));
	}

	/**
	 * Send a message to group of devices subscribed to a topic via HTTP using
	 * JSON
	 * 
	 * @param targets
	 * @param message
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/Notification/topicMessage", method = RequestMethod.POST)
	protected ModelAndView topicMessage(
			@ModelAttribute MessageViewModel message, HttpSession sesion,
			Model modelo) {

		// Send single http JSON notification to a topic
		// The target is a topic
		String topic;
		topic = message.getTarget().toString();
		String status;
		Result result = null;
		if (topic.isEmpty()) {
			status = "Message ignored as there is no target topic!";
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

			String prority = message.getPriority();
			if (!prority.isEmpty()) {
				messageBuilder.priority(prority);
			}

			Boolean delayWhileIdle = message.getDelayWhileIdle();
			if (delayWhileIdle != null) {
				messageBuilder.delayWhileIdle(delayWhileIdle);
			}

			Boolean dryRun = message.getDryRun();
			if (dryRun != null) {
				messageBuilder.dryRun(dryRun);
			}

			Boolean contentAvailable = message.getContentAvailable();
			if (contentAvailable != null) {
				messageBuilder.contentAvailable(contentAvailable);
			}

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

			// Convert the json string stored in data to a HashMap and build
			// data in message class
			String data = message.getData();
			if (!data.isEmpty()) {
				Map<String, String> mapData = new HashMap<String, String>();
				ObjectMapper mapper = new ObjectMapper();
				try {
					// convert JSON string to Map
					mapData = mapper.readValue(data,
							new TypeReference<HashMap<String, String>>() {
							});
					for (Map.Entry<String, String> entry : mapData.entrySet()) {
						if (entry.getValue() != "") {
							messageBuilder.addData(entry.getKey(),
									entry.getValue());
						}
					}
				} catch (Exception e) {
					logger.error("Error while creating HasMap from data string "
							+ e.toString());
					e.printStackTrace();
				}
			}

			// Send the message to the target device
			try {
				result = sender.sendTopic(messageBuilder.build(), topic, 5);
				// analyze the results

				String messageId = result.getMessageId();
				if (messageId != null) {
					logger.info("Succesfully sent message to topic: " + topic
							+ "; messageId = " + messageId);
				} else {
					String error = result.getErrorCodeName();
					if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
						// application has been removed from device -
						// unregister it
						logger.info("Unregistered topic: " + topic);
						// Datastore.unregister(regId);
					} else {
						logger.error("Error sending message to " + topic + ": "
								+ error);
					}
				}

			} catch (IOException e) {
				logger.error("Error posting messages", e);
			}

		}

		status = "Message sent to topic";

		logger.info(status);

		// modelo.addAttribute("messagePlain", messagePlain);
		// modelo.addAttribute("messageJson", messageJson);
		// modelo.addAttribute("messageTopic", messageTopic);
		// modelo.addAttribute("messageGroup", messageGroup);
		// modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		// modelo.addAttribute("messageXMPP", messageXMPP);
		// modelo.addAttribute("deviceList",
		// deviceMngr.getdeviceListSnapshot());
		// modelo.addAttribute("topicList", topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("request", message.toString());
		modelo.addAttribute("response", result.toString());
		// logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", createModel(modelo));
	}

	/**
	 * Send a message to group of devices subscribed to a topic via HTTP using
	 * JSON
	 * 
	 * @param targets
	 * @param message
	 * @param sesion
	 * @param modelo
	 */
	@RequestMapping(value = "/Notification/groupMessage", method = RequestMethod.POST)
	protected ModelAndView groupMessage(
			@ModelAttribute MessageViewModel message, HttpSession sesion,
			Model modelo) {
		// Send single http JSON notification
		// NOTE: a real application
		// could always send a multicast, even for just one recipient.
		// Thus if we want to send a single message we pass one device only
		// The targetList will contain only 1 target since is a single JSON
		// message

		String targetGroup = message.getTarget();
		String status;
		GroupMessageResult groupResult = null;
		if (targetGroup.isEmpty()) {
			status = "Message ignored as there is no target group!";
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

			String prority = message.getPriority();
			if (!prority.isEmpty()) {
				messageBuilder.priority(prority);
			}

			Boolean delayWhileIdle = message.getDelayWhileIdle();
			if (delayWhileIdle != null) {
				messageBuilder.delayWhileIdle(delayWhileIdle);
			}

			Boolean dryRun = message.getDryRun();
			if (dryRun != null) {
				messageBuilder.dryRun(dryRun);
			}

			Boolean contentAvailable = message.getContentAvailable();
			if (contentAvailable != null) {
				messageBuilder.contentAvailable(contentAvailable);
			}

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

			// Convert the json string stored in data to a HashMap and build
			// data in message class
			String data = message.getData();
			if (!data.isEmpty()) {
				Map<String, String> mapData = new HashMap<String, String>();
				ObjectMapper mapper = new ObjectMapper();
				try {
					// convert JSON string to Map
					mapData = mapper.readValue(data,
							new TypeReference<HashMap<String, String>>() {
							});
					for (Map.Entry<String, String> entry : mapData.entrySet()) {
						if (entry.getValue() != "") {
							messageBuilder.addData(entry.getKey(),
									entry.getValue());
						}
					}
				} catch (Exception e) {
					logger.error("Error while creating HasMap from data string "
							+ e.toString());
					e.printStackTrace();
				}
			}

			// Send the message to the target device
			try {
				groupResult = sender.sendGroup(messageBuilder.build(),
						targetGroup, 5);

				if (groupResult.getfailedRegistrationIds().isEmpty()) {
					logger.info("Succesfully sent message to group: "
							+ targetGroup);
				}

			} catch (IOException e) {
				logger.error("Error posting messages", e);
			}

		}

		status = "Message sent to device";

		logger.info(status);

		// modelo.addAttribute("messagePlain", messagePlain);
		// modelo.addAttribute("messageJson", messageJson);
		// modelo.addAttribute("messageTopic", messageTopic);
		// modelo.addAttribute("messageGroup", messageGroup);
		// modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		// modelo.addAttribute("messageXMPP", messageXMPP);
		// modelo.addAttribute("deviceList",
		// deviceMngr.getdeviceListSnapshot());
		// modelo.addAttribute("topicList", topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("request", message.toString());
		modelo.addAttribute("response", groupResult.toString());
		// logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Notifications", "modelo", createModel(modelo));
	}

	private Model createModel(Model modelo) {
		modelo.addAttribute("messagePlain", messagePlain);
		modelo.addAttribute("messageJson", messageJson);
		modelo.addAttribute("messageTopic", messageTopic);
		modelo.addAttribute("messageGroup", messageGroup);
		modelo.addAttribute("messageJsonMulti", messageJsonMulti);
		modelo.addAttribute("messageXMPP", messageXMPP);
		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("topicList", topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		logger.info("Modelo: " + modelo.toString());
		return modelo;
	}
}
