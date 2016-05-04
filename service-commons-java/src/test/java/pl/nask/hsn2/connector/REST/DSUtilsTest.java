package pl.nask.hsn2.connector.REST;

import java.lang.reflect.Constructor;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DSUtilsTest {
	@Test
	public void cantInstantiate() throws Exception {
		Constructor<?>[] constructors = DSUtils.class.getDeclaredConstructors();
		Assert.assertEquals(constructors.length, 1);
		Assert.assertFalse(constructors[0].isAccessible());
		constructors[0].setAccessible(true);
		Assert.assertEquals(constructors[0].newInstance().getClass(), DSUtils.class);
	}

	@Test
	public void testGetAddress() {
		String address = DSUtils.dsAddress("http://localhost:8080/", 1, 1);
		Assert.assertEquals(address, "http://localhost:8080/data/1/1");
	}

	@Test
	public void testPostAddress() {
		String address = DSUtils.dsAddress("http://localhost:8080/", 1);
		Assert.assertEquals(address, "http://localhost:8080/data/1");
	}
}
