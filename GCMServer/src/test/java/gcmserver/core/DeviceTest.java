package gcmserver.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceTest {
	
	private final String name1 = "dev1";
	private final String name2 = "dev2";
	private final String name3 = "dev3";
	private final String regId1 = "1234567890";
	private final String regId2	= "1234567980";

	/**
	 * Test method for {@link gcmserver.core.Device#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Device dev1 = new Device(name1,regId1);
		Device dev2 = new Device(name2, regId1);
		Device dev3 = new Device(name3, regId2);
		assertEquals(dev1, dev2);
		assertNotEquals(dev1, dev3);
	}

}
