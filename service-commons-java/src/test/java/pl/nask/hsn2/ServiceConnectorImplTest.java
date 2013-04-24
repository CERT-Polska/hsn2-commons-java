package pl.nask.hsn2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mockit.Delegate;
import mockit.NonStrictExpectations;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;
import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.AMQP.InConnector;
import pl.nask.hsn2.connector.AMQP.ObjectStoreConnectorImpl;
import pl.nask.hsn2.connector.REST.DataStoreConnectorImpl;
import pl.nask.hsn2.protobuff.DataStore.DataResponse;
import pl.nask.hsn2.protobuff.Object.Attribute;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.Object.Reference;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.ResponseType;
import pl.nask.hsn2.protobuff.Process.TaskAccepted;
import pl.nask.hsn2.protobuff.Process.TaskCompleted;
import pl.nask.hsn2.protobuff.Process.TaskError;
import pl.nask.hsn2.protobuff.Process.TaskError.ReasonType;
import pl.nask.hsn2.protobuff.Process.TaskRequest;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * ServiceConnectorImpl test class.
 */
public class ServiceConnectorImplTest {
	private static final String SERVICE_NAME = "serviceName";
	private static final long JOB_ID = 176253L;
	private static final int TASK_ID = 84201;
	private static final long OBJECT_ID = 2174625536L;
	private static final String RBT_HOST = "rbtHostName";
	private static final String RBT_ROUTINGKEY = "rbtRoutingKey";
	private static final String RBT_EXCHANGE = "rbtExchange";
	private static final String OS_QUEUE = "osQueueName";
	private static final String DS_URL = "dsUrl";

	/**
	 * Possible behaviors to follow during the tests.
	 */
	private enum ActionType {
		FW_CON_THROW_PROTOBUF_EXCEPTION, DS_CON_PING_FALSE, FW_CON_THROW_BUS_EXCEPTION, DS_CON_SHOULD_BE_NULL, FW_CON_SEND_TASK_COMPLETED, FW_CON_SEND_TASK_ERROR, OS_CON_THROW_STORAGE_EXCEPTION;
	}

	private Set<ActionType> actions = new HashSet<>();
	private Set<String> results = new HashSet<>();

