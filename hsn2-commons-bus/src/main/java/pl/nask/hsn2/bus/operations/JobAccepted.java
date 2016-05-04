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
 * This is response operation for correctly job accepted by a framework.
 * 
 *
 */
public class JobAccepted implements Operation {

	/**
	 * Accepted job identifier.
	 */
	private long jobId;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId Accepted jobId.
	 */
	public JobAccepted(long jobId) {
		this.jobId = jobId;
	}

	/**
	 * Gets identifier of accepted job.
	 * 
	 * @return Job identifier.
	 */
	public final long getJobId() {
		return jobId;
	}

	/**
	 * Sets identifier of accepted job.
	 * 
	 * @param jobId Job identifier.
	 */
	public final void setJobId(long jobId) {
		this.jobId = jobId;
	}

	@Override
	public String toString() {
		return new StringBuffer(128).append("JobAccepted={")
				.append("jobId=").append(jobId).append("}")
				.toString();
	}
}
