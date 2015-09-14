package pl.nask.hsn2;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.protobuff.Jobs.JobFinished;
import pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public abstract class FinishedJobsListenerAbstract implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(FinishedJobsListenerAbstract.class);
	private static final boolean AUTO_ACK = true;

	private String rabbitMqHost;
	private String rabbitMqExchange;
	private AtomicBoolean isInitialized = new AtomicBoolean(false);
	private AtomicBoolean isMainLoopEnabled = new AtomicBoolean(false);

	public void initialize(String rbtHost, String rbtExchange) {
		if (!isInitialized.get()) {
			if (rbtHost == null || rbtExchange == null) {
				LOG.error("Finished jobs listener initialization error.");
			} else {
				synchronized (this) {
					rabbitMqHost = rbtHost;
					rabbitMqExchange = rbtExchange;
					isInitialized.set(true);
					isMainLoopEnabled.set(true);
				}
				LOG.debug("Finished jobs listener initialized.");
			}
		}
	}
	
	abstract protected void process(JobFinished data);
	abstract protected void process(JobFinishedReminder data);

	@Override
	public void run() {
		if (!isInitialized.get()) {
			LOG.error("Listener has not been initialized properly and has not been started.");
			return;
		}

		// Listener initialized.
		LOG.info("Finished jobs listener started. (host={}, exchange={})", rabbitMqHost, rabbitMqExchange);
		try {
			// Prepare consumer.
			QueueingConsumer rabbitMqConsumer = prepareRabbitMqConsumer();

			// Main loop.
			while (isMainLoopEnabled.get()) {
				try {
					QueueingConsumer.Delivery delivery = rabbitMqConsumer.nextDelivery();

					// Check if it's JobFinished or JobFinishedReminder and if so add to list.
					if (delivery.getProperties().getType().equals("JobFinished")) {
						JobFinished jobFinishedData = JobFinished.parseFrom(delivery.getBody());
						this.process(jobFinishedData);
					} else if (delivery.getProperties().getType().equals("JobFinishedReminder")) {
						JobFinishedReminder jobFinishedData = JobFinishedReminder.parseFrom(delivery.getBody());
						this.process(jobFinishedData);
					}

				} catch (ShutdownSignalException e) {
					LOG.debug("Shutdown signal received.", e);
					isMainLoopEnabled.set(false);
				} catch (ConsumerCancelledException e) {
					LOG.debug("Consumer cancelled operation.", e);
				} catch (InterruptedException e) {
					// nothing to do
				}
			}
		} catch (IOException e) {
			LOG.error("Error while communicating with message broker.", e);
		} catch (Exception e) {
			LOG.error("Unexpected error during JobFinished message processing.", e);
		}
		LOG.info("Finished jobs listener terminated.");
	}

	private QueueingConsumer prepareRabbitMqConsumer() throws IOException {
		ConnectionFactory rabbitMqConnectionFactory = new ConnectionFactory();
		rabbitMqConnectionFactory.setHost(rabbitMqHost);
		
		Connection rabbitMqConnection = rabbitMqConnectionFactory.newConnection();
		Channel rabbitMqChannel = rabbitMqConnection.createChannel();
		rabbitMqChannel.exchangeDeclare(rabbitMqExchange, "fanout");

		String rabbitMqQueueName = rabbitMqChannel.queueDeclare().getQueue();
		rabbitMqChannel.queueBind(rabbitMqQueueName, rabbitMqExchange, "");

		QueueingConsumer rabbitMqConsumer = new QueueingConsumer(rabbitMqChannel);
		rabbitMqChannel.basicConsume(rabbitMqQueueName, AUTO_ACK, rabbitMqConsumer);
		return rabbitMqConsumer;
	}
	
	public boolean isMainLoopEnabled() {
		return isMainLoopEnabled.get();
	}

	public void shutdown() {
		isMainLoopEnabled.set(false);
	}
}
