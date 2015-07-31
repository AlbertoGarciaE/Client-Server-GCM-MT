package gcmserver.controllers;

import java.util.ArrayList;

import gcmserver.controllers.model.GroupViewModel;
import gcmserver.controllers.model.TopicViewModel;
import gcmserver.core.DeviceManager;
import gcmserver.core.GroupManager;
import gcmserver.core.TopicManager;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory
			.getLogger(DeviceManagementController.class);

	@PostConstruct
	public void init() {

		deviceMngr = DeviceManager.getInstance();
		groupMngr = GroupManager.getInstance();
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
	protected ModelAndView unregisterGroup(@RequestParam String id,
			HttpSession sesion, Model modelo) {

		groupMngr.unregisterGroup(id);
		// Save to XML file
		groupMngr.save();

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);
	}

	@RequestMapping(value = "/registerGroup", method = RequestMethod.GET)
	protected ModelAndView registerGroup(@RequestParam String id,
			HttpSession sesion, Model modelo) {

		groupMngr.registerGroup(id);
		// Save to XML file
		groupMngr.save();

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);

	}

	@RequestMapping(value = "/unregisterDeviceFromGroup", method = RequestMethod.GET)
	protected ModelAndView unregisterDeviceFromGroup(@RequestParam String id,
			@RequestParam String regId, HttpSession sesion, Model modelo) {

		groupMngr.unregisterDeviceFromGroup(id, regId);
		// Save to XML file
		groupMngr.save();

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("groupList", groupMngr.getGroupListSnapshot());
		modelo.addAttribute("newGroup", newGroup);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Groups", "modelo", modelo);
	}

	@RequestMapping(value = "/registerDeviceInGroup", method = RequestMethod.POST)
	protected ModelAndView registerDeviceInGroup(@RequestParam String id,
			@ModelAttribute GroupViewModel newGroup, HttpSession sesion,
			Model modelo) {

		try {

			groupMngr.registerDeviceInGroup(newGroup.getId(), newGroup
					.getRegIds().get(0));
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
