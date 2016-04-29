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

package pl.nask.hsn2.bus.operations;

import java.util.Properties;

/**
 * This is TaskRequest operation.
 * 
 *
 */
public class TaskRequest implements Operation {

	/**
	 * Th task identifier.
	 */
	private int taskId;
	
	/**
	 * Parameters for the task.
	 */
	private Properties parameters;
	
	/**
	 * Job identifier as parent for the task.
	 */
	private long job;
	
	/**
	 * Initial object identifier.
	 */
	private long object;

	/**
	 * Default constructor creates task request with empty parameters set.
	 * 
	 * @param jobId The job identifier.
	 * @param taskId The task identifier.
	 * @param objectId Initial object identifier.
	 */
	public TaskRequest(long jobId, int taskId, long objectId) {
		this(jobId, taskId, objectId, null);
	}

	/**
	 * Constructor creates task request with provided parameters.
	 * Empty parameters set will be created if parameters argument is null.
	 * 
	 * @param jobId The job identifier.
	 * @param taskId The task identifier.
	 * @param objectId Initial object identifier.
	 * @param parameters The set of parameters for the task.
	 */
	public TaskRequest(long jobId, int taskId, long objectId, Properties parameters) {
		this.job = jobId;
		this.taskId = taskId;
		this.object = objectId;
		this.parameters = parameters;
		if (this.parameters == null) {
			this.parameters = new Properties();
		}
	}

	/**
	 * Gets the task identifier.
	 * 
	 * @return The task identifier.
	 */
	public final int getTaskId() {
		return taskId;
	}

	/**
	 * Gets the task parameters.
	 *  
	 * @return The task parameters.
	 */
	public final Properties getParameters() {
		return parameters;
	}

	/**
	 * Gets job identifier.
	 * 
	 * @return The job identifier.
	 */
	public final long getJob() {
		return job;
	}

	/**
	 * Gets initial object identifier.
	 * 
	 * @return The object identifier.
	 */
	public final long getObject() {
		return object;
	}

	/**
	 * Adds parameter to parameters set.
	 * 
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 */
	public final void addParameter(String name, String value) {
		parameters.put(name, value);
	}
}
