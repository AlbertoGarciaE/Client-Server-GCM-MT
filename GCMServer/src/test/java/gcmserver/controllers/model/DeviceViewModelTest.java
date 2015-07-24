package gcmserver.controllers.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import gcmserver.controllers.model.DeviceViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceViewModelTest {
	
	private final String name1 = "dev1";
	private final String name2 = "dev2";
	private final String name3 = "dev3";
	private final String regId1 = "1234567890";
	private final String regId2	= "1234567980";

	/**
	 * Test method for {@link gcmserver.controllers.model.DeviceViewModel#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		DeviceViewModel dev1 = new DeviceViewModel(name1,regId1);
		DeviceViewModel dev2 = new DeviceViewModel(name2, regId1);
		DeviceViewModel dev3 = new DeviceViewModel(name3, regId2);
		assertEquals(dev1, dev2);
		assertNotEquals(dev1, dev3);
	}

}
