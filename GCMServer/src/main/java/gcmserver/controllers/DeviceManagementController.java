package gcmserver.controllers;

import gcmserver.controllers.model.DeviceViewModel;
import gcmserver.core.DeviceManager;
import gcmserver.model.Devices;

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

/**
 * 
 * @author Usuario
 *
 */

@Controller
@RequestMapping("/deviceManagement")
public class DeviceManagementController {

	private DeviceManager deviceMngr;
	private DeviceViewModel newDevice;
	private static final Logger logger = LoggerFactory
			.getLogger(DeviceManagementController.class);

	@PostConstruct
	public void init() {

		deviceMngr = DeviceManager.getInstance();
		// Load devices stored in XML file
		deviceMngr.load();
		newDevice = new DeviceViewModel();

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("newDevice", newDevice);

		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Devices", "modelo", modelo);
	}

	@RequestMapping(value = "/unregisterDevice", method = RequestMethod.GET)
	protected ModelAndView unregisterDevice(@RequestParam String regId,
			HttpSession sesion, Model modelo) {
		/*
		 * TODO Unregister service
		 */

		deviceMngr.unregisterDevice(regId);

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("newDevice", newDevice);
		// Save to XML file
		deviceMngr.save();

		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Devices", "modelo", modelo);
	}

	@RequestMapping(value = "/registerDevice", method = RequestMethod.POST)
	protected ModelAndView registerDevice(
			@ModelAttribute DeviceViewModel newDevice, HttpSession sesion,
			Model modelo) {
		/*
		 * TODO register service
		 */

		deviceMngr.registerDevice(newDevice.getName(),
				newDevice.getRegistrationId());
		// Save to XML file
		deviceMngr.save();

		modelo.addAttribute("deviceList", deviceMngr.getdeviceListSnapshot());
		modelo.addAttribute("newDevice", newDevice);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Devices", "modelo", modelo);

	}

}
