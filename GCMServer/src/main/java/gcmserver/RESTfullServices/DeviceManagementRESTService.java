package gcmserver.RESTfullServices;

import gcmserver.controllers.model.DeviceViewModel;
import gcmserver.core.DeviceManager;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Usuario
 *
 */

@RestController
@RequestMapping("/RESTService")
public class DeviceManagementRESTService {

	private DeviceManager deviceMngr;
	private static final Logger logger = LoggerFactory
			.getLogger(DeviceManagementRESTService.class);

	@PostConstruct
	public void init() {

		deviceMngr = DeviceManager.getInstance();
		// Load devices stored in XML file
		if (deviceMngr.getdeviceListSnapshot().isEmpty()) {
			deviceMngr.load();
		}

	}

	@RequestMapping(value = "/unregisterDevice", method = RequestMethod.POST)
	protected JSONObject unregisterDevice(@RequestParam String regId,
			HttpSession sesion) {

		Boolean successAdding = deviceMngr.unregisterDevice(regId);
		// Save to XML file
		Boolean successSaving = deviceMngr.save();

		JSONObject response = new JSONObject();
		String result = successAdding && successSaving ? "success" : "failure";
		response.put("result", result);
		String message = successAdding && successSaving ? "New device removed and saved correctly"
				: "Error trying to remove/save the new device";
		response.put("message", message);
		logger.info("REST response: " + response.toJSONString());
		return response;
	}

	@RequestMapping(value = "/registerDevice", method = RequestMethod.POST)
	protected JSONObject registerDevice(@RequestParam String regId,
			@RequestParam String deviceName) {

		Boolean successAdding = deviceMngr.registerDevice(deviceName, regId);
		// Save to XML file
		Boolean successSaving = deviceMngr.save();

		JSONObject response = new JSONObject();
		String result = successAdding && successSaving ? "success" : "failure";
		response.put("result", result);
		String message = successAdding && successSaving ? "New device added and saved correctly"
				: "Error trying to add/save the new device";
		response.put("message", message);
		logger.info("REST response: " + response.toJSONString());
		return response;

	}

}
