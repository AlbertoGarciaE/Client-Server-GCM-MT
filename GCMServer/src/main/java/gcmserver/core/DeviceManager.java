package gcmserver.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	}

	/**
	 * 
	 * @param device
	 */
	public void registerDevice(Device device) {
		this.deviceList.add(new Device(device));
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
		return new ArrayList<Device>(deviceList);
	}

	@SuppressWarnings("rawtypes")
	public void load(HashMap<String, String> list) {
		Iterator it = list.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry elemento = (Map.Entry) it.next();
			String name = elemento.getKey().toString();
			String regId = elemento.getValue().toString();
			this.deviceList.add(new Device(name, regId));
		}

	}

	public void save() {
		// TODO save list of devices to a persistence system
	}

	public void updateRegistration(String regId, String canonicalRegId) {
		Device aux = new Device("aux", regId);
		int index = this.deviceList.indexOf(aux);
		this.deviceList.get(index).setRegistrationId(canonicalRegId);

	}

}
