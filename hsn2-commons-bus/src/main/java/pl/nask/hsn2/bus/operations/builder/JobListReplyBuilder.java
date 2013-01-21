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

package pl.nask.hsn2.bus.operations.builder;

import java.util.List;

import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.JobListReply;

/**
 * This is a builder for <code>JobListReply</code> operation.
 * 
 *
 */
public class JobListReplyBuilder implements OperationBuilder<JobListReply> {
	
	/**
	 * Internal variable
	 */
	private JobListReply jobListReply;

	/**
	 * Default constructor with no arguments.
	 */
	public JobListReplyBuilder() {
		this.jobListReply = new JobListReply();
	}

	/**
	 * Constructor with jobs information provided.
	 * 
	 * @param jobsInfo Jobs information.
	 */
	public JobListReplyBuilder(List<JobInfo> jobsInfo) {
		setJobsInfo(jobsInfo);
	}
	
	/**
	 * Sets jobs information.
	 * 
	 * @param jobsInfo Jobs information.
	 * @return This builder instance.
	 */
	public final JobListReplyBuilder setJobsInfo(List<JobInfo> jobsInfo) {
		jobListReply.setJobs(jobsInfo);
		return this;
	}

	/**
	 * Adds information about job.
	 * 
	 * @param jobInfo Information about job.
	 * @return This builder instance.
	 */
	public final JobListReplyBuilder addJobInfo(JobInfo jobInfo) {
		this.jobListReply.getJobs().add(jobInfo);
		return this;
	}

	/**
	 * Returns current JobListReply instance.
	 * 
	 * @return Actual JobListReply instance.
	 */
	@Override
	public final JobListReply build() {
		return this.jobListReply;
	}
}
