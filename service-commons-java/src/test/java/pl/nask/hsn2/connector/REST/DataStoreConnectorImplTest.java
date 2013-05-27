package pl.nask.hsn2.connector.REST;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import mockit.Delegate;
import mockit.NonStrictExpectations;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.protobuff.Resources.Request;

import com.google.protobuf.GeneratedMessage;

public class DataStoreConnectorImplTest {
	private static final String TEST_RESPONSE_HEADER = "test response header";
	private static final String TEST_REQUEST_HEADER = "test request header";
	private static final String TEST_URL_ABSOLUTE = "test url absolute";
	private static final String TEST_URL_ORIGINAL = "test url original";
	private static final int ACTION_GET_SC_OK = 1;
	private static final int ACTION_GET_SC_CREATED = 2;
	private static final int ACTION_GET_PROTOBUF_ATTRIBUTE = 3;
	private static final int ACTION_REST_THROWS_IO_EXCEPTION = 4;
	private static final int ACTION_KEY_NOT_NUMBER = 5;
	private static final long KEY_ID = 63291L;
	private static final long JOB_ID = 95381L;
	private static final String DATA_STORE_HOST = "data-store-host";
	private Set<Integer> actions = new HashSet<>();

	@SuppressWarnings({ "static-access", "rawtypes", "unused" })
	private void mockExternalResources() throws Exception {
		new NonStrictExpectations() {
			RestRequestor rr;
			{
				rr.get(anyString);
				result = rr;
				forEachInvocation = new Object() {
					void validate() throws IOException {
						if (actions.remove(ACTION_REST_THROWS_IO_EXCEPTION)) {
							throw new IOException();
						}
					}
				};

				rr.post(anyString, withInstanceOf(InputStream.class));
				result = rr;

				rr.getResponseMessage();
				result = DATA_STORE_HOST;

				rr.getResponseCode();
				result = new Delegate() {
					int delegate() {
						if (actions.remove(ACTION_GET_SC_OK)) {
							return HttpStatus.SC_OK;
						} else if (actions.remove(ACTION_GET_SC_CREATED)) {
							return HttpStatus.SC_CREATED;
						}
						return -1;
					}
				};

				rr.getInputStream();
				result = new Delegate() {
					InputStream delegate() {
						if (actions.remove(ACTION_GET_PROTOBUF_ATTRIBUTE)) {
							Request req = Request.newBuilder().setRequestUrlOriginal(TEST_URL_ORIGINAL)
									.setRequestUrlAbsolute(TEST_URL_ABSOLUTE).setRequestHeader(TEST_REQUEST_HEADER)
									.setResponseHeader(TEST_RESPONSE_HEADER).setResponseCode(HttpStatus.SC_OK).build();
							return new ByteArrayInputStream(req.toByteArray());
						} else {
							return new ByteArrayInputStream(DATA_STORE_HOST.getBytes());
						}
					}
				};

				rr.getHeaderField(anyString);
				result = new Delegate() {
					String delegate() {
						if (actions.remove(ACTION_KEY_NOT_NUMBER)) {
							return DATA_STORE_HOST;
						} else {
							return String.valueOf(KEY_ID);
						}
					}
				};
				forEachInvocation = new Object() {
					void validate(String headerFieldName) throws IOException {
						Assert.assertEquals(headerFieldName, "Content-ID");
					}
				};
			}
		};
	}

	@BeforeMethod
	private void beforeEachTest() {
		actions.clear();
	}

	// #########################################################
	// #########[ Test methods below ]##########################
	// #########################################################

