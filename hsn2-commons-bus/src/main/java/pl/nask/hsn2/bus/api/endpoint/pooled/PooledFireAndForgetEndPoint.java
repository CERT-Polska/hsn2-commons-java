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

package pl.nask.hsn2.bus.api.endpoint.pooled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AlreadyClosedException;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;

/**
 * Pooled <code>NotificationEndPoint for more effective processing
 * messages. Each call of <code>sendNotify</code> or <code>spread</code>
 * will process by separate thread from pool. In multi-threaded
 * environment it's significant boost for the application.
 * 
 * <code>PooledNotificationEndPoint</code> implements <code>NotificationEndPoint</code>
 * so it's very easy to use them interchangeable.
 * 
 *
 */
public class PooledFireAndForgetEndPoint extends AbstractPooledEndPoint implements FireAndForgetEndPoint {

	private static final long DEFAULT_TIMEOUT = 30000; // 30 sec
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PooledFireAndForgetEndPoint.class);
	
	private long timeout = DEFAULT_TIMEOUT;
	
	private final FireAndForgetEndPoint endPoint;
	
	/**
	 * This is simplest constructor of <code>PooledNotificationEndPoint</code>.
	 * Default maximum number of threads is 10.
	 * 
	 * @param createCallback <code>NotificationEndpoint</code> factory callback.
	 * @throws BusException If there is any problem, this exception will thrown.
	 */
	public PooledFireAndForgetEndPoint(PooledNEPCreateCallback createCallback)
				throws BusException {
		endPoint = createCallback.create();
		super.open();
	}
	
	/**
	 * This is a constructor of <code>PooledNotificationEndPoint</code>
	 * with <code>maxthread</code> parameter.
	 * 
	 * @param maxThreads Maximum number of threads for the pool.
	 * @param createCallback <code>NotificationEndpoint</code> factory callback.
	 * @throws BusException If there is any problem, this exception will thrown.
	 */
	public PooledFireAndForgetEndPoint(int maxThreads, PooledNEPCreateCallback createCallback)
				throws BusException {
		super(maxThreads);
		endPoint = createCallback.create();
		super.open();
	}
	
	@Override
	public final void sendNotify(final Message message) throws BusException {
		
		getExecutor().execute(new Runnable(){
			@Override
			public void run() {
				try {
					endPoint.sendNotify(message);
				} catch (BusException | AlreadyClosedException e) {
					LOGGER.error("Cannot process message", e);
				}
			}
		});
	}

	@Override
	public final void spread(final Message message, final String[] servicesNames)
			throws BusException {
		
		getExecutor().execute(new Runnable(){
			@Override
			public void run() {
				try {
					endPoint.spread(message, servicesNames);
				} catch (BusException e) {
					LOGGER.error("Cannot process message", e);
				}
			}
		});
	}

	/**
	 * Sets a timeout for task complete.
	 * 
	 * @param seconds Number of seconds to wait for task complete.
	 */
	public final void setTimeout(long seconds) {
		this.timeout = seconds;
	}
	
	/**
	 * Returns current value of timeout property.
	 * How long publishing task should max works.
	 * 
	 * @return Number of seconds.
	 */
	public final long getTimeout() {
		return timeout;
	}

	/**
	 * This is a callback interface to provide factory method
	 * <code>create</code> for creation implementation
	 * of the <code>NotificationEndPoint</code>
	 * related to chosen transport implementation.
	 */
	public interface PooledNEPCreateCallback {
		FireAndForgetEndPoint create();
	}
}
