package gcmserver.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gcmserver.controllers.model.GroupViewModel;
import gcmserver.controllers.model.TopicViewModel;
import gcmserver.core.Constants;
import gcmserver.core.DeviceManager;
import gcmserver.core.GroupManagementSender;
import gcmserver.core.GroupManager;
import gcmserver.core.TopicManager;
import gcmserver.model.Devices;
import gcmserver.model.GroupManagementMessage;
import gcmserver.model.GroupResult;
import gcmserver.model.Groups;
import gcmserver.model.ObjectFactory;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/Groups")
public class GroupManagementController {

	private DeviceManager deviceMngr;
	private GroupManager groupMngr;
	private GroupViewModel newGroup;
	private GroupManagementSender groupSender;
	private ObjectFactory factory;

	private static final Logger logger = LoggerFactory
			.getLogger(DeviceManagementController.class);

	// API key obtained through the Google API Console.
	@Value("${gcm.attributeAccesKey}")
	private String attribute_acces_key;

	// API key obtained through the Google API Console.
	@Value("${gcm.projectID}")
	private String sender_id;

	@PostConstruct
	public void init() {

		factory = new ObjectFactory();
		deviceMngr = DeviceManager.getInstance();
		groupMngr = GroupManager.getInstance();
		groupSender = new GroupManagementSender(attribute_acces_key, sender_id);
		// Load devices stored in XML file
		groupMngr.load();
		newGroup = new GroupViewModel();

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);

		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);
	}

	@RequestMapping(value = "/unregisterGroup", method = RequestMethod.GET)
	protected ModelAndView unregisterGroup(
			@RequestParam String notificationKeyName,
			@RequestParam String notificationKey, HttpSession sesion,
			Model modelo) {
		// Build GroupManagementMessage to be sent
		GroupManagementMessage.Builder builder = new GroupManagementMessage.Builder();
		builder.operation(Constants.GROUP_OPERATION_REMOVE);
		builder.notificationKeyName(notificationKeyName);
		builder.notificationKey(notificationKey);
		// Find all the registration Ids of this group
		Groups.Group groupTarget = groupMngr.getGroup(notificationKey);
		List<Groups.Group.RegistrationId> list = groupTarget
				.getRegistrationIdList();
		for (Groups.Group.RegistrationId regId : list) {
			builder.addRegId(regId.getName(), regId.getValue());
		}
		GroupManagementMessage message = builder.build();
		try {
			GroupResult result = groupSender.sendRemoveFromGroup(message);
			groupMngr.unregisterGroup(notificationKeyName, notificationKey);
			// Save to XML file
			groupMngr.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);
	}

	@RequestMapping(value = "/registerGroup", method = RequestMethod.POST)
	protected ModelAndView registerGroup(@ModelAttribute GroupViewModel group,
			HttpSession sesion, Model modelo) {

		String error = null;
		GroupManagementMessage.Builder builder = new GroupManagementMessage.Builder();
		builder.operation(Constants.GROUP_OPERATION_CREATE);
		String notificatonKeyName = group.getNotificationKeyName();
		if (notificatonKeyName != null && !notificatonKeyName.isEmpty()) {
			builder.notificationKeyName(notificatonKeyName);
		}
		if (!group.getRegIds().isEmpty()) {
			ArrayList<String> regIds = new ArrayList<String>(group.getRegIds());
			ArrayList<Devices.Device> aux = deviceMngr.getdeviceListSnapshot();
			for (String regId : regIds) {
				if (!regId.isEmpty()) {
					Devices.Device targetDevice = deviceMngr.getDevice(regId);
					builder.addRegId(targetDevice.getName(),
							targetDevice.getRegistrationId());
				}
			}

		}
		GroupManagementMessage message = builder.build();

		try {
			GroupResult result = groupSender.sendCreateGroup(message);
			groupMngr.registerGroup(notificatonKeyName,
					result.getNotificationKey(), result.getRegistrationIds());
			// Save to XML file
			groupMngr.save();
		} catch (IOException e) {
			error = "Error creating group: " + e.toString();
			logger.error(error);
			e.printStackTrace();
		}

		// modelo.addAttribute("error", error);
		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);

	}

	@RequestMapping(value = "/unregisterDeviceFromGroup", method = RequestMethod.GET)
	protected ModelAndView unregisterDeviceFromGroup(
			@RequestParam String notificationKeyName,
			@RequestParam String notificationKey, @RequestParam String regId,
			@RequestParam String regIdName, HttpSession sesion, Model modelo) {

		// Build GroupManagementMessage to be sent
		GroupManagementMessage.Builder builder = new GroupManagementMessage.Builder();
		builder.operation(Constants.GROUP_OPERATION_REMOVE);
		builder.notificationKeyName(notificationKeyName);
		builder.notificationKey(notificationKey);
		// Find details of the target registration Id in the device list
		builder.addRegId(regIdName, regId);

		GroupManagementMessage message = builder.build();

		try {
			GroupResult result = groupSender.sendRemoveFromGroup(message);
			Groups.Group.RegistrationId targetRegId = groupMngr
					.getNewRegistrationId();
			targetRegId.setName(regIdName);
			targetRegId.setValue(regId);
			groupMngr.unregisterDeviceFromGroup(notificationKeyName,
					notificationKey, targetRegId);
			// The last registration id in the group was removed and the group
			// is also removed
			Groups.Group group = groupMngr.getGroup(notificationKey);
			if (group.getRegistrationIdList().size() == 0) {
				groupMngr.unregisterGroup(notificationKeyName, notificationKey);
			}
			// Save to XML file
			groupMngr.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);
	}

	@RequestMapping(value = "/registerDeviceInGroup", method = RequestMethod.POST)
	protected ModelAndView registerDeviceInGroup(
			@ModelAttribute GroupViewModel newGroup, HttpSession sesion,
			Model modelo) {
		GroupManagementMessage.Builder builder = new GroupManagementMessage.Builder();
		builder.operation(Constants.GROUP_OPERATION_ADD);
		builder.notificationKey(newGroup.getNotificationKey());
		String notificationKeyName = groupMngr.getGroup(
				newGroup.getNotificationKey()).getNotificationKeyName();
		builder.notificationKeyName(notificationKeyName);

		if (!newGroup.getRegIds().isEmpty()) {
			ArrayList<String> regIds = new ArrayList<String>(
					newGroup.getRegIds());
			for (String regId : regIds) {
				if (!regId.isEmpty()) {
					Devices.Device targetDevice;
					targetDevice = deviceMngr.getDevice(regId);
					builder.addRegId(targetDevice.getName(),
							targetDevice.getRegistrationId());
				}
			}

		}
		GroupManagementMessage message = builder.build();
		try {
			GroupResult result = groupSender.sendAddToGroup(message);
			Map<String, String> regIdList = message.getRegistrationIds();
			for (Map.Entry<String, String> entry : regIdList.entrySet()) {
				Groups.Group.RegistrationId targetRegId = groupMngr
						.getNewRegistrationId();
				targetRegId.setName(entry.getKey());
				targetRegId.setValue(entry.getValue());
				groupMngr.registerDeviceInGroup(
						result.getNotificationKeyName(),
						result.getNotificationKey(), targetRegId);
			}
			// Save to XML file
			groupMngr.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("Error: " + e.toString());
			e.printStackTrace();
		}

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);

	}
}
