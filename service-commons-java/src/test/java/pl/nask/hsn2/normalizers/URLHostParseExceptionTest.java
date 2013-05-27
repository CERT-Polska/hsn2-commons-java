package pl.nask.hsn2.normalizers;

import org.testng.Assert;
import org.testng.annotations.Test;

public class URLHostParseExceptionTest {
	private static final String TEST_MSG = "test-msg";
	private static final String TEST_MSG2 = "test-msg2";

	@Test
	public void urlHostParseExceptionTest() {
		URLHostParseException e = new URLHostParseException();
		Assert.assertEquals(e.getMessage(), null);
		Assert.assertEquals(e.getCause(), null);
		
		e = new URLHostParseException(TEST_MSG);
		Assert.assertEquals(e.getMessage(), TEST_MSG);
		Assert.assertEquals(e.getCause(), null);
		
		NullPointerException e2 = new NullPointerException(TEST_MSG2);
		
		e = new URLHostParseException(TEST_MSG, e2);
		Assert.assertEquals(e.getMessage(), TEST_MSG);
		Assert.assertEquals(e.getCause(), e2);
		Assert.assertEquals(e.getCause().getMessage(), TEST_MSG2);
	}
}
