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

import pl.nask.hsn2.bus.operations.JobRejected;

/**
 * This is a builder for JobRejected operation.
 * 
 *
 */
public class JobRejectedBuilder implements OperationBuilder<JobRejected> {

	/**
	 * Internal variable
	 */
	private JobRejected jobRejected;
	
	/**
	 * Constructor with no arguments. Reason will be null.
	 */
	public JobRejectedBuilder() {
		this.jobRejected = new JobRejected();
	}

	/**
	 * Constructor with reason argument.
	 * 
	 * @param reason Reason why job has been rejected by a framework.
	 */
	public JobRejectedBuilder(String reason) {
		this();
		setReason(reason);
	}

	/**
	 * Sets reason why job has been rejected by a framework.
	 * @param reason Reason of reject.
	 * @return This builder instance.
	 */
	public final JobRejectedBuilder setReason(String reason) {
		jobRejected.setReason(reason);
		return this;
	}
	
	/**
	 * Returns current Job JobRejected instance.
	 * 
	 * @return Actual JobRejected instance.
	 */
	@Override
	public final JobRejected build() {
		return this.jobRejected;
	}

}
