package gcmserver.core;

import gcmserver.model.Datastore;
import gcmserver.model.Groups;
import gcmserver.model.ObjectFactory;
import static gcmserver.core.Constants.GROUP_LIMIT_SIZE;

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

public class GroupManager {

	private final Logger logger = LoggerFactory.getLogger(GroupManager.class);
	// private List<Groups.Group> deviceList;
	private final ObjectFactory factory;
	private Groups groups;
	private static GroupManager instance;
	@Value("${gcm.XMLfile}")
	private String XMLfile = "static/storage/groupList.xml";

	/**
	 * 
	 * @return
	 */
	public static GroupManager getInstance() {

		if (instance == null) {
			return instance = new GroupManager();
		} else {
			return instance;
		}
	}

	/**
	 * 
	 */
	private GroupManager() {
		this.factory = new ObjectFactory();
		this.groups = factory.createGroups();
	}

	/**
	 * 
	 * @param device
	 */
	public void registerGroup(String id) {
		Groups.Group group = factory.createGroupsGroup();
		group.setId(id);
		group.getRegistrationIdList();
		groups.getGroupList().add(group);
		logger.info("Added " + group.toString() + " to list of groups");
	}

	/**
	 * 
	 * @param device
	 */
	public void unregisterGroup(String id) {
		Groups.Group group = factory.createGroupsGroup();
		group.setId(id);
		group.getRegistrationIdList();
		groups.getGroupList().remove(group);
		logger.info("Removed " + group.toString() + " from list of groups");
	}

	/**
	 * 
	 * @param id
	 * @param regId
	 * @throws Exception 
	 */
	public void registerDeviceInGroup(String id, String regId) throws Exception {
		Groups.Group group = factory.createGroupsGroup();
		group.setId(id);
		group.getRegistrationIdList();
		int index = groups.getGroupList().indexOf(group);
		if (groups.getGroupList().get(index).getRegistrationIdList().size() < GROUP_LIMIT_SIZE) {
			groups.getGroupList().get(index).getRegistrationIdList().add(regId);
		} else {
			throw new Exception("Size limit reached for this group");
		}
		logger.info("Added " + regId + " to group " + id);
	}

	/**
	 * 
	 * @param id
	 * @param regId
	 */
	public void unregisterDeviceFromGroup(String id, String regId) {
		Groups.Group group = factory.createGroupsGroup();
		group.setId(id);
		group.getRegistrationIdList();
		int index = groups.getGroupList().indexOf(group);
		groups.getGroupList().get(index).getRegistrationIdList().remove(regId);
		logger.info("Removed " + regId + " from group " + id);
	}

	/**
	 * 
	 * @return List<Device>
	 */
	public ArrayList<Groups.Group> getGroupListSnapshot() {
		logger.info("Returned a snapshot of list of groups");
		return new ArrayList<Groups.Group>(groups.getGroupList());
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
				Groups auxTopics = (Groups) Datastore.unmarshalXMLData(is);
				if (auxTopics.getGroupList() != null) {
					this.groups.getGroupList().clear();
					this.groups.getGroupList().addAll(auxTopics.getGroupList());
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
				Datastore.marshalXML(out, groups);
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
		this.groups.getGroupList().clear();
	}

	public Groups.Group getNewTopic() {
		return factory.createGroupsGroup();
	}

}
