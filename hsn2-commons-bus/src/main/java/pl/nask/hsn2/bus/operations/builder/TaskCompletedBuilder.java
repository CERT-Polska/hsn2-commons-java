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

import java.util.List;
import java.util.Set;

import pl.nask.hsn2.bus.operations.TaskCompleted;

/**
 * This is a builder for <code>TaskCompleted</code> operation.
 * 
 *
 */
public class TaskCompletedBuilder implements OperationBuilder<TaskCompleted> {

	/**
	 * Internal variable.
	 */
	private TaskCompleted taskCompleted;
	
	public TaskCompletedBuilder(long jobId, int taskId) {
		this.taskCompleted = new TaskCompleted(jobId, taskId);
	}
	
	/**
	 * Ands warning to the warnings list.
	 * 
	 * @param warning Warning to be added.
	 */
	public final TaskCompletedBuilder addWarning(String warning) {
		this.taskCompleted.getWarnings().add(warning);
		return this;
	}
	
	/**
	 * Adds identifier of new created object.
	 * 
	 * @param objectId Object identifier.
	 */
	public final TaskCompletedBuilder addNewObjectId(long objectId) {
		this.taskCompleted.getObjects().add(objectId);
		return this;
	}

	/**
	 * Sets warnings list for task accepted operation.
	 * 
	 * @param warnings List of warnings.
	 * @return This builder instance.
	 */
	public final TaskCompletedBuilder setWarnings(List<String> warnings) {
		this.taskCompleted.setWarnings(warnings);
		return this;
	}

	/**
	 * Sets set of newly created objects.
	 * 
	 * @param objects Set of objects identifiers.
	 * @return This builder instance.
	 */
	public final TaskCompletedBuilder setObjects(Set<Long> objects) {
		this.taskCompleted.setObjects(objects);
		return this;
	}

	/**
	 * Returns current TaskCompleted instance.
	 * 
	 * @return Actual TaskCompleted instance.
	 */
	@Override
	public final TaskCompleted build() {
		return this.taskCompleted;
	}
}
