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

package pl.nask.hsn2.bus.recovery;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is simple implementation of Recovery Monitor.
 * 
 * Basically it's designed for bus implementations but it can
 * be used also by other services which need to be kept
 * in correct state.
 * 
 *
 */
public class RecoveryMonitor {

	private static final long RECOVERY_INTERVAL = 5000; // 5sec
	private static final String DEFAULT_RECOVERY_THREAD_NAME = "RecoveryMonitorThread";
	private static final Logger LOGGER = LoggerFactory.getLogger(RecoveryMonitor.class);
	
	private boolean running = false;
	private long recoveryInterval = RECOVERY_INTERVAL;
	private Thread recoveryMonitorThread;
	private String recoveryThreadName;
	private Object mutex;
	
	private List<Recoverable> recoverables = Collections.synchronizedList(new LinkedList<Recoverable>());

	/**
	 * Default constructor.
	 */
	public RecoveryMonitor() {
		this(DEFAULT_RECOVERY_THREAD_NAME);
	}

	/**
	 * Default constructor with name for recovery thread provided.
	 */
	public RecoveryMonitor(String threadName) {
		this.recoveryThreadName = threadName;
		this.mutex = this;
	}

	/**
	 * Sets interval how frequent RM should monitor recoverables.
	 * @param recoveryInterval Interval in miliseconds.
	 */
	public final void setRecoveryInterval(long recoveryInterval) {
		this.recoveryInterval = recoveryInterval;
	}

	/**
	 * Starts Recovery Monitor.
	 */
	public final void start() {
		if (!this.running) {
			synchronized(mutex) {
				this.recoveryMonitorThread = new Thread(new RecoveryThread(), this.recoveryThreadName);
				this.recoveryMonitorThread.start();
				this.running = true;
			}
		}
	}

	/**
	 * Stops Recovery Monitor.
	 */
	public final void stop() {
		synchronized (mutex) {
			if (running) {
				this.running = false;
				if (recoveryMonitorThread != null
						&& !recoveryMonitorThread.isInterrupted()) {
					recoveryMonitorThread.interrupt();
				}
			}
		}
		if (recoveryMonitorThread != null) {
			LOGGER.info("Waiting for RecoveryMonitor to stop.");
			while (recoveryMonitorThread.isAlive()) {
				try {
					Thread.sleep(50);
				} catch(InterruptedException ex) {
					// nothing to do
				}
			}
			this.recoveryMonitorThread = null;
			LOGGER.info("RecoveryMonitor stopped.");
		}
	}

	/**
	 * Checks if RecoveryMonitor is running.
	 * @return <code>true</code> if RM running, <code>false</code> otherwise.
	 */
	public final boolean isRunning() {
		synchronized (mutex) { return this.running; }
	}

	/**
	 * Register the <code>Recoverable</code> to be monitored.
	 * 
	 * @param recoverable Recoverable to be monitored by RecoverMonitor. 
	 */
	public final void registerRecoverable(Recoverable recoverable) {
		this.recoverables.add(recoverable);
	}

	/**
	 * Unregister the <code>Recoverable</code>.
	 * 
	 * @param recoverable Recoverable to be removed from monitor entities.
	 */
	public final void unregisterRecoverable(Recoverable recoverable) {
		if (!recoverables.remove(recoverable)) {
			LOGGER.warn("Recoverables list doesn't contain this recoverable: {}", recoverable);
		}
	}
	
	/**
	 * This is monitor thread used by RecoveryMonitor.
	 * 
		 *
	 */
	private class RecoveryThread implements Runnable {
		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(recoveryInterval);
					for (Recoverable recoverable : recoverables) {
						LOGGER.debug("Recovering if needed: {}", recoverable.getClass().getSimpleName());
						recoverable.recovery();
					}
				} catch (InterruptedException e) {
					LOGGER.debug("Recovery monitor interrupted!");
					Thread.currentThread().interrupt();
					synchronized (mutex) { running = false; }
				}
			}
			LOGGER.info("RecoveryMonitor stops.");
		}
	}
}
