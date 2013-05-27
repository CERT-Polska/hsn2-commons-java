package pl.nask.hsn2.connector.AMQP;

import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.CLOSE_STRING;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.EXCHANGE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.MESSAGE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.SERVICE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.TYPE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.actions;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.mockDeclareOK;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.rbtMock;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.result;

import java.io.IOException;
import java.util.Arrays;

import mockit.Mocked;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.AMQP.RbtMockUtil.ActionType;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class OutConnectorTest {
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

	@Test(dependsOnGroups = "InConnectorTest",expectedExceptions = BusException.class)
	public void sendUnsuccessful() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);
		OutConnector oc = new OutConnector("", SERVICE);
		actions.add(ActionType.PUBLISH_THROW_IO_EXCEPTION);
		oc.send(MESSAGE, TYPE);
	}

	@Test(dependsOnGroups = "InConnectorTest")
	public void sendAndReceive() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);
		OutConnector oc = new OutConnector("", SERVICE);
		oc.send(MESSAGE, TYPE);
		Assert.assertEquals(result, SERVICE + TYPE + Arrays.toString(MESSAGE));
		
		byte[] msg = oc.receive();
		oc.close();
		Assert.assertEquals(Arrays.toString(msg), Arrays.toString(MESSAGE));
		Assert.assertEquals(result, SERVICE + TYPE + Arrays.toString(MESSAGE) + CLOSE_STRING);
	}

	@Test(dependsOnGroups = "InConnectorTest")
	public void sendAndReceiveWrongCorrId() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);
		OutConnector oc = new OutConnector("", SERVICE);
		oc.send(MESSAGE, TYPE);
		Assert.assertEquals(result, SERVICE + TYPE + Arrays.toString(MESSAGE));

		// Connector should get 1st message with wrong correlation ID, ignore it, and then get 2nd message with proper
		// correlation ID which should finish the test.
		actions.add(ActionType.WRONG_CORRELATION_ID);
		byte[] msg = oc.receive();
		oc.close();
		Assert.assertEquals(Arrays.toString(msg), Arrays.toString(MESSAGE));
		Assert.assertEquals(result, SERVICE + TYPE + Arrays.toString(MESSAGE) + CLOSE_STRING);
	}

	@Test(dependsOnGroups = "InConnectorTest",expectedExceptions=IllegalArgumentException.class)
	public void sendAndReceiveWrongContentType() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);
		OutConnector oc = new OutConnector("", SERVICE);
		oc.send(MESSAGE, TYPE);
		Assert.assertEquals(result, SERVICE + TYPE + Arrays.toString(MESSAGE));

		actions.add(ActionType.WRONG_CONTENT_TYPE);
		oc.receive();
	}

	@Test(dependsOnGroups = "InConnectorTest",expectedExceptions=BusException.class)
	public void sendAndReceiveInterrupted() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);
		OutConnector oc = new OutConnector("", SERVICE);
		oc.send(MESSAGE, TYPE);
		Assert.assertEquals(result, SERVICE + TYPE + Arrays.toString(MESSAGE));
		
		actions.add(ActionType.THROW_INTERRUPT_EXCEPTION);
		oc.receive();
	}

	@Test(dependsOnGroups = "InConnectorTest")
	void closeChannel() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);
		OutConnector oc = new OutConnector("", SERVICE);
		oc.closeChannel();
		oc.close();
		Assert.assertEquals(result, EXCHANGE + SERVICE + EXCHANGE + SERVICE + CLOSE_STRING);
	}

	@Test(dependsOnGroups = "InConnectorTest")
	public void closeChannelFailed() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		mockDeclareOK(declareOk, channel);
		OutConnector oc = new OutConnector("", SERVICE);

		// RabbitMq will throw exception but connector should catch it, display log message, but return as it always
		// does.
		actions.add(ActionType.CHANNEL_CLOSE_THROW_IO_EXCEPTION);
		oc.closeChannel();
		oc.close();
		Assert.assertEquals(result, CLOSE_STRING);
	}
}
