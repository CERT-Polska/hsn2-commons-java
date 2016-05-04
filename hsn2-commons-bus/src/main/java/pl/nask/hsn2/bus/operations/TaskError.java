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

/**
 * This is a task error operation which returns from service
 * as an response for <code>TaskRequest</code> if there is an error
 * with processing task.
 * 
 *
 */
public class TaskError implements Operation {
	
	/**
	 * The job which the task belongs to.
	 */
	private long jobId;
	
	/**
	 * The task identifier.
	 */
	private int taskId;
	
	/**
	 * Reason of the error.
	 */
	private TaskErrorReasonType reason;
	
	/**
	 * More detailed description of the error. Can be null.
	 */
	private String description;

	/**
	 * Default constructor with no description.
	 * 
	 * @param jobId The job identifier.
	 * @param taskId The task identifier.
	 * @param reason The reason of the error.
	 */
	public TaskError(long jobId, int taskId, TaskErrorReasonType reason) {
		this(jobId, taskId, reason, null);
	}

	/**
	 * Constructor with the error description provided.
	 * 
	 * @param jobId The job identifier.
	 * @param taskId The task identifier.
	 * @param reason The reason of the error.
	 * @param description The more detailed description of the errror.
	 */
	public TaskError(long jobId, int taskId, TaskErrorReasonType reason, String description) {
		this.jobId = jobId;
		this.taskId = taskId;
		this.reason = reason;
		this.description = description;
	}

	/**
	 * Gets description of the error.
	 * 
	 * @return Description of the error.
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Sets description of the error.
	 * 
	 * @param description Description of the error.
	 */
	public final void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets job identifier.
	 * 
	 * @return Job identifier.
	 */
	public final long getJobId() {
		return jobId;
	}

	/**
	 * Gets task identifier.
	 * 
	 * @return The task identifier.
	 */
	public final int getTaskId() {
		return taskId;
	}

	/**
	 * Gets general reason of the error.
	 * 
	 * @return Reason of the error.
	 */
	public final TaskErrorReasonType getReason() {
		return reason;
	}
}
