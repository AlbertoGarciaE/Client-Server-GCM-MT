package gcmserver.controladores;

import gcmserver.core.Device;
import gcmserver.core.DeviceManager;

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
	private Device newDevice;
	private static final Logger logger = LoggerFactory
			.getLogger(DeviceManagementController.class);

	@PostConstruct
	public void init() {

		deviceMngr = DeviceManager.getInstance();
		newDevice = new Device();

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {

		modelo.addAttribute("deviceList", deviceMngr.getdeviceList());
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

		deviceMngr.unregisterDevice(new Device("aux", regId));

		modelo.addAttribute("deviceList", deviceMngr.getdeviceList());
		modelo.addAttribute("newDevice", newDevice);

		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Devices", "modelo", modelo);
	}

	@RequestMapping(value = "/registerDevice", method = RequestMethod.POST)
	protected ModelAndView registerDevice(@ModelAttribute Device newDevice,
			HttpSession sesion, Model modelo) {
		/*
		 * TODO register service
		 */

		deviceMngr.registerDevice(newDevice);

		modelo.addAttribute("deviceList", deviceMngr.getdeviceList());
		modelo.addAttribute("newDevice", newDevice);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Devices", "modelo", modelo);

	}

	@RequestMapping(value = "/createGroup", method = RequestMethod.GET)
	protected void createGroup(@RequestParam String regId, HttpSession sesion,
			Model modelo) {
		/*
		 * TODO create a group of devices under a same notification key
		 */
	}

	@RequestMapping(value = "/addToGroup", method = RequestMethod.GET)
	protected void addToGroup(@RequestParam String regId, HttpSession sesion,
			Model modelo) {
		/*
		 * TODO add a device to a group of devices under a same notification key
		 */
	}

	@RequestMapping(value = "/removeFromGroup", method = RequestMethod.GET)
	protected void removeFromGroup(@RequestParam String regId,
			HttpSession sesion, Model modelo) {
		/*
		 * TODO remove a device from a group of devices under a same
		 * notification key
		 */
	}

}
