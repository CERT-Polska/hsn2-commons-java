package pl.nask.hsn2;

import java.util.HashSet;
import java.util.Set;

import mockit.Delegate;
import mockit.NonStrictExpectations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.AMQP.InConnector;
import pl.nask.hsn2.connector.AMQP.ObjectStoreConnectorImpl;
import pl.nask.hsn2.connector.REST.DataStoreConnectorImpl;
import pl.nask.hsn2.protobuff.Process.TaskRequest;

import com.google.protobuf.InvalidProtocolBufferException;

public class ServiceConnectorImplTest {
	private static final Logger LOGGER = LoggerFactory.getLogger("TEST");
	private static final long JOB_ID = 176253L;
	private static final int TASK_ID = 84201;
	private static final long OBJECT_ID = 2174625536L;
	private static final String RBT_HOST = "rbtHostName";
	private static final String RBT_ROUTINGKEY = "rbtRoutingKey";
	private static final String RBT_EXCHANGE = "rbtExchange";
	private static final String OS_QUEUE = "osQueueName";
	private static final String DS_URL = "dsUrl";

	private enum ActionType {
		FW_CON_THROW_PROTOBUF_EXCEPTION, FW_CON_RECEIVE_TASK_REQUEST, DS_CONN_PING_FALSE;
	}

	private Set<ActionType> actions = new HashSet<>();

	@SuppressWarnings({ "unused", "rawtypes" })
	private void mockConnectors() throws BusException {
		new NonStrictExpectations() {
			InConnector frameworkConnector;
			ObjectStoreConnectorImpl objectStoreConnector;
			DataStoreConnectorImpl dataStoreConnector;
			{
				// Framework connector new instance
				new InConnector(anyString, withInstanceOf(RbtDestination.class));
				times = 1;
				forEachInvocation = new Object() {
					void validate(String connectorAddress, RbtDestination destination) {
						Assert.assertEquals(connectorAddress, RBT_HOST);
						Assert.assertEquals(destination.getExchange(), RBT_EXCHANGE);
						Assert.assertEquals(destination.getService(), RBT_ROUTINGKEY);
						LOGGER.info("RbtHost={}, RbtRKey={}, RbtExch={}", connectorAddress, destination.getService(),
								destination.getExchange());
					}
				};

				// Framework connector receive
				frameworkConnector.receive();
				result = new Delegate() {
					public byte[] validate() throws Exception {
						LOGGER.info("InConn receive delegate");
						TaskRequest protobufMsg = TaskRequest.newBuilder().setJob(JOB_ID).setTaskId(TASK_ID).setObject(OBJECT_ID).build();
						return protobufMsg.toByteArray();
					}
				};
				forEachInvocation = new Object() {
					void validate() throws InvalidProtocolBufferException {
						LOGGER.info("InConn receive forEachInvocation");
						if (actions.remove(ActionType.FW_CON_THROW_PROTOBUF_EXCEPTION)) {
							throw new InvalidProtocolBufferException("test");
						}
					}
				};

				// ObjectStore connector new instance
				new ObjectStoreConnectorImpl(anyString, anyString);
				times = 1;
				forEachInvocation = new Object() {
					void validate(String connectorAddress, String objectStoreQueueName) {
						Assert.assertEquals(connectorAddress, RBT_HOST);
						Assert.assertEquals(objectStoreQueueName, OS_QUEUE);
						LOGGER.info("RbtHost={}, OsQueue={}", connectorAddress, objectStoreQueueName);
					}
				};

				// DataStore connector new instance
				new DataStoreConnectorImpl(anyString);
				times = 1;
				forEachInvocation = new Object() {
					void validate(String dataStoreAddress) {
						Assert.assertEquals(dataStoreAddress, DS_URL);
						LOGGER.info("DsUrl={}", dataStoreAddress);
					}
				};

				// DataStore connector ping
				dataStoreConnector.ping();
				times = 1;
				result = new Delegate() {
					public boolean validate() {
						boolean pingResult = true;
						if (actions.remove(ActionType.DS_CONN_PING_FALSE)) {
							pingResult = false;
						}
						LOGGER.info("Ping result = {}", pingResult);
						return pingResult;
					}
				};
			}
		};
	}

	@BeforeMethod
	public void beforeMethod() {
		actions.clear();
	}

	@Test(enabled = false, expectedExceptions = RuntimeException.class)
	public void newInstanceDataStoreConnError() throws Exception {
		// Prepare test data.
		// mockConnectors();
		actions.add(ActionType.DS_CONN_PING_FALSE);

		// Run methods to test.
		new ServiceConnectorImpl("", "", "", "", "");
	}

	// ###########################################

	@Test
	public void getTaskRequest() throws Exception {
		// Prepare test data.
		mockConnectors();
		actions.add(ActionType.FW_CON_RECEIVE_TASK_REQUEST);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl(RBT_HOST, RBT_ROUTINGKEY, RBT_EXCHANGE, OS_QUEUE, DS_URL);
		TaskRequest tr = sci.getTaskRequest();

		// Check results.
		Assert.assertEquals(tr.getJob(), JOB_ID);
		Assert.assertEquals(tr.getTaskId(), TASK_ID);
		Assert.assertEquals(tr.getObject(), OBJECT_ID);
	}

	@Test(enabled = false, expectedExceptions = IllegalStateException.class)
	public void getTaskRequestWithProtobufException() throws Exception {
		// Prepare test data.
		// mockConnectors();
		actions.add(ActionType.FW_CON_THROW_PROTOBUF_EXCEPTION);

		// Run methods to test.
		ServiceConnectorImpl sci = new ServiceConnectorImpl("", "", "", "", "");
		sci.getTaskRequest();
	}

	@Test(enabled = false)
	public void getDataStoreData() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getDataStoreDataAsStream() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getObjectStoreData() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void ignoreLastTaskRequest() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void saveObjects() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendDataStoreDatalongbyte() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendDataStoreDatalongInputStream() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendObjectStoreData() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskAccepted() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskComplete() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskCompletedWithWarnings() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskErrorlongintException() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskErrorlongintParameterException() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskErrorlongintResourceException() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskErrorlongintInputDataException() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void sendTaskErrorMessage() {
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

	@Test(enabled = false)
	public void updateObjectStoreData() {
		throw new RuntimeException("Test not implemented");
	}
}
