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
 * This is an operation with information about job status.
 * 
 *
 */
public class JobInfo implements Operation {

	/**
	 * Identifier of the job.
	 */
	private long id;
	
	/**
	 * Status of the job.
	 */
	private JobStatus status;
	
	/**
	 * Default constructor.
	 * 
	 * @param id Identifier of the job.
	 * @param status Status of the job.
	 */
	public JobInfo(long id, JobStatus status) {
		this.id = id;
		this.status = status;
	}

	/**
	 * Gets job identifier.
	 * @return Job identifier.
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Sets job identifier.
	 * 
	 * @param id Job identifier.
	 */
	public final void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets job status.
	 * 
	 * @return Job status.
	 */
	public final JobStatus getStatus() {
		return status;
	}

	/**
	 * Sets job status.
	 * 
	 * @param status Job status.
	 */
	public final void setStatus(JobStatus status) {
		this.status = status;
	}
}
