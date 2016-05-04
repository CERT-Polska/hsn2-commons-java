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

import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.JobStatus;

/**
 * This is a builder for <code>JobInfo</code> operation.
 * 
 *
 */
public class JobInfoBuilder implements OperationBuilder<JobInfo> {
	/**
	 * Internal variable
	 */
	private JobInfo jobInfo;

	/**
	 * Default constructor.
	 * 
	 * @param jobId Identifier of the job.
	 * @param status Status of the job.
	 */
	public JobInfoBuilder(long jobId, JobStatus status) {
		this.jobInfo = new JobInfo(jobId, status);
	}

	/**
	 * Returns current Job JobInfo instance.
	 * 
	 * @return Actual JobInfo instance.
	 */
	@Override
	public final JobInfo build() {
		return this.jobInfo;
	}

}
