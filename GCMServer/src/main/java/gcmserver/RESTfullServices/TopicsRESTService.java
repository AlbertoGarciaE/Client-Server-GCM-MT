package gcmserver.RESTfullServices;

import java.util.ArrayList;

import gcmserver.controllers.model.DeviceViewModel;
import gcmserver.core.DeviceManager;
import gcmserver.core.TopicManager;
import gcmserver.model.Topics.Topic;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

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
public class TopicsRESTService {

	private TopicManager topicMngr;
	private static final Logger logger = LoggerFactory
			.getLogger(TopicsRESTService.class);

	@PostConstruct
	public void init() {

		topicMngr = TopicManager.getInstance();
		// Load devices stored in XML file
		if (topicMngr.getTopicsListSnapshot().isEmpty()) {
			topicMngr.load();
		}

	}

	@RequestMapping(value = "/getTopicList", method = RequestMethod.GET)
	protected ArrayList<Topic> unregisterDevice() {

		logger.info("Topic list delivered");
		return topicMngr.getTopicsListSnapshot();
	}

}
