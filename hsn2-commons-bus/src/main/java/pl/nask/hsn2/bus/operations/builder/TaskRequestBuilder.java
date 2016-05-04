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

package pl.nask.hsn2.bus.operations.builder;

import java.util.Properties;

import pl.nask.hsn2.bus.operations.TaskRequest;

/**
 * This is a builder for <code>TaskRequest</code> operation.
 * 
 *
 */
public class TaskRequestBuilder implements OperationBuilder<TaskRequest> {

	/**
	 * Internal variable.
	 */
	private TaskRequest taskRequest;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId Job identifier.
	 * @param taskId Task identifier.
	 * @param objectId initial object identifier.
	 */
	public TaskRequestBuilder(long jobId, int taskId, long objectId) {
		this.taskRequest = new TaskRequest(jobId, taskId, objectId);
	}
	
	/**
	 * Constructor with parameters argument.
	 * @param jobId Job identifier.
	 * @param taskId Task identifier.
	 * @param objectId Initial object identifier.
	 * @param parameters Paramaters for the task.
	 */
	public TaskRequestBuilder(long jobId, int taskId, long objectId, Properties parameters) {
		this.taskRequest = new TaskRequest(jobId, taskId, objectId, parameters);
	}
	
	/**
	 * Adds parameter for the task.
	 * 
	 * @param name Name of the parameter.
	 * @param value Value of the parameter.
	 * @return This builder instance.
	 */
	public final TaskRequestBuilder addParameter(String name, String value) {
		this.taskRequest.addParameter(name, value);
		return this;
	}
	
	/**
	 * Returns current TaskRequest instance.
	 * 
	 * @return Actual TaskRequest instance.
	 */
	@Override
	public final TaskRequest build() {
		return this.taskRequest;
	}

}
