package gcmserver.controllers;

import gcmserver.controllers.model.TopicViewModel;
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
@RequestMapping("/Topics")
public class TopicManagementController {

	private TopicManager topicMngr;
	private TopicViewModel newTopic;
	private static final Logger logger = LoggerFactory
			.getLogger(DeviceManagementController.class);

	@PostConstruct
	public void init() {

		topicMngr = TopicManager.getInstance();
		// Load devices stored in XML file
		topicMngr.load();
		newTopic = new TopicViewModel();

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Model modelo) {

		modelo.addAttribute("topicList", topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("newTopic", newTopic);

		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Topics", "modelo", modelo);
	}

	@RequestMapping(value = "/unregisterTopic", method = RequestMethod.GET)
	protected ModelAndView unregisterDevice(@RequestParam String url,
			HttpSession sesion, Model modelo) {
		/*
		 * TODO Unregister service
		 */

		topicMngr.unregisterTopic(url);

		modelo.addAttribute("topicList", topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("newTopic", newTopic);
		// Save to XML file
		topicMngr.save();

		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Topics", "modelo", modelo);
	}

	@RequestMapping(value = "/registerTopic", method = RequestMethod.POST)
	protected ModelAndView registerDevice(
			@ModelAttribute TopicViewModel newTopic, HttpSession sesion,
			Model modelo) {
		/*
		 * TODO register service
		 */

		topicMngr.registerTopic(newTopic.getName(),
				newTopic.getUrl());
		// Save to XML file
		topicMngr.save();

		modelo.addAttribute("topicList", topicMngr.getTopicsListSnapshot());
		modelo.addAttribute("newTopic", newTopic);
		logger.info("Modelo: " + modelo.toString());
		return new ModelAndView("Topics", "modelo", modelo);

	}

}
