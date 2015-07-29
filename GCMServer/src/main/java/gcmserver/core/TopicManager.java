package gcmserver.core;

import gcmserver.model.Datastore;
import gcmserver.model.ObjectFactory;
import gcmserver.model.Topics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class TopicManager {

	private final Logger logger = LoggerFactory.getLogger(TopicManager.class);
	// private List<Topics.Topic> deviceList;
	private final ObjectFactory factory;
	private Topics topics;
	private static TopicManager instance;
	@Value("${gcm.XMLfile}")
	private String XMLfile = "static/storage/topicList.xml";

	/**
	 * 
	 * @return
	 */
	public static TopicManager getInstance() {

		if (instance == null) {
			return instance = new TopicManager();
		} else {
			return instance;
		}
	}

	/**
	 * 
	 */
	private TopicManager() {
		this.factory = new ObjectFactory();
		this.topics = factory.createTopics();
	}

	/**
	 * 
	 * @param device
	 */
	public void registerTopic(String name, String url) {
		Topics.Topic topic = factory.createTopicsTopic();
		topic.setName(name);
		topic.setUrl(url);
		topics.getTopicsList().add(topic);
		logger.info("Added " + topic.toString() + " to list of topics");
	}

	/**
	 * 
	 * @param device
	 */
	public void unregisterTopic(String url) {
		Topics.Topic topic = factory.createTopicsTopic();
		topic.setName("remove");
		topic.setUrl(url);
		topics.getTopicsList().remove(topic);
		logger.info("Removed " + topic.toString() + " from list of topics");
	}

	/**
	 * 
	 * @return List<Device>
	 */
	public ArrayList<Topics.Topic> getTopicsListSnapshot() {
		logger.info("Returned a snapshot of list of topics");
		return new ArrayList<Topics.Topic>(topics.getTopicsList());
	}

	/**
	 * 
	 * @return
	 */
	public boolean load() {

		boolean result = false;
		// XML file reader
		String path = null;
		URL url = null;
		try {
			// Get file from resources folder
			ClassLoader classLoader = getClass().getClassLoader();
			url = classLoader.getResource(XMLfile);
			if (url != null) {
				path = url.getFile();
				InputStream is = new FileInputStream(path);
				Topics auxTopics = (Topics) Datastore.unmarshalXMLData(is);
				if (auxTopics.getTopicsList() != null) {
					this.topics.getTopicsList().clear();
					this.topics.getTopicsList().addAll(
							auxTopics.getTopicsList());
				}
				logger.info("List of topics readed from file "
						+ path.toString());
				result = true;
			} else {
				logger.error("List of topics could not be readed from file "
						+ XMLfile);
				result = false;
			}
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException trying to open path "
					+ path.toString() + ". Error trace: " + e.toString());
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public boolean save() {

		boolean result = false;
		// XML file writer
		String path = null;
		URL url = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			url = classLoader.getResource(XMLfile);
			if (url != null) {
				path = url.getFile();
				Writer writer = new FileWriter(path);
				PrintWriter out = new PrintWriter(writer);
				Datastore.marshalXML(out, topics);
				logger.info("List of topics persisted to file "
						+ path.toString());
				result = true;
			} else {
				logger.error("List of topics could not be saved to file "
						+ XMLfile);
				result = false;
			}
		} catch (IOException e) {
			logger.error("IOException trying to open path " + path.toString()
					+ ". Error trace: " + e.toString());
			e.printStackTrace();
			result = false;
		}
		return result;

	}

	public void clearList() {
		this.topics.getTopicsList().clear();
	}

	public Topics.Topic getNewTopic() {
		return factory.createTopicsTopic();
	}

}
