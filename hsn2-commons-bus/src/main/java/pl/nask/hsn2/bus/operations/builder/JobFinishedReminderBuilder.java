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

import pl.nask.hsn2.bus.operations.JobFinished;
import pl.nask.hsn2.bus.operations.JobStatus;

public class JobFinishedReminderBuilder implements OperationBuilder<JobFinished> {

	/**
	 * Internal variable
	 */
	private JobFinished jobFinished;

	/**
	 * Default constructor.
	 * 
	 * @param jobId Identifier of the job.
	 * @param status Status of the job.
	 */
	public JobFinishedReminderBuilder(long jobId, JobStatus status) {
		this.jobFinished = new JobFinished(jobId, status);
	}

	/**
	 * Returns current Job JobStarted instance.
	 * 
	 * @return Actual JobStarted instance.
	 */
	@Override
	public final JobFinished build() {
		return this.jobFinished;
	}

}
