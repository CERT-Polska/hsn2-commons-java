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

import pl.nask.hsn2.bus.operations.TaskError;
import pl.nask.hsn2.bus.operations.TaskErrorReasonType;

/**
 * This is a builder for <code>TaskError</code> operation.
 * 
 *
 */
public class TaskErrorBuilder implements OperationBuilder<TaskError> {

	/**
	 * Internal variable.
	 */
	private TaskError taskError;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId The job identifier.
	 * @param taskId The task identifier.
	 * @param reason Reason of the error.
	 */
	public TaskErrorBuilder(long jobId, int taskId, TaskErrorReasonType reason) {
		this.taskError = new TaskError(jobId, taskId, reason);
	}
	
	/**
	 * Sets more detailed error description.
	 * 
	 * @param description Description of the error.
	 * @return This builder instance.
	 */
	public final TaskErrorBuilder setDescription(String description) {
		this.taskError.setDescription(description);
		return this;
	}

	/**
	 * Returns current TaskError instance.
	 * 
	 * @return Actual TaskError instance.
	 */
	@Override
	public final TaskError build() {
		return this.taskError;
	}
}
