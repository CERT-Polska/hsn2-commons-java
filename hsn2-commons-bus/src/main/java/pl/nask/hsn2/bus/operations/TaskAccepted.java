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
 * This is task acceptance operation returned form service
 * if the service will accept task request.
 * 
 *
 */
public class TaskAccepted implements Operation {

	/**
	 * Identifier of the accepted task.
	 */
	private int taskId;
	
	/**
	 * Identifier of a job which task belongs to.
	 */
	private long jobId;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId Job identifier.
	 * @param taskId Task identifier.
	 */
	public TaskAccepted(long jobId, int taskId) {
		this.jobId = jobId;
		this.taskId = taskId;
	}

	/**
	 * Gets task identifier.
	 * 
	 * @return Task identifier.
	 */
	public final int getTaskId() {
		return taskId;
	}

	/**
	 * Gets job identifier.
	 * 
	 * @return Job identifier.
	 */
	public final long getJobId() {
		return jobId;
	}
}
