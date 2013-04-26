package pl.nask.hsn2.normalizers;

import org.testng.Assert;
import org.testng.annotations.Test;

public class URLMalformedInputExceptionTest {
	private static final String TEST_MSG = "test-msg";
	private static final String TEST_MSG2 = "test-msg2";

	@Test
	public void urlMalformedInputExceptionTest() {
		URLMalformedInputException e = new URLMalformedInputException();
		Assert.assertEquals(e.getMessage(), null);
		Assert.assertEquals(e.getCause(), null);

		e = new URLMalformedInputException(TEST_MSG);
		Assert.assertEquals(e.getMessage(), TEST_MSG);
		Assert.assertEquals(e.getCause(), null);

		NullPointerException e2 = new NullPointerException(TEST_MSG2);

		e = new URLMalformedInputException(e2);
		Assert.assertEquals(e.getMessage(), e2.toString());
		Assert.assertEquals(e.getCause(), e2);

		e = new URLMalformedInputException(TEST_MSG, e2);
		Assert.assertEquals(e.getMessage(), TEST_MSG);
		Assert.assertEquals(e.getCause(), e2);
		Assert.assertEquals(e.getCause().getMessage(), TEST_MSG2);
	}
}
