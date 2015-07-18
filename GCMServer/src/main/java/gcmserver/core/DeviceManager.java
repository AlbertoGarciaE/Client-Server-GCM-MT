package gcmserver.core;

import java.util.ArrayList;
import java.util.List;

public class DeviceManager {

	// TODO sustituir por lista de objetos device
	private List<Device> deviceList;
	private static DeviceManager instance;

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
		this.deviceList = new ArrayList<Device>();
		load();
	}

	/**
	 * 
	 * @param device
	 */
	public void registerDevice(Device device) {
		this.deviceList.add(device);
	}

	/**
	 * 
	 * @param device
	 */
	public void unregisterDevice(Device device) {
		this.deviceList.remove(device);
	}

	/**
	 * 
	 * @return List<Device>
	 */
	public List<Device> getdeviceList() {
		return deviceList;
	}

	public void load() {
		// TODO load list of devices from a source
	}

	public void save() {
		// TODO save list of devices to a persistence system
	}

	public void updateRegistration(String regId, String canonicalRegId) {
		// TODO change the regId old for a new canonical regId
	}

}