	@Test
	public void sendGet() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_OK);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		InputStream is = con.sendGet(JOB_ID, KEY_ID);
		String str = IOUtils.toString(is);
		Assert.assertEquals(str, DATA_STORE_HOST);
	}

	@Test
	public void sendGetWithWrongServerAnswer() throws Exception {
		mockExternalResources();
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		InputStream is = con.sendGet(JOB_ID, KEY_ID);
		Assert.assertNull(is);
	}

	@Test
	public void sendPostByteArray() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_CREATED);
		byte[] data = DATA_STORE_HOST.getBytes();
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		DataResponse dr = con.sendPost(data, JOB_ID);
		System.out.println(dr);
		Assert.assertTrue(dr.isSuccesful());
		Assert.assertEquals(dr.getKeyId(), KEY_ID);
		Assert.assertNull(dr.getError());
	}

	@Test
	public void sendPostInputStream() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_CREATED);
		InputStream is = new ByteArrayInputStream(DATA_STORE_HOST.getBytes());
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		DataResponse dr = con.sendPost(is, JOB_ID);
		Assert.assertTrue(dr.isSuccesful());
		Assert.assertEquals(dr.getKeyId(), KEY_ID);
		Assert.assertNull(dr.getError());
	}

	@Test
	public void sendPostInputStreamWithError() throws Exception {
		mockExternalResources();
		InputStream is = new ByteArrayInputStream(DATA_STORE_HOST.getBytes());
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		DataResponse dr = con.sendPost(is, JOB_ID);
		System.out.println(dr);
		Assert.assertFalse(dr.isSuccesful());
		Assert.assertEquals(dr.getKeyId(), -1);
		Assert.assertEquals(dr.getError(), DATA_STORE_HOST);
	}

	@Test(expectedExceptions = NumberFormatException.class)
	public void sendPostInputStreamWithNonNumber() throws Exception {
		mockExternalResources();
		actions.add(ACTION_KEY_NOT_NUMBER);
		actions.add(ACTION_GET_SC_CREATED);
		InputStream is = new ByteArrayInputStream(DATA_STORE_HOST.getBytes());
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		con.sendPost(is, JOB_ID);
	}

	@Test
	public void getResourceAsStream() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_OK);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		InputStream is = con.getResourceAsStream(JOB_ID, KEY_ID);
		String str = IOUtils.toString(is);
		Assert.assertEquals(str, DATA_STORE_HOST);
	}

	@Test(expectedExceptions = ResourceException.class)
	public void getResourceAsStreamWithWrongResponseCode() throws Exception {
		mockExternalResources();
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		con.getResourceAsStream(JOB_ID, KEY_ID);
	}

	@Test(expectedExceptions = StorageException.class)
	public void getResourceAsStreamWithRestConnError() throws Exception {
		mockExternalResources();
		actions.add(ACTION_REST_THROWS_IO_EXCEPTION);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		con.getResourceAsStream(JOB_ID, KEY_ID);
	}

	@Test
	public void getResourceAsMsg() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_OK);
		actions.add(ACTION_GET_PROTOBUF_ATTRIBUTE);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		GeneratedMessage protobufMsg = con.getResourceAsMsg(JOB_ID, KEY_ID, "Request");
		if (protobufMsg instanceof Request) {
			Request req = (Request) protobufMsg;
			Assert.assertEquals(req.getRequestUrlOriginal(), TEST_URL_ORIGINAL);
			Assert.assertEquals(req.getRequestUrlAbsolute(), TEST_URL_ABSOLUTE);
			Assert.assertEquals(req.getRequestHeader(), TEST_REQUEST_HEADER);
			Assert.assertEquals(req.getResponseHeader(), TEST_RESPONSE_HEADER);
			Assert.assertEquals(req.getResponseCode(), HttpStatus.SC_OK);
		} else {
			Assert.fail();
		}
	}

	@Test(expectedExceptions = StorageException.class)
	public void getResourceAsMsgWrongResponseCode() throws Exception {
		mockExternalResources();
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		con.getResourceAsMsg(JOB_ID, KEY_ID, "Request");
	}

	@Test(expectedExceptions = StorageException.class)
	public void getResourceAsMsgRestConnError() throws Exception {
		mockExternalResources();
		actions.add(ACTION_REST_THROWS_IO_EXCEPTION);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		con.getResourceAsMsg(JOB_ID, KEY_ID, "Request");
	}

	@Test(expectedExceptions = StorageException.class)
	public void getResourceAsMsgRestConnError_zzzzz() throws Exception {
		mockExternalResources();
		actions.add(ACTION_REST_THROWS_IO_EXCEPTION);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		con.getResourceAsMsg(JOB_ID, KEY_ID, "NonExist");
	}

	@Test
	public void ping() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_OK);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		boolean ping = con.ping();
		Assert.assertTrue(ping);
	}

	@Test
	public void pingWrongResponseCode() throws Exception {
		mockExternalResources();
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		boolean ping = con.ping();
		Assert.assertFalse(ping);
	}

	@Test
	public void pingRestConnError() throws Exception {
		mockExternalResources();
		actions.add(ACTION_REST_THROWS_IO_EXCEPTION);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		boolean ping = con.ping();
		Assert.assertFalse(ping);
	}
}
