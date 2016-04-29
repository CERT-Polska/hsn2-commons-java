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

import pl.nask.hsn2.bus.operations.TaskAccepted;

/**
 * This is a builder for <code>TaskAccepted</code> operation.
 * 
 *
 */
public class TaskAcceptedBuilder implements OperationBuilder<TaskAccepted> {

	/**
	 * Internal variable.
	 */
	private TaskAccepted taskAccepted;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId Job identifier which task belongs to.
	 * @param taskId Task identifier.
	 */
	public TaskAcceptedBuilder(long jobId, int taskId) {
		this.taskAccepted = new TaskAccepted(jobId, taskId);
	}
	
	/**
	 * Returns current TaskAccepted instance.
	 * 
	 * @return Actual TaskAccepted instance.
	 */
	@Override
	public final TaskAccepted build() {
		return this.taskAccepted;
	}

}
