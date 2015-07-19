package gcmserver.core;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceManagerTest {

	private final String regId = "1234567890";
	private final String canonicalId = "2222222222";
	private final Device dev1 = new Device("dev1", regId);
	private final Device dev2 = new Device("dev1", canonicalId);
	private final DeviceManager instance1 = DeviceManager.getInstance();
	private final HashMap<String, String> list = new HashMap<String, String>();

	@Test
	public void testGetInstance() {
		DeviceManager instance2 = DeviceManager.getInstance();
		assertEquals(instance1, instance2);
	}

	@Test
	public void testRegisterDevice() {
		instance1.registerDevice(dev1);
		assertTrue(instance1.getdeviceList().contains(dev1));
	}

	@Test
	public void testUnregisterDevice() {
		instance1.unregisterDevice(dev1);
		assertFalse(instance1.getdeviceList().contains(dev1));
	}

	@Test
	public void testUpdateRegistration() {
		instance1.registerDevice(dev1);
		instance1.updateRegistration(regId, canonicalId);
		assertTrue(instance1.getdeviceList().contains(dev2));
		assertFalse(instance1.getdeviceList().contains(dev1));
	}

	@Test
	public void testLoad() {
		list.put("dev4", "000000000");
		list.put("dev5", "111111111");
		list.put("dev6", "333333333");

		instance1.load(list);
		assertEquals(3, instance1.getdeviceList().size());
	}
}
