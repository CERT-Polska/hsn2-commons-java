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

package pl.nask.hsn2.bus.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is configurable <code>ThreadPoolExecutor</code>.
 * 
 *
 */
public class ConfigurableExecutorService extends ThreadPoolExecutor {

	/**
	 * Default numer of threads.
	 */
	private static final int NUMER_OF_THREADS = 5;
	
	/**
	 * Default threads prefix.
	 */
	private static final String DEFAULT_THREADS_PREFIX = "executor-";

	/**
	 * Default empty constructor.
	 */
	public ConfigurableExecutorService() {
		this(NUMER_OF_THREADS);
	}
	
	/**
	 * Constructor with max threads.
	 *  
	 * @param numThreads Maximum number of threads managed by the pool.
	 */
	public ConfigurableExecutorService(int numThreads) {
		this(DEFAULT_THREADS_PREFIX, numThreads);
	}
	
	/**
	 * Constructor with threads prefix provided.
	 * 
	 * @param threadsPrefix Prefix for threads names.
	 */
	public ConfigurableExecutorService(String threadsPrefix) {
		this(threadsPrefix, NUMER_OF_THREADS);
	}
	
	/**
	 * Constructor with threads names prefix and maximum number of threads.
	 * 
	 * @param threadsPrefix Threads prefix.
	 * @param numThreads Maximum number of threads, managed by the pool.
	 */
	public ConfigurableExecutorService(final String threadsPrefix, final int numThreads) {
		super(numThreads, numThreads, 20L,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
				new ThreadFactory() {
					private final ThreadFactory defaultFactory = Executors
							.defaultThreadFactory();

					@Override
					public Thread newThread(Runnable r) {
						Thread thread = defaultFactory.newThread(r);
						thread.setName(threadsPrefix + thread.getName());
						return thread;
					}
				});
	}

}
