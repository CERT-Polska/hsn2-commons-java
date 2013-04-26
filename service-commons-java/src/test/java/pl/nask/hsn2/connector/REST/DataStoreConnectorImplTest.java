package pl.nask.hsn2.connector.REST;

import java.io.ByteArrayInputStream;
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

import com.google.protobuf.GeneratedMessage;

public class DataStoreConnectorImplTest {
	private static final int ACTION_GET_SC_OK = 1;
	private static final int ACTION_GET_SC_CREATED = 2;
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
				result = new ByteArrayInputStream(DATA_STORE_HOST.getBytes());

				rr.getHeaderField(anyString);
				result = String.valueOf(KEY_ID);
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
	public void sendPostByteArray() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_CREATED);
		byte[] data = DATA_STORE_HOST.getBytes();
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		DataResponse dr = con.sendPost(data, JOB_ID);
		System.out.println(dr);
		Assert.assertTrue(dr.isSuccesful());
		Assert.assertEquals(dr.getKeyId(), KEY_ID);
		Assert.assertEquals(dr.getError(), null);
	}

	@Test
	public void sendPostInputStream() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_CREATED);
		InputStream is = new ByteArrayInputStream(DATA_STORE_HOST.getBytes());
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		DataResponse dr = con.sendPost(is, JOB_ID);
		System.out.println(dr);
		Assert.assertTrue(dr.isSuccesful());
		Assert.assertEquals(dr.getKeyId(), KEY_ID);
		Assert.assertEquals(dr.getError(), null);
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
	
	@Test
	public void getResourceAsMsg() throws Exception {
		mockExternalResources();
		actions.add(ACTION_GET_SC_OK);
		DataStoreConnectorImpl con = new DataStoreConnectorImpl(DATA_STORE_HOST);
		GeneratedMessage protobufMsg = con.getResourceAsMsg(JOB_ID, KEY_ID, "Attribute");
	}

	@Test
	public void buildMessageObject() {
	}

	@Test
	public void ping() {
	}

}
