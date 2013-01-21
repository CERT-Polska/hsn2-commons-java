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

package pl.nask.hsn2.bus.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is task completed operation sends by service
 * when finished processing of the task.
 * 
 *
 */
public class TaskCompleted implements Operation {

	/**
	 * Identifier of the job which task belongs to.
	 */
	private long jobId;
	
	/**
	 * Identifier of the task.
	 */
	private int taskId;
	
	/**
	 * Non-fatal service errors.
	 */
	private List<String> warnings;

	/**
	 * Set of new objects created in Object Store.
	 */
	private Set<Long> objects;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId Identifier of the job.
	 * @param taskId Identifier of the task.
	 */
	public TaskCompleted(long jobId, int taskId) {
		this(jobId, taskId, new ArrayList<String>());
	}

	/**
	 * Constructor with list of warnings provided.
	 * 
	 * @param jobId Identifier of the job.
	 * @param taskId Identifier of the task.
	 * @param warnings List of service warnings.
	 */
	public TaskCompleted(long jobId, int taskId, List<String> warnings) {
		this(jobId, taskId, warnings, new HashSet<Long>());
	}
	
	/**
	 * Constructor with list of warnings and new created objects provided.
	 * 
	 * @param jobId Identifier of the job.
	 * @param taskId Identifier of the task.
	 * @param warnings List of service warnings.
	 * @param objects Set of ne created objects.
	 */
	public TaskCompleted(long jobId, int taskId, List<String> warnings, Set<Long> objects) {
		this.jobId = jobId;
		this.taskId = taskId;
		this.warnings = warnings;
		this.objects = objects;
	}
	
	/**
	 * Gets non-fatal service errors.
	 * 
	 * @return Service warnings.
	 */
	public final List<String> getWarnings() {
		return warnings;
	}

	
	/**
	 * Sets non-fatal service errors.
	 * 
	 * @param warnings Service warnings.
	 */
	public final void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	/**
	 * Gets set of new created objects.
	 * 
	 * @return Set of objects identifiers.
	 */
	public final Set<Long> getObjects() {
		return objects;
	}

	/**
	 * Sets a set of new created objects identifiers.
	 * 
	 * @param objects Objects identifiers.
	 */
	public final void setObjects(Set<Long> objects) {
		this.objects = objects;
	}

	/**
	 * Gets the job identifier.
	 * 
	 * @return Job identifier.
	 */
	public final long getJobId() {
		return jobId;
	}

	/**
	 * Gets the task identifier.
	 * 
	 * @return Task identifier.
	 */
	public final int getTaskId() {
		return taskId;
	}
	
	@Override
	public String toString() {
		return new StringBuffer(128).append("TaskCompleted={")
				.append("jobId=").append(jobId).append(",")
				.append("taskId=").append(taskId).append(",")
				.append("objects=[").append(objects).append("],")
				.append("warnings=[").append(warnings).append("}")
				.toString();
	}
}
