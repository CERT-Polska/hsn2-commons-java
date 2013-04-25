package pl.nask.hsn2.connector.AMQP;

import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.EXCHANGE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.QUEUE_NAME;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.actions;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.mockDeclareOK;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.rbtMock;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mockit.Mocked;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.AMQP.RbtMockUtil.ActionType;
import pl.nask.hsn2.protobuff.Object.Attribute;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure.QueryType;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.ResponseType;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class ObjectStoreConnectorImplTest {
	@Mocked
	public ConnectionFactory connectionFactory;

	@Mocked
	public Connection connection;

	@Mocked
	public Channel channel;

	@Mocked
	public QueueingConsumer consumer;

	@Mocked
	public DeclareOk declareOk;

	@BeforeMethod
	public void beforeMethod() throws IOException, InterruptedException {
		actions.clear();
		result = "";
	}

	@Test(dependsOnGroups = "InConnectorTest")
	public void sendObjectStoreData() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);

		long jobId = 123L;
		int requestId = 456;
		Attribute a1 = Attribute.newBuilder().setType(Type.INT).setName("a1").setDataInt(1111).build();
		Set<ObjectData> dataList = new HashSet<>();
		dataList.add(ObjectData.newBuilder().addAttrs(a1).build());

		actions.add(ActionType.RECEIVE_OBJECT_RESPONSE);
		ObjectStoreConnectorImpl os = new ObjectStoreConnectorImpl("", QUEUE_NAME);
		ObjectResponse response = os.sendObjectStoreData(jobId, requestId, dataList);

		Assert.assertEquals(result, QUEUE_NAME
				+ "ObjectRequest[8, 1, 16, 123, 34, 11, 10, 9, 10, 2, 97, 49, 16, 2, 32, -41, 8, 40, -56, 3]");
		Assert.assertEquals(response.getType(), ResponseType.SUCCESS_GET);
		Assert.assertEquals(response.getError(), EXCHANGE);
	}

	@Test(dependsOnGroups = "InConnectorTest")
	public void updateObjectStoreData() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);

		long jobId = 123L;
		Attribute a1 = Attribute.newBuilder().setType(Type.INT).setName("a1").setDataInt(1111).build();
		Set<ObjectData> dataList = new HashSet<>();
		dataList.add(ObjectData.newBuilder().addAttrs(a1).build());

		actions.add(ActionType.RECEIVE_OBJECT_RESPONSE);
		ObjectStoreConnectorImpl os = new ObjectStoreConnectorImpl("", QUEUE_NAME);
		ObjectResponse response = os.updateObjectStoreData(jobId, dataList);

		Assert.assertEquals(result, QUEUE_NAME + "ObjectRequest[8, 2, 16, 123, 34, 11, 10, 9, 10, 2, 97, 49, 16, 2, 32, -41, 8]");
		Assert.assertEquals(response.getType(), ResponseType.SUCCESS_GET);
		Assert.assertEquals(response.getError(), EXCHANGE);
	}

	@Test(dependsOnGroups = "InConnectorTest")
	public void getObjectStoreData() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);

		long jobId = 123L;
		List<Long> objectsId = new ArrayList<>();
		objectsId.add(1L);
		objectsId.add(2L);

		actions.add(ActionType.RECEIVE_OBJECT_RESPONSE);
		ObjectStoreConnectorImpl os = new ObjectStoreConnectorImpl("", QUEUE_NAME);
		ObjectResponse response = os.getObjectStoreData(jobId, objectsId);

		Assert.assertEquals(result, QUEUE_NAME + "ObjectRequest[8, 0, 16, 123, 26, 2, 1, 2]");
		Assert.assertEquals(response.getType(), ResponseType.SUCCESS_GET);
		Assert.assertEquals(response.getError(), EXCHANGE);
	}

	@Test(dependsOnGroups = "InConnectorTest")
	public void query() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);

		long jobId = 123L;
		QueryStructure qs = QueryStructure.newBuilder().setAttrName("name").setType(QueryType.BY_ATTR_NAME).build();
		List<QueryStructure> queryStructures = new ArrayList<>();
		queryStructures.add(qs);

		actions.add(ActionType.RECEIVE_OBJECT_RESPONSE);
		ObjectStoreConnectorImpl os = new ObjectStoreConnectorImpl("", QUEUE_NAME);
		ObjectResponse response = os.query(jobId, queryStructures);

		Assert.assertEquals(result, QUEUE_NAME + "ObjectRequest[8, 3, 16, 123, 58, 8, 8, 1, 18, 4, 110, 97, 109, 101]");
		Assert.assertEquals(response.getType(), ResponseType.SUCCESS_GET);
		Assert.assertEquals(response.getError(), EXCHANGE);
	}

	@Test(dependsOnGroups = "InConnectorTest", expectedExceptions = StorageException.class)
	public void queryInvalidProtobufType() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);

		long jobId = 123L;
		QueryStructure qs = QueryStructure.newBuilder().setAttrName("name").setType(QueryType.BY_ATTR_NAME).build();
		List<QueryStructure> queryStructures = new ArrayList<>();
		queryStructures.add(qs);

		ObjectStoreConnectorImpl os = new ObjectStoreConnectorImpl("", QUEUE_NAME);
		os.query(jobId, queryStructures);
	}

	@Test(dependsOnGroups = "InConnectorTest", expectedExceptions = StorageException.class)
	public void queryMsgSendThrowsStorageException() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);

		long jobId = 123L;
		QueryStructure qs = QueryStructure.newBuilder().setAttrName("name").setType(QueryType.BY_ATTR_NAME).build();
		List<QueryStructure> queryStructures = new ArrayList<>();
		queryStructures.add(qs);

		actions.add(ActionType.RECEIVE_OBJECT_RESPONSE);
		actions.add(ActionType.PUBLISH_THROW_IO_EXCEPTION);
		ObjectStoreConnectorImpl os = new ObjectStoreConnectorImpl("", QUEUE_NAME);
		os.query(jobId, queryStructures);
	}
}
