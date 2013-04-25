package pl.nask.hsn2.connector.AMQP;

import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.CLOSE_STRING;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.DESTINATION;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.EXCHANGE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.MESSAGE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.SERVICE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.TYPE;
import static pl.nask.hsn2.connector.AMQP.RbtMockUtil.actions;
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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

@Test(groups = "InConnectorTest")
public class InConnectorTest {
	@Mocked
	public ConnectionFactory connectionFactory;

	@Mocked
	public Connection connection;

	@Mocked
	public Channel channel;

	@Mocked
	public QueueingConsumer consumer;

	@BeforeMethod
	public void beforeMethod() throws IOException, InterruptedException {
		actions.clear();
		result = "";
	}

	@Test(expectedExceptions = BusException.class, expectedExceptionsMessageRegExp = "Can't create connection.")
	public void newConnectionWithException() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		actions.add(ActionType.NEW_CONNECTION_THROW_IO_EXCEPTION);
		new InConnector("", DESTINATION);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void send() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		ic.send(MESSAGE, TYPE);
		ic.close();
		Assert.assertEquals(result, EXCHANGE + SERVICE + TYPE + Arrays.toString(MESSAGE) + CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void sendFailed() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		// RabbitMq will throw exception but connector should catch it, display log message, but return as it always
		// does.
		actions.add(ActionType.PUBLISH_THROW_IO_EXCEPTION);
		ic.send(MESSAGE, TYPE);
		ic.close();
		Assert.assertEquals(result, CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void sendWithAck() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		ic.sendWithAck(MESSAGE, TYPE);
		ic.close();
		Assert.assertEquals(result, EXCHANGE + SERVICE + TYPE + Arrays.toString(MESSAGE) + "0false" + CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void sendWithAckWithIoException() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		// RabbitMq will throw exception but connector should catch it, display log message, but return as it always
		// does.
		actions.add(ActionType.ACK_THROW_IO_EXCEPTION);
		ic.sendWithAck(MESSAGE, TYPE);
		ic.close();
		// Msg should be send, but ack action will fail, so we get part of result.
		Assert.assertEquals(result, EXCHANGE + SERVICE + TYPE + Arrays.toString(MESSAGE) + CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void ack() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		ic.ackLastMessage();
		ic.close();
		Assert.assertEquals(result, "0false" + CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void ackWithException() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		// RabbitMq will throw exception but connector should catch it, display log message, but return as it always
		// does.
		actions.add(ActionType.ACK_THROW_IO_EXCEPTION);
		ic.ackLastMessage();
		ic.close();
		Assert.assertEquals(result, CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void receive() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		byte[] msg = ic.receive();
		ic.close();
		Assert.assertEquals(Arrays.toString(msg), Arrays.toString(MESSAGE));
	}

	@Test(dependsOnMethods = "newConnectionWithException", expectedExceptions = IllegalArgumentException.class)
	public void receiveWrongType() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		actions.add(ActionType.WRONG_CONTENT_TYPE);
		byte[] msg = ic.receive();
		ic.close();
		Assert.assertEquals(Arrays.toString(msg), Arrays.toString(MESSAGE) + CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException", expectedExceptions = BusException.class, expectedExceptionsMessageRegExp = "Can't receive message.")
	public void receiveWithException() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		actions.add(ActionType.THROW_INTERRUPT_EXCEPTION);
		ic.receive();
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void closeChannel() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		ic.closeChannel();
		ic.close();
		Assert.assertEquals(result, EXCHANGE + SERVICE + EXCHANGE + SERVICE + CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void closeChannelWithIoException() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		// RabbitMq will throw exception but connector should catch it, display log message, but return as it always
		// does.
		actions.add(ActionType.CHANNEL_CLOSE_THROW_IO_EXCEPTION);
		ic.closeChannel();
		ic.close();
		Assert.assertEquals(result, CLOSE_STRING);
	}

	@Test(dependsOnMethods = "newConnectionWithException")
	public void closeAlreadyClosedConnector() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		// No action will be taken because connection is already closed.
		actions.add(ActionType.CONNECTION_IS_CLOSED);
		ic.close();
		Assert.assertEquals(result, "");
	}

	@Test(dependsOnMethods = "newConnectionWithException", expectedExceptions = BusException.class)
	public void closeConnectorWithIoException() throws Exception {
		rbtMock(connectionFactory, connection, channel, consumer);
		InConnector ic = new InConnector("", DESTINATION);

		actions.add(ActionType.CONNECTION_CLOSE_THROW_IO_EXCEPTION);
		ic.close();
	}
}
