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

package pl.nask.hsn2.bus.test.rabbitmq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.utils.ConfigurableExecutorService;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class RbtAbstractEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtAbstractEndPointTest.class);
			
	protected Connection getConnection(int numberOfconsumerThreads) throws BusException {
		
		ConnectionFactory factory = getConnectionFactory();

		Connection connection = null;
		try {
			connection = factory.newConnection(new ConfigurableExecutorService("rbt-test-consumer-", numberOfconsumerThreads));
		} catch (IOException e) {
			throw new BusException("Cannot establish connection to the broker.", e);
		}
		
		if (connection == null) {
			throw new BusException("Cannot establish connection to the broker.");
		}

		LOGGER.info("Got connection.");
		return connection;
	}
	
	private ConnectionFactory getConnectionFactory() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("195.187.238.85");
		return factory;
	}
}
