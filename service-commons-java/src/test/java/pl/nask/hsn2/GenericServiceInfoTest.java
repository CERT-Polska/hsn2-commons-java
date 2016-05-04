package pl.nask.hsn2;

import java.lang.reflect.Constructor;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GenericServiceInfoTest {
	private static final String SERVICE_NAME = "service-name-123";

	@Test
	public void setterAndGetter() {
		GenericServiceInfo.setServiceName(SERVICE_NAME);
		Assert.assertEquals(GenericServiceInfo.getServiceName(), SERVICE_NAME);
	}

	/**
	 * Class is utility, so it shouldn't be able to instantiate.
	 * 
	 * @throws Exception
	 */
	@Test
	public void cantInstantiate() throws Exception {
		Constructor<?>[] constructors = GenericServiceInfo.class.getDeclaredConstructors();
		Assert.assertEquals(constructors.length, 1);
		Assert.assertFalse(constructors[0].isAccessible());
		constructors[0].setAccessible(true);
		Assert.assertEquals(constructors[0].newInstance().getClass(), GenericServiceInfo.class);
	}

	@Test
	public void getServicePath() throws Exception {
		String classPathSuffix = "service-commons-java" + System.getProperty("file.separator") + "target";

		// workaround to handle Cobertura target directory
		if (GenericServiceInfo.getServicePath(GenericServiceInfo.class).endsWith("/generated-classes")) {
			classPathSuffix = classPathSuffix.concat("/generated-classes");
		}

		Assert.assertTrue(GenericServiceInfo.getServicePath(GenericServiceInfo.class).endsWith(classPathSuffix));
	}
}
