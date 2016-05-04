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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;

/**
 * Pooled <code>RequestResponseEndPoint for more effective processing
 * messages. Each call of <code>sendAndGet</code>
 * will process by separate thread from pool. In multi-threaded
 * environment it's significant boost for the application.
 * 
 * <code>PooledRequestResponseEndPoint</code> implements
 * <code>RequestResponseEndPoint</code> so it's very easy to
 * use them interchangeable. This wrapper is technology independent. 
 * 
 *
 */
public class PooledRequestResponseEndPoint extends AbstractPooledEndPoint implements RequestResponseEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(PooledRequestResponseEndPoint.class);
			
	private PooledRREPCreateCallback createCallback;

	/**
	 * This is simplest constructor of <code>PooledRequestResponseEndPoint</code>.
	 * Default maximum number of threads is 10.
	 * 
	 * @param createCallback <code>RequestResponseEndPoint</code> factory callback.
	 * @throws BusException If there is any problem, this exception will thrown.
	 */
	public PooledRequestResponseEndPoint(
			PooledRREPCreateCallback createCallback)
				throws BusException {
		this.createCallback = createCallback;
		super.open();
	}

	/**
	 * This is a constructor of <code>PooledRequestResponseEndPoint</code>
	 * with maximum threads parameter.
	 *
	 * @param maxThreads Maximum number of threads used by the pool.
	 * @param createCallback <code>RequestResponseEndPoint</code> factory callback.
	 * @throws BusException If there is any problem, this exception will thrown.
	 */
	public PooledRequestResponseEndPoint(int maxThreads,
			PooledRREPCreateCallback createCallback)
				throws BusException {
		super(maxThreads);
		this.createCallback = createCallback;
		super.open();
	}

	@Override
	public final Message sendAndGet(final Message message) throws BusException {

		final AtomicReference<Message> response = new AtomicReference<Message>();
		final RequestResponseEndPoint rrEndPoint = createCallback.create();
		try {
			Future<?> future = getExecutor().submit(new Runnable(){
				@Override
				public void run() {
					try {
						response.set(rrEndPoint.sendAndGet(message));
					} catch (BusException e) {
						LOGGER.error("Cannot process message", e);
					}
				}
			});

			future.get(rrEndPoint.getTimeout(), TimeUnit.SECONDS);
			if (!future.isCancelled()) {
				return response.get();
			} else {
				throw new BusException("Task cancelled...");
			}
		} catch (java.util.concurrent.TimeoutException ex) {
			throw new TimeoutException("Witing for response message timeout.", ex);
		} catch (InterruptedException ex) {
			throw new BusException("Problem with sending message...", ex);
		} catch (ExecutionException ex) {
			throw new BusException("Problem with sending message...", ex);
		} finally {
			rrEndPoint.close();
		}
	}

	@Override
	public final void setTimeout(int timeout) {
		throw new IllegalStateException(
				"There is no way to set timeout for pooled endpoint. Timeout is managed by RequestResponseEndPoint implementation.");
	}

	@Override
	public final int getTimeout() {
		throw new IllegalStateException(
				"There is no way to get timeout for pooled endpoint. Timeout is managed by RequestResponseEndPoint implementation.");
	}

	/**
	 * This is a callback interface to provide
	 * factory method <code>create</code> for
	 * creation implementation of the <code>RequestResponseEndPoint</code>
	 * related to chosen transport implementation.
	 *
	 */
	public interface PooledRREPCreateCallback {
		RequestResponseEndPoint create();
	}
}
