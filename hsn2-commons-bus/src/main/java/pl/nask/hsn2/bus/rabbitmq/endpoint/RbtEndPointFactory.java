/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.bus.rabbitmq.endpoint;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPoint;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.EndPointFactory;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.MulticastEndPoint;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledFireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledMulticastEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledRequestResponseEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.ThreadLocalRequestResponseEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;
import pl.nask.hsn2.bus.utils.ConfigurableExecutorService;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RbtEndPointFactory implements EndPointFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtEndPointFactory.class);
	
	private ConnectionFactory factory;
	private Connection connection;
	private int numberOfconsumerThreads = 10;
	
	public final RbtEndPointFactory setServerAddress(String serverAddress) {
		if (this.factory == null) {
			this.factory = new ConnectionFactory();
		}
		this.factory.setHost(serverAddress);
		LOGGER.debug("RbtEndPointFactory configuration: serverAddress={}", serverAddress);
		return this;
	}

	/**
	 * Sets expected number of customer threads used by the ConsumeEndPoint.
	 * 
	 * This method will NOT reconnect, so will not affect if called too late!
	 * There is a recommendation to call it before setServerAddress().
	 * 
	 * @param numberOfconsumerThreads Expected number of threads, default 10.
	 * @return Instance this class.
	 */
	public final RbtEndPointFactory setNumberOfconsumerThreads(int numberOfconsumerThreads) {
		this.numberOfconsumerThreads = numberOfconsumerThreads;
		return this;
	}
	
	@Override
	public final ConsumeEndPoint createConsumeEndPoint(
			ConsumeEndPointHandler messagesHandler, String routingKey)
			throws BusException {

		return this.createConsumeEndPoint(messagesHandler, routingKey, true);
	}

	@Override
	public final ConsumeEndPoint createConsumeEndPoint(
			ConsumeEndPointHandler messagesHandler, String routingKey,
			boolean autoack) throws BusException {

		ensureIfConnectionOpen();

		return new RbtConsumeEndPoint(this.connection, messagesHandler,
				routingKey, autoack, numberOfconsumerThreads);
	}

	@Override
	public final RequestResponseEndPoint createRequestResponseEndPoint()
			throws BusException {

		return createRequestResponseEndPoint(1);
	}
	
	@Override
	public final RequestResponseEndPoint createThreadSafeRequestResponseEndPoint() throws BusException {

		ensureIfConnectionOpen();
		
		return new ThreadLocalRequestResponseEndPoint(this);
	}

	@Override
	public final RequestResponseEndPoint createRequestResponseEndPoint(int maxThreads)
			throws BusException {

		ensureIfConnectionOpen();

		final EndPointFactory thiz = this;
		
		if (maxThreads > 1) {
			return new PooledRequestResponseEndPoint(maxThreads,
					new PooledRequestResponseEndPoint.PooledRREPCreateCallback(){
						@Override
						public RequestResponseEndPoint create() {
							try {
								return thiz.createRequestResponseEndPoint(1);
							} catch (BusException e) {
								throw new IllegalStateException("The connection to Rabbit MQ is not stable.");
							}
						}
			});
		} else {
			return new RbtRequestResponseEndPoint(this.connection);
		}
	}		

	@Override
	public final FireAndForgetEndPoint createFireAndForgetEndPoint()
			throws BusException {
		return this.createNotificationEndPoint(1);
	}

	@Override
	public final FireAndForgetEndPoint createNotificationEndPoint(int maxThreads)
			throws BusException {

		ensureIfConnectionOpen();

		final EndPointFactory thiz = this;
		
		if (maxThreads > 1) {
			return new PooledFireAndForgetEndPoint(maxThreads,
					new PooledFireAndForgetEndPoint.PooledNEPCreateCallback(){
						@Override
						public FireAndForgetEndPoint create() {
							try {
								return thiz.createNotificationEndPoint(1);
							} catch (BusException e) {
								throw new IllegalStateException("The connection to Rabbit MQ is not stable.");
							}
						}
			});
		} else {
			return new RbtFireAndForgetEndPoint(this.connection);
		}
	}

	@Override
	public final MulticastEndPoint createMulticastEndPoint() throws BusException {
		return this.createMulticastEndPoint(1);
	}

	@Override
	public final MulticastEndPoint createMulticastEndPoint(int maxThreads)
			throws BusException {
		ensureIfConnectionOpen();

		final EndPointFactory thiz = this;
		
		if (maxThreads > 1) {
			return new PooledMulticastEndPoint(maxThreads,
					new PooledMulticastEndPoint.PooledMulticastEPCreateCallback(){
						@Override
						public MulticastEndPoint create() {
							try {
								return thiz.createMulticastEndPoint(1);
							} catch (BusException e) {
								throw new IllegalStateException("The connection to Rabbit MQ is not stable.");
							}
						}
			});
		} else {
			return new RbtMulticastEndPoint(this.connection);
		}
	}

	public final Connection getConnection() throws BusException {
		ensureIfConnectionOpen();
		return this.connection;
	}
	
	private void ensureIfConnectionOpen() throws BusException {
		if (!isConnectionValid()) {
			reconnect();
		}
	}

	public final boolean isConnectionValid() {
		return this.connection != null && this.connection.isOpen();
	}
	
	/**
	 * Reconnects to the broker with new parameters.
	 * 
	 * @throws BusException If there is any problem this exception will rise.
	 */
	public void reconnect() throws BusException {
		try {
			LOGGER.info("(Re)connecting to Rabbit MQ server.");
			RbtUtils.closeConnection(this.connection);
			this.connection = factory.newConnection(new ConfigurableExecutorService("rbt-consumer-", numberOfconsumerThreads));			
			LOGGER.info("Connected to Rabbit MQ server.");
		} catch (IOException e) {
			throw new BusException("Cannot connect to Rabbit MQ server.", e);
		}
	}
	
	public void disconnect() throws BusException {
		if (isConnectionValid()) {
			try {
				this.connection.close();
			} catch (IOException e) {
				LOGGER.error("There are errors during disconnecting:", e);
			}
		}
	}
}
