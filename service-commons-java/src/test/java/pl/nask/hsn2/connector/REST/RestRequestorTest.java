package pl.nask.hsn2.connector.REST;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mockit.Delegate;
import mockit.Expectations;

import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings({ "rawtypes", "unused" })
public class RestRequestorTest {
	private static final String URL = "http://test-host/path/";
	private static final String HEADER_FIELD = "header-field";
	private static final String HEADER_FIELD_VALUE = "header-field-value";
	private static final String RESPONSE_MESSAGE = "connection-response-message";
	private static final String DATA = "test-data";
	private static final String ERROR = "test-error";
	private static final int RESPONSE_CODE = 872945;
	private static final String CONTENT_TYPE = "content-type";

	@Test
	public void getGeneralInfo() throws Exception {
		new Expectations() {
			URL url;
			HttpURLConnection connection;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = connection;

				connection.getHeaderField(HEADER_FIELD);
				result = HEADER_FIELD_VALUE;

				connection.getResponseMessage();
				result = RESPONSE_MESSAGE;

				connection.getResponseCode();
				result = RESPONSE_CODE;
			}
		};
		RestRequestor rr = RestRequestor.get(URL);
		String hf = rr.getHeaderField(HEADER_FIELD);
		String rm = rr.getResponseMessage();
		int rc = rr.getResponseCode();
		Assert.assertEquals(hf, HEADER_FIELD_VALUE);
		Assert.assertEquals(rm, RESPONSE_MESSAGE);
		Assert.assertEquals(rc, RESPONSE_CODE);
	}

	@Test
	public void getDataAndErrorMsg() throws Exception {
		new Expectations() {
			URL url;
			HttpURLConnection connection;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = connection;

				connection.getInputStream();
				result = new Delegate() {
					InputStream delegate() {
						return new ByteArrayInputStream(DATA.getBytes());
					}
				};

				connection.getErrorStream();
				result = new Delegate() {
					InputStream delegate() {
						return new ByteArrayInputStream(ERROR.getBytes());
					}
				};
			}
		};
		RestRequestor rr = RestRequestor.get(URL);
		try (InputStream input = rr.getInputStream()) {
			String data = IOUtils.toString(input);
			Assert.assertEquals(data, DATA);
		}
		try (InputStream input = rr.getErrorStream()) {
			String error = IOUtils.toString(input);
			Assert.assertEquals(error, ERROR);
		}
	}

	@Test
	public void put() throws Exception {
		new Expectations() {
			ByteArrayOutputStream outStream;
			URL url;
			HttpURLConnection connection;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = connection;

				connection.setDoOutput(true);

				connection.setRequestMethod("PUT");

				connection.setRequestProperty("Content-Type", CONTENT_TYPE);

				connection.setRequestProperty("Content-Length", String.valueOf(DATA.getBytes().length));

				connection.getOutputStream();
				result = new Delegate() {
					OutputStream delegate() {
						if (outStream == null) {
							outStream = new ByteArrayOutputStream();
						}
						return outStream;
					}
				};
				times = 3;
			}
		};
		InputStream dataInputStream = new ByteArrayInputStream(DATA.getBytes());
		RestRequestor rr = RestRequestor.put(URL, CONTENT_TYPE, DATA.getBytes());
	}

	@Test(expectedExceptions = IOException.class)
	public void putWithIoException() throws Exception {
		new Expectations() {
			URL url;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = new IOException();
			}
		};
		InputStream dataInputStream = new ByteArrayInputStream(DATA.getBytes());
		RestRequestor.put(URL, CONTENT_TYPE, DATA.getBytes());
	}

	@Test
	public void post() throws Exception {
		new Expectations() {
			URL url;
			HttpURLConnection con;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = con;

				con.setRequestMethod("POST");

				con.setDoOutput(true);

				con.getOutputStream();
				result = new ByteArrayOutputStream();
			}
		};
		ByteArrayInputStream is = new ByteArrayInputStream(URL.getBytes());
		RestRequestor.post(URL, is);
	}

	@Test
	public void head() throws Exception {
		new Expectations() {
			URL url;
			HttpURLConnection con;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = con;

				con.setRequestMethod("HEAD");
			}
		};
		RestRequestor rr = RestRequestor.head(URL);
	}

	@Test
	public void close() throws IOException {
		new Expectations() {
			URL url;
			HttpURLConnection con;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = con;

				con.getInputStream();

				con.getOutputStream();

				con.getErrorStream();

				con.disconnect();
			}
		};
		RestRequestor rr = RestRequestor.get(URL);
		rr.close();
	}

	@Test
	public void closeWithExceptions() throws IOException {
		new Expectations() {
			URL url;
			HttpURLConnection con;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = con;

				con.getInputStream();
				result = new IOException();

				con.getOutputStream();
				result = new IOException();

				con.getErrorStream();

				con.disconnect();
			}
		};
		RestRequestor rr = RestRequestor.get(URL);
		rr.close();
	}

	@Test
	public void closeNullConnection() throws IOException {
		new Expectations() {
			URL url;
			HttpURLConnection con;
			{
				newInstance("java.net.URL", URL);

				url.openConnection();
				result = null;
			}
		};
		RestRequestor rr = RestRequestor.get(URL);
		rr.close();
	}
}