	/**
	 * Mocks InConnector, ObjectStoreConnectorImpl and DataStoreConnectorImpl.
	 * 
	 * @throws BusException
	 *             When needed for tests.
	 * @throws IOException
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private void mockConnectors() throws Exception {
		new NonStrictExpectations() {
			InConnector frameworkConnector;
			ObjectStoreConnectorImpl objectStoreConnector;
			DataStoreConnectorImpl dataStoreConnector;
			{
				// Framework connector new instance
				new InConnector(anyString, withInstanceOf(RbtDestination.class));
				forEachInvocation = new Object() {
					void validate(String connectorAddress, RbtDestination destination) throws BusException {
						Assert.assertEquals(connectorAddress, RBT_HOST);
						Assert.assertEquals(destination.getExchange(), RBT_EXCHANGE);
						Assert.assertEquals(destination.getService(), RBT_ROUTINGKEY);
						if (actions.remove(ActionType.FW_CON_THROW_BUS_EXCEPTION)) {
							throw new BusException();
						}
					}
				};

				// Framework connector send
				frameworkConnector.send(withInstanceOf(byte[].class), anyString);
				forEachInvocation = new Object() {
					void validate(byte[] msg, String msgTypeName) throws InvalidProtocolBufferException {
						results.add("type=" + msgTypeName);
						TaskAccepted ta = TaskAccepted.parseFrom(msg);
						results.add("jobId=" + ta.getJob());
						results.add("taskId=" + ta.getTaskId());
					}
				};

				// Framework connector send with ack
				frameworkConnector.sendWithAck(withInstanceOf(byte[].class), anyString);
				forEachInvocation = new Object() {
					void validate(byte[] msg, String msgTypeName) throws InvalidProtocolBufferException {
						results.add("type=" + msgTypeName);
						if (actions.remove(ActionType.FW_CON_SEND_TASK_COMPLETED)) {
							TaskCompleted tc = TaskCompleted.parseFrom(msg);
							results.add("jobId=" + tc.getJob());
							results.add("taskId=" + tc.getTaskId());
							results.add("objects=" + tc.getObjectsList());
							results.add("warnings=" + tc.getWarningsList());
						} else if (actions.remove(ActionType.FW_CON_SEND_TASK_ERROR)) {
							TaskError te = TaskError.parseFrom(msg);
							results.add("jobId=" + te.getJob());
							results.add("taskId=" + te.getTaskId());
							results.add("reason=" + te.getReason());
							results.add("description=" + te.getDescription());
						}
					}
				};

				// Framework connector receive
				frameworkConnector.receive();
				result = new Delegate() {
					byte[] delegate() throws Exception {
						TaskRequest protobufMsg = TaskRequest.newBuilder().setJob(JOB_ID).setTaskId(TASK_ID).setObject(OBJECT_ID).build();
						return protobufMsg.toByteArray();
					}
				};
				forEachInvocation = new Object() {
					void validate() throws InvalidProtocolBufferException {
						if (actions.remove(ActionType.FW_CON_THROW_PROTOBUF_EXCEPTION)) {
							throw new InvalidProtocolBufferException("test");
						}
					}
				};

				// ObjectStore connector new instance
				new ObjectStoreConnectorImpl(anyString, anyString);
				forEachInvocation = new Object() {
					void validate(String connectorAddress, String objectStoreQueueName) {
						Assert.assertEquals(connectorAddress, RBT_HOST);
						Assert.assertEquals(objectStoreQueueName, OS_QUEUE);
					}
				};

				// ObjectStore connector new instance
				objectStoreConnector.sendObjectStoreData(anyLong, anyInt, withInstanceOf(Iterable.class));
				result = new Delegate() {
					ObjectResponse delegate(long jobId, int requestId, Iterable<? extends ObjectData> dataList) {
						// Add results for assertions.
						results.add("jobId=" + jobId);
						results.add("requestId=" + requestId);

						// Return value for assertions.
						ObjectResponse or = ObjectResponse.newBuilder().setType(ResponseType.SUCCESS_GET).addAllData(dataList)
								.addObjects(JOB_ID).addMissing(OBJECT_ID).addConflicts(JOB_ID + OBJECT_ID).setError(OS_QUEUE).build();
						return or;
					}
				};
				forEachInvocation = new Object() {
					void validate(long jobId, int requestId, Iterable<? extends ObjectData> dataList) throws MessageSerializerException {
						if (actions.contains(ActionType.OS_CON_THROW_STORAGE_EXCEPTION)) {
							throw new MessageSerializerException();
						}
					}
				};
				// WST test

				// DataStore connector new instance
				new DataStoreConnectorImpl(anyString);
				forEachInvocation = new Object() {
					void validate(String dataStoreAddress) {
						if (actions.remove(ActionType.DS_CON_SHOULD_BE_NULL)) {
							Assert.assertNull(dataStoreAddress);
						} else {
							Assert.assertEquals(dataStoreAddress, DS_URL);
						}
					}
				};

				// DataStore connector ping
				dataStoreConnector.ping();
				result = new Delegate() {
					public boolean validate() {
						boolean pingResult = true;
						if (actions.remove(ActionType.DS_CON_PING_FALSE)) {
							pingResult = false;
						}
						return pingResult;
					}
				};

				// DataStore connector send post with byte array
				dataStoreConnector.sendPost(withInstanceOf(byte[].class), anyLong);
				forEachInvocation = new Object() {
					void validate(byte[] data, long jobId) {
						results.add("jobId=" + jobId);
						results.add("data=" + data);
					}
				};

				// DataStore connector send post with input stream
				dataStoreConnector.sendPost(withInstanceOf(InputStream.class), anyLong);
				forEachInvocation = new Object() {
					void validate(InputStream dataInputStream, long jobId) throws IOException {
						List<Byte> dataAsList = new ArrayList<>();
						int byteAsInt;
						while ((byteAsInt = dataInputStream.read()) != -1) {
							dataAsList.add((byte) byteAsInt);
						}
						results.add("data=" + dataAsList);
						results.add("jobId=" + jobId);
					}
				};

				// DataStore connector send get
				dataStoreConnector.sendGet(anyLong, anyLong);
				result = new Delegate() {
					DataResponse delegate(long jobId, long dataId) {
						Reference reference = Reference.newBuilder().setKey(OBJECT_ID).setStore(TASK_ID).build();
						ByteString data = ByteString.copyFrom(RBT_ROUTINGKEY.getBytes());
						DataResponse response = DataResponse.newBuilder()
								.setType(pl.nask.hsn2.protobuff.DataStore.DataResponse.ResponseType.OK).setRef(reference).setData(data)
								.setError(RBT_EXCHANGE).build();
						return response;
					}
				};
				forEachInvocation = new Object() {
					void validate(long jobId, long dataId) {
						results.add("jobId=" + jobId);
						results.add("objectId=" + dataId);
					}
				};

				// DataStore connector get resource as stream
				dataStoreConnector.getResourceAsStream(anyLong, anyLong);
				result = new Delegate() {
					InputStream delegate(long jobId, long referenceId) {
						return new ByteArrayInputStream(RBT_EXCHANGE.getBytes());
					}
				};
				forEachInvocation = new Object() {
					void validate(long jobId, long referenceId) {
						results.add("jobId=" + jobId);
						results.add("referenceId=" + referenceId);
					}
				};
			}
		};
	}

	/**
	 * Launched before each test. Clears actions set.
	 */
	@BeforeMethod
	public void beforeMethod() {
		actions.clear();
		results.clear();
	}

