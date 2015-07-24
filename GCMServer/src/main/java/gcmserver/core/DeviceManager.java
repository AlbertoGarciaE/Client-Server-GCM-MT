package gcmserver.core;

import gcmserver.model.Datastore;
import gcmserver.model.Devices;
import gcmserver.model.ObjectFactory;

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

public class DeviceManager {

	private final Logger logger = LoggerFactory.getLogger(DeviceManager.class);
	// private List<Devices.Device> deviceList;
	private final ObjectFactory factory;
	private Devices devices;
	private static DeviceManager instance;
	@Value("${gcm.XMLfile}")
	private String XMLfile = "static/storage/deviceList.xml";

	/**
	 * 
	 * @return
	 */
	public static DeviceManager getInstance() {

		if (instance == null) {
			return instance = new DeviceManager();
		} else {
			return instance;
		}
	}

	/**
	 * 
	 */
	private DeviceManager() {
		this.factory = new ObjectFactory();
		this.devices = factory.createDevices();
	}

	/**
	 * 
	 * @param device
	 */
	public void registerDevice(String name, String regId) {
		Devices.Device device = factory.createDevicesDevice();
		device.setName(name);
		device.setRegistrationId(regId);
		devices.getDeviceList().add(device);
		logger.info("Added " + device.toString() + " to list of de devices");
	}

	/**
	 * 
	 * @param device
	 */
	public void unregisterDevice(String regId) {
		Devices.Device device = factory.createDevicesDevice();
		device.setName("erase");
		device.setRegistrationId(regId);
		devices.getDeviceList().remove(device);
		logger.info("Removed " + device.toString() + " from list of de devices");
	}

	/**
	 * 
	 * @return List<Device>
	 */
	public ArrayList<Devices.Device> getdeviceListSnapshot() {
		logger.info("Returned a snapshot of list of de devices");
		return new ArrayList<Devices.Device>(devices.getDeviceList());
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
				Devices auxDevices = (Devices) Datastore.unmarshalXMLData(is);
				if (auxDevices.getDeviceList() != null) {
					this.devices.getDeviceList().clear();
					this.devices.getDeviceList().addAll(
							auxDevices.getDeviceList());
				}
				logger.info("List of devices readed from file "
						+ path.toString());
				result = true;
			} else {
				logger.error("List of devices could not be readed from file "
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
				Datastore.marshalXML(out, devices);
				logger.info("List of devices persisted to file "
						+ path.toString());
				result = true;
			} else {
				logger.error("List of devices could not be saved to file "
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

	public void updateRegistration(String regId, String canonicalRegId) {
		Devices.Device device = factory.createDevicesDevice();
		device.setName("update");
		device.setRegistrationId(regId);
		int index = devices.getDeviceList().indexOf(device);
		devices.getDeviceList().get(index).setRegistrationId(canonicalRegId);
	}

	public void clearList() {
		this.devices.getDeviceList().clear();
	}

	public Devices.Device getNewDevice() {
		return factory.createDevicesDevice();
	}

}
