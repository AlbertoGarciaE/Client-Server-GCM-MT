package gcmserver.core;

import static org.junit.Assert.*;
import gcmserver.controllers.model.DeviceViewModel;
import gcmserver.model.Devices;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RunWith(MockitoJUnitRunner.class)
public class DeviceManagerTest {

	private final String regId1 = "1234567890";
	private final String regId2 = "0987654321";
	private final String canonicalId = "2222222222";
	private final String name1 = "dev1";
	private final String name2 = "dev2";
	private final String name3 = "canoDev";
	private final DeviceManager instance1 = DeviceManager.getInstance();
	private final Devices.Device dev1 = instance1.getNewDevice();
	private final Devices.Device dev2 = instance1.getNewDevice();
	private final Devices.Device canoDev = instance1.getNewDevice();

	@Before
	public void setUp() throws Exception {
		dev1.setName(name1);
		dev1.setRegistrationId(regId1);
		dev2.setName(name2);
		dev2.setRegistrationId(regId2);
		canoDev.setName(name3);
		canoDev.setRegistrationId(canonicalId);
		instance1.clearList();
		instance1.registerDevice(name1, regId1);
		instance1.save();
	}

	@Test
	public void testGetInstance() {
		DeviceManager instance2 = DeviceManager.getInstance();
		assertEquals(instance1, instance2);
	}

	@Test
	public void testRegisterDevice() {
		instance1.registerDevice(name2, regId2);
		assertTrue(instance1.getdeviceListSnapshot().contains(dev2));
	}

	@Test
	public void testUnregisterDevice() {
		instance1.unregisterDevice(regId1);
		assertFalse(instance1.getdeviceListSnapshot().contains(dev1));
	}

	@Test
	public void testUpdateRegistration() {
		instance1.updateRegistration(regId1, canonicalId);
		assertTrue(instance1.getdeviceListSnapshot().contains(canoDev));
		assertFalse(instance1.getdeviceListSnapshot().contains(dev1));
	}

	@Test
	public void testLoad() {
		instance1.load();
		assertTrue(instance1.getdeviceListSnapshot().size() > 0);
	}

	@Test
	public void testSave() {
		instance1.load();
		int sizeBefore = instance1.getdeviceListSnapshot().size();
		instance1.registerDevice(name1, regId1);
		instance1.save();
		instance1.registerDevice(name1, regId1);
		instance1.load();
		int sizeAfter = instance1.getdeviceListSnapshot().size();
		assertEquals(sizeBefore + 1, sizeAfter);
	}

	@Test
	public void testSaveAndLoadEmptyDeviceList() {
		instance1.clearList();
		Boolean resultSave = instance1.save();
		instance1.registerDevice(name1, regId1);
		// Size = 1
		int sizeBefore = instance1.getdeviceListSnapshot().size();
		Boolean resultLoad = instance1.load();
		// Size = 0
		int sizeAfter = instance1.getdeviceListSnapshot().size();
		assertEquals(sizeBefore, sizeAfter + 1);
		assertTrue(resultLoad);
		assertTrue(resultSave);
	}

}
