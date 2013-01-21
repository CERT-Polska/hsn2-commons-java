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

import pl.nask.hsn2.bus.operations.JobStarted;

/**
 * This is a builder for <code>JobStarted</code> operation.
 * 
 *
 */
public class JobStartedBuilder implements OperationBuilder<JobStarted> {

	/**
	 * Internal variable
	 */
	private JobStarted jobStarted;

	/**
	 * Default constructor.
	 * 
	 * @param jobId Identifier of the job.
	 */
	public JobStartedBuilder(long jobId) {
		this.jobStarted = new JobStarted(jobId);
	}

	/**
	 * Returns current JobStarted instance.
	 * 
	 * @return Actual JobStarted instance.
	 */
	@Override
	public final JobStarted build() {
		return this.jobStarted;
	}

}