	// ##################################################
	// ##############[ Test methods below ]##############
	// ##################################################

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error in connection with DataStore.")
	public void newInstanceWithDataStoreConnError() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.DS_CON_PING_FALSE);

		// Run methods to test.
		new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error in connection with Bus.")
	public void newInstanceWithFrameworkConnectorError() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_THROW_BUS_EXCEPTION);

		// Run methods to test.
		new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
	}

	@Test
	public void getTaskRequest() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.DS_CON_SHOULD_BE_NULL);

		// Run methods to test. (DataStore host should null below. This is to test one of the IF branches in connector.)
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, null);
		TaskRequest tr = sci.getTaskRequest();

		// Check results.
		Assert.assertEquals(tr.getJob(), JOB_ID);
		Assert.assertEquals(tr.getTaskId(), TASK_ID);
		Assert.assertEquals(tr.getObject(), OBJECT_ID);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void getTaskRequestWithProtobufException() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_THROW_PROTOBUF_EXCEPTION);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.getTaskRequest();
	}

	@Test
	public void sendTaskAccepted() throws Exception {
		// Prepare test data.
		mockConnectors();

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskAccepted(JOB_ID, TASK_ID);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskAccepted"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
	}

	@Test
	public void sendTaskComplete() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_COMPLETED);
		List<Long> newObjects = new ArrayList<>();
		newObjects.add(JOB_ID);
		newObjects.add(OBJECT_ID);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskComplete(JOB_ID, TASK_ID, newObjects);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskCompleted"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("objects=" + newObjects));
		Assert.assertTrue(results.contains("warnings=[]"));
	}

	@Test
	public void sendTaskCompletedWithWarnings() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_COMPLETED);
		List<Long> newObjects = new ArrayList<>();
		newObjects.add(JOB_ID);
		newObjects.add(OBJECT_ID);
		List<String> warnings = new ArrayList<>();
		warnings.add(RBT_EXCHANGE);
		warnings.add(RBT_HOST);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskCompletedWithWarnings(JOB_ID, TASK_ID, newObjects, warnings);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskCompleted"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("objects=" + newObjects));
		Assert.assertTrue(results.contains("warnings=" + warnings));
	}

	@Test
	public void sendTaskCompletedWithNoObjectsAndNoWarnings() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_COMPLETED);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskCompletedWithWarnings(JOB_ID, TASK_ID, null, null);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskCompleted"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("objects=[]"));
		Assert.assertTrue(results.contains("warnings=[]"));
	}

	@Test
	public void sendTaskCompletedWithNoObjectsButWithWarnings() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_COMPLETED);
		List<String> warnings = new ArrayList<>();
		warnings.add(RBT_EXCHANGE);
		warnings.add(RBT_HOST);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskCompletedWithWarnings(JOB_ID, TASK_ID, null, warnings);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskCompleted"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("objects=[]"));
		Assert.assertTrue(results.contains("warnings=" + warnings));
	}

	@Test
	public void sendTaskErrorWithGenericException() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_ERROR);
		Exception e = new Exception(RBT_EXCHANGE);
		GenericServiceInfo.setServiceName(SERVICE_NAME);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskError(JOB_ID, TASK_ID, e);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskError"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("reason=" + ReasonType.DEFUNCT));
		Assert.assertTrue(results.contains("description=" + SERVICE_NAME + ": " + RBT_EXCHANGE));
	}

	@Test
	public void sendTaskErrorWithGenericExceptionWithoutMsg() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_ERROR);
		Exception e = new Exception();
		GenericServiceInfo.setServiceName(SERVICE_NAME);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskError(JOB_ID, TASK_ID, e);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskError"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("reason=" + ReasonType.DEFUNCT));
		Assert.assertTrue(results.contains("description=" + SERVICE_NAME + ": java.lang.Exception"));
	}

	@Test
	public void sendTaskErrorWithParameterException() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_ERROR);
		ParameterException e = new ParameterException(RBT_EXCHANGE);
		GenericServiceInfo.setServiceName(SERVICE_NAME);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskError(JOB_ID, TASK_ID, e);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskError"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("reason=" + ReasonType.PARAMS));
		Assert.assertTrue(results.contains("description=" + SERVICE_NAME + ": " + RBT_EXCHANGE));
	}

	@Test
	public void sendTaskErrorlongintResourceException() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_ERROR);
		ResourceException e = new ResourceException(RBT_EXCHANGE);
		GenericServiceInfo.setServiceName(SERVICE_NAME);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskError(JOB_ID, TASK_ID, e);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskError"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("reason=" + ReasonType.RESOURCE));
		Assert.assertTrue(results.contains("description=" + SERVICE_NAME + ": " + RBT_EXCHANGE));
	}

	@Test
	public void sendTaskErrorlongintInputDataException() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_SEND_TASK_ERROR);
		InputDataException e = new InputDataException(RBT_EXCHANGE);
		GenericServiceInfo.setServiceName(SERVICE_NAME);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendTaskError(JOB_ID, TASK_ID, e);

		// Check results.
		Assert.assertTrue(results.contains("type=TaskError"));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("taskId=" + TASK_ID));
		Assert.assertTrue(results.contains("reason=" + ReasonType.INPUT));
		Assert.assertTrue(results.contains("description=" + SERVICE_NAME + ": " + RBT_EXCHANGE));
	}

	@Test
	public void sendDataStoreDataWithByteArray() throws Exception {
		// Prepare test data.
		mockConnectors();
		byte[] data = RBT_EXCHANGE.getBytes();

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendDataStoreData(JOB_ID, data);

		// Check results.
		System.out.println(results);
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("data=" + data));
	}

	@Test
	public void sendDataStoreDataWithInputStream() throws Exception {
		// Prepare test data.
		mockConnectors();
		byte[] data = RBT_EXCHANGE.getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.sendDataStoreData(JOB_ID, bais);

		// Check results.
		System.out.println(results);
		System.out.println(Arrays.toString(data));
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("data=" + Arrays.toString(data)));
	}

	@Test
	public void getDataStoreData() throws Exception {
		// Prepare test data.
		mockConnectors();

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		DataResponse data = sci.getDataStoreData(JOB_ID, OBJECT_ID);

		// Check results.
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("objectId=" + OBJECT_ID));
		Assert.assertEquals(data.getError(), RBT_EXCHANGE);
		Assert.assertEquals(data.getType(), pl.nask.hsn2.protobuff.DataStore.DataResponse.ResponseType.OK);
		Assert.assertEquals(data.getRef().getKey(), OBJECT_ID);
		Assert.assertEquals(data.getRef().getStore(), TASK_ID);
		Assert.assertEquals(data.getData().toStringUtf8(), RBT_ROUTINGKEY);
	}

	@Test
	public void getDataStoreDataAsStream() throws Exception {
		// Prepare test data.
		mockConnectors();

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		InputStream inputStream = sci.getDataStoreDataAsStream(JOB_ID, OBJECT_ID);

		// Check results.
		int dataChunkSize = RBT_EXCHANGE.length();
		byte[] data = new byte[dataChunkSize];
		int numberOfBytesRead = inputStream.read(data);
		Assert.assertEquals(numberOfBytesRead, dataChunkSize);
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("referenceId=" + OBJECT_ID));
	}

	@Test
	public void sendObjectStoreData() throws Exception {
		// Prepare test data.
		mockConnectors();
		List<ObjectData> dataList = new ArrayList<>();
		Attribute a1 = Attribute.newBuilder().setName("a1").setType(Type.INT).setDataInt(TASK_ID).build();
		Attribute a2 = Attribute.newBuilder().setName("a2").setType(Type.TIME).setDataTime(OBJECT_ID).build();
		ObjectData od = ObjectData.newBuilder().setId(OBJECT_ID).addAttrs(a1).addAttrs(a2).build();
		dataList.add(od);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		ObjectResponse or = sci.sendObjectStoreData(JOB_ID, TASK_ID, dataList);

		// Check results.
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("requestId=" + TASK_ID));

		Assert.assertEquals(or.getType(), ResponseType.SUCCESS_GET);
		Assert.assertEquals(or.getError(), OS_QUEUE);

		Assert.assertEquals(or.getDataCount(), 1);
		od = or.getData(0);
		Assert.assertEquals(od.getAttrsCount(), 2);
		a1 = od.getAttrs(0);
		Assert.assertEquals(a1.getName(), "a1");
		Assert.assertEquals(a1.getType(), Type.INT);
		Assert.assertEquals(a1.getDataInt(), TASK_ID);
		a1 = od.getAttrs(1);
		Assert.assertEquals(a1.getName(), "a2");
		Assert.assertEquals(a1.getType(), Type.TIME);
		Assert.assertEquals(a1.getDataTime(), OBJECT_ID);

		Assert.assertEquals(or.getConflictsCount(), 1);
		Assert.assertEquals(or.getConflicts(0), JOB_ID + OBJECT_ID);

		Assert.assertEquals(or.getMissingCount(), 1);
		Assert.assertEquals(or.getMissing(0), OBJECT_ID);

		Assert.assertEquals(or.getObjectsCount(), 1);
		Assert.assertEquals(or.getObjects(0), JOB_ID);
	}

	@Test
	public void saveObjects() throws Exception {
		// Prepare test data.
		mockConnectors();
		List<pl.nask.hsn2.bus.operations.ObjectData> dataList = new ArrayList<>();
		Set<pl.nask.hsn2.bus.operations.Attribute> attributes = new HashSet<>();
		attributes.add(new pl.nask.hsn2.bus.operations.Attribute("a1", AttributeType.BOOL, true));
		attributes.add(new pl.nask.hsn2.bus.operations.Attribute("a2", AttributeType.BOOL, false));
		pl.nask.hsn2.bus.operations.ObjectData od = new pl.nask.hsn2.bus.operations.ObjectData(JOB_ID, attributes);
		dataList.add(od);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		pl.nask.hsn2.bus.operations.ObjectResponse or = sci.saveObjects(JOB_ID, dataList);

		// Check results.
		Assert.assertTrue(results.contains("jobId=" + JOB_ID));
		Assert.assertTrue(results.contains("requestId=0"));
		Assert.assertEquals(or.getData().size(), 1);
		for (pl.nask.hsn2.bus.operations.ObjectData data : or.getData()) {
			for (pl.nask.hsn2.bus.operations.Attribute attr : data.getAttributes()) {
				results.add("" + attr);
			}
		}
		Assert.assertTrue(results.contains("Attribute={name=a1,type=BOOL,value=true}"));
		Assert.assertTrue(results.contains("Attribute={name=a2,type=BOOL,value=false}"));
		Assert.assertNull(or.getError());
		Assert.assertNotNull(or.getConflicts());
		Assert.assertEquals(or.getConflicts().size(), 0);
		Assert.assertNotNull(or.getObjects());
		Assert.assertEquals(or.getObjects().size(), 0);
		Assert.assertNotNull(or.getMissing());
		Assert.assertEquals(or.getMissing().size(), 0);
	}

	@Test(expectedExceptions = StorageException.class)
	public void saveObjectsWithStorageException() throws Exception {
		// Prepare test data.
		mockConnectors();
		List<pl.nask.hsn2.bus.operations.ObjectData> dataList = new ArrayList<>();
		actions.add(ActionType.OS_CON_THROW_STORAGE_EXCEPTION);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		sci.saveObjects(JOB_ID, dataList);
	}

	@Test
	public void updateObjectStoreData() {
		// WST to do
	}

	// ##############################################################
	@Test(enabled = false)
	public void getObjectStoreData() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void ignoreLastTaskRequest() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void toStringCheck() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void updateObject() {
		throw new RuntimeException("Test not implemented");
	}
}
