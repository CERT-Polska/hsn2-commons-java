package pl.nask.hsn2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.Assert;

import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.normalizers.URLParseException;

public class ExceptionsTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionsTest.class);
	private static final String MSG = "testMsg";
	private static final String MSG2 = "testMsg2";
	private static final NullPointerException NPE = new NullPointerException(MSG2);

	@Test
	public void testContextSizeLimitExceeded() {
		ContextSizeLimitExceeded e = new ContextSizeLimitExceeded(MSG);
		Assert.assertEquals(e.getMessage(), MSG);
		ContextSizeLimitExceeded e2 = new ContextSizeLimitExceeded(MSG, NPE);
		Assert.assertEquals(e2.getCause(), NPE);
		LOGGER.info("testContextSizeLimitExceeded passed");
	}

	@Test
	public void testInputDataException() {
		InputDataException e = new InputDataException(MSG);
		Assert.assertEquals(e.getMessage(), MSG);
		InputDataException e2 = new InputDataException(MSG, NPE);
		Assert.assertEquals(e2.getCause(), NPE);
		LOGGER.info("testInputDataException passed");
	}

	@Test
	public void testParameterException() {
		ParameterException e = new ParameterException(MSG);
		Assert.assertEquals(e.getMessage(), MSG);
		ParameterException e2 = new ParameterException(MSG, NPE);
		Assert.assertEquals(e2.getCause(), NPE);
		LOGGER.info("testParameterException passed");
	}

	@Test
	void testRequiredParameterMissingException() {
		RequiredParameterMissingException e = new RequiredParameterMissingException(MSG);
		Assert.assertEquals(e.getMessage(), "No parameter with name=" + MSG + " found");
		Assert.assertEquals(e.getParamName(), MSG);
		LOGGER.info("testRequiredParameterMissingException passed");
	}

	@Test
	public void testResourceException() {
		ResourceException e = new ResourceException(MSG);
		Assert.assertEquals(e.getMessage(), MSG);
		ResourceException e2 = new ResourceException(MSG, NPE);
		Assert.assertEquals(e2.getCause(), NPE);
		LOGGER.info("testResourceException passed");
	}

	@Test
	public void testStorageException() {
		StorageException e = new StorageException(MSG);
		Assert.assertEquals(e.getMessage(), MSG);
		StorageException e2 = new StorageException(MSG, NPE);
		Assert.assertEquals(e2.getCause(), NPE);
		LOGGER.info("testStorageException passed");
	}

	@Test
	public void testBusException() {
		BusException e = new BusException(MSG);
		Assert.assertEquals(e.getMessage(), MSG);
		BusException e2 = new BusException(MSG, NPE);
		Assert.assertEquals(e2.getCause(), NPE);
		BusException e3 = new BusException(NPE);
		Assert.assertEquals(e3.getCause(), NPE);
		BusException e4 = new BusException();
		Assert.assertEquals(e4.getMessage(), null);
		Assert.assertEquals(e4.getCause(), null);
		LOGGER.info("testBusException passed");
	}

	@Test
	public void testURLParseException() {
		URLParseException e = new URLParseException(MSG);
		Assert.assertEquals(e.getMessage(), MSG);
		URLParseException e2 = new URLParseException(MSG, NPE);
		Assert.assertEquals(e2.getCause(), NPE);
		URLParseException e4 = new URLParseException();
		Assert.assertEquals(e4.getMessage(), null);
		Assert.assertEquals(e4.getCause(), null);
		LOGGER.info("testURLParseException passed");
	}
}
