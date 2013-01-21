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

/**
 * This is an operation send by a framework to Data/Object store.
 * 
 *
 */
public class JobStarted implements Operation {

	/**
	 * Identifier of started job.
	 */
	private long jobId;
	
	/**
	 * Default constructor.
	 * 
	 * @param jobId Identifier of the job.
	 */
	public JobStarted(long jobId) {
		this.jobId = jobId;
	}

	/**
	 * Gets identifier of just started job.
	 */
	public final long getJobId() {
		return jobId;
	}

	/**
	 * Sets identifier of just started job.
	 * 
	 * @param jobId Identifier of the job.
	 */
	public final void setJobId(long jobId) {
		this.jobId = jobId;
	}
}
