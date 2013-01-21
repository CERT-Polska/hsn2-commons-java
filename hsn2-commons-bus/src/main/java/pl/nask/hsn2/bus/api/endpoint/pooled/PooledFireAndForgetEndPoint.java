/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
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
	
	private PooledNEPCreateCallback createCallback;
	private long timeout = DEFAULT_TIMEOUT;
	
	/**
	 * This is simplest constructor of <code>PooledNotificationEndPoint</code>.
	 * Default maximum number of threads is 10.
	 * 
	 * @param createCallback <code>NotificationEndpoint</code> factory callback.
	 * @throws BusException If there is any problem, this exception will thrown.
	 */
	public PooledFireAndForgetEndPoint(
			PooledNEPCreateCallback createCallback)
				throws BusException {
		super();
		this.createCallback = createCallback;
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
	public PooledFireAndForgetEndPoint(int maxThreads,
			PooledNEPCreateCallback createCallback)
				throws BusException {
		super(maxThreads);
		this.createCallback = createCallback;
		super.open();
	}
	
	@Override
	public final void sendNotify(final Message message) throws BusException {
		final FireAndForgetEndPoint endPoint = createCallback.create();
		try {
			Future<?> future = getExecutor().submit(new Runnable(){
				@Override
				public void run() {
					try {
						endPoint.sendNotify(message);
					} catch (BusException e) {
						LOGGER.error("Cannot process message", e);
					}
				}
			});
			wait(future);
		} finally {
			endPoint.close();
		}
	}

	@Override
	public final void spread(final Message message, final String[] servicesNames)
			throws BusException {
		final FireAndForgetEndPoint endPoint = createCallback.create();
		try {
			Future<?> future = getExecutor().submit(new Runnable(){
				@Override
				public void run() {
					try {
						endPoint.spread(message, servicesNames);
					} catch (BusException e) {
						LOGGER.error("Cannot process message", e);
					}
				}
			});
			wait(future);
		} finally {
			endPoint.close();
		}
	}

	/**
	 * Waits for task finish.
	 * 
	 * @param future Task to wait for.
	 * @throws BusException The exception will thrown if there is execution problem.
	 * @throws TimeoutException The exception will thrown if timeout.
	 */
	private void wait(Future<?> future) throws BusException {
		try {
			future.get(getTimeout(), TimeUnit.SECONDS);
			if (future.isCancelled()) {
				throw new BusException("Task cancelled...");
			}
		} catch (java.util.concurrent.TimeoutException ex) {
			throw new TimeoutException("Witing for response message timeout.", ex);
		} catch (InterruptedException ex) {
			throw new BusException("Problem with sending message, thread interruped...", ex);
		} catch (ExecutionException ex) {
			throw new BusException("Problem with sending message, task aborted...", ex);
		}
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
		return this.timeout;
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
