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
 * This is a notification operation about just finished job.
 * 
 *
 */
public class JobFinished implements Operation {

	/**
	 * Identifier of finished job.
	 */
	private long jobId;
	
	/**
	 * Status of finished job.
	 */
	private JobStatus status;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId Identifier of just finished job.
	 * @param status Final status of the job.
	 */
	public JobFinished(long jobId, JobStatus status) {
		this.jobId = jobId;
		this.status = status;
	}

	/**
	 * Gets finished job identifier.
	 * 
	 * @return Job identifier.
	 */
	public final long getJobId() {
		return jobId;
	}

	/**
	 * Sets finished job identifier.
	 * 
	 * @param jobId Job identifier.
	 */
	public final void setJobId(long jobId) {
		this.jobId = jobId;
	}

	/**
	 * Gets final job status.
	 * 
	 * @return Job status.
	 */
	public final JobStatus getStatus() {
		return status;
	}

	/**
	 * Sets final job status.
	 * 
	 * @param status Job status.
	 */
	public final void setStatus(JobStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return new StringBuffer(128).append("JobFinished={")
				.append("jobId=").append(jobId).append(",")
				.append("status=").append(status).append("}")
				.toString();
	}
}
