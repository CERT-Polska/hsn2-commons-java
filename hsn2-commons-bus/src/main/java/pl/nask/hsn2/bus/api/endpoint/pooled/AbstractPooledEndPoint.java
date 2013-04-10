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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pl.nask.hsn2.bus.api.BusException;

/**
 * This is <code>abstract</code> class for all pooled endpoints.
 * It's aggregates management of <code>ExecutorService</code>.
 * 
 *
 */
public abstract class AbstractPooledEndPoint {

	private static final int DEFAULT_MAX_THREADS = 10;
	private static final int DEFAULT_FINISH_WAIT = 30; // in seconds
	
//	private ExecutorService executor = null;
	private ExecutorService hp_executor = null;
	private ExecutorService lp_executor = null;
	private boolean closed = true;
	private int maxThreads = DEFAULT_MAX_THREADS;
	private int waitForFinishTime = DEFAULT_FINISH_WAIT;

	/**
	 * Default constructor with max threads variable.
	 * 
	 * @param maxThreads Maximum number of threads property
	 *                   to be used by <code>ExecutorServoce</code> 
	 */
	protected AbstractPooledEndPoint(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * Default constructor.
	 * 
	 * @throws BusException 
	 */
	protected AbstractPooledEndPoint() {
		this(DEFAULT_MAX_THREADS);
	}

	/**
	 * Gets current value of max number of threads sets for this endpoint.
	 * 
	 * @return Maximum number of threads.
	 */
	protected final int getMaxThreads() {
		return this.maxThreads;
	}

	/**
	 * Gets <code>ExecutorService</code> instance for this endpoint.
	 * 
	 * @return <code>ExecutorService</code> instance
	 *         or null id endpoint is not open.
	 */
	@Deprecated //TODO test performance
	protected final ExecutorService getExecutor() {
//		return executor;
		return lp_executor; 
	}

	/**
	 * Internal check if endpoint is closed.
	 * 
	 * @return <code>true</code> if endpoint open,
	 * 	       <code>false> otherwise.
	 */
	public final boolean isClosed() {
		return closed;
	}

	/**
	 * Internal endpoint open operation.
	 * 
	 * @throws BusException The exception will thrown if any error.
	 */
	public final void open() throws BusException {
		if (closed) {
//			this.executor = Executors.newFixedThreadPool(maxThreads);
			this.hp_executor = Executors.newFixedThreadPool(maxThreads);
			this.lp_executor = Executors.newFixedThreadPool(maxThreads);
		}
		closed = false;
	}

	/**
	 * Internal endpoint close operation.
	 * 
	 * @throws BusException The exception will thrown if any error.
	 */
	public final void close() throws BusException {
		if (!closed) {
			try {
//				executor.shutdown();
//				executor.awaitTermination(waitForFinishTime, TimeUnit.SECONDS);
				this.hp_executor.shutdown();
				this.lp_executor.shutdown();
				if ( !this.hp_executor.awaitTermination(waitForFinishTime, TimeUnit.SECONDS)) {
					this.hp_executor.shutdownNow();
				}
				if ( !this.lp_executor.awaitTermination(waitForFinishTime, TimeUnit.SECONDS) ) {
					this.lp_executor.shutdownNow();
				}
				
			} catch (InterruptedException e) {
				// not important in this case
			} finally {
				closed = true;
//				executor = null;
				this.hp_executor = null;
				this.lp_executor = null;
			}
		}
		
	}

	/**
	 * Sets timeout for executor shutdown operation.
	 * After invocation of <code>close()</code> operation
	 * it will wait certain of time to shutdown executor
	 * to make sure executor process all scheduled tasks. 
	 * 
	 * @param seconds Wait timeout in seconds.
	 */
	public final void setWaitForFinishTime(int seconds) {
		this.waitForFinishTime = seconds;
	}
}
