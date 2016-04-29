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

import java.util.ArrayList;
import java.util.List;

/**
 * This is an operation to check all jobs information from a framework.
 * 
 *
 */
public class JobListReply implements Operation {

	/**
	 * List of jobs information.
	 */
	private List<JobInfo> jobs;
	
	/**
	 * Default constructor with empty jobs information list.
	 */
	public JobListReply() {
		this.jobs = new ArrayList<JobInfo>();
	}

	/**
	 * Constructor with job information list.
	 * 
	 * @param jobs List of jobs information.
	 */
	public JobListReply(List<JobInfo> jobs) {
		this.jobs = jobs;
	}

	/**
	 * Gets list of jobs information.
	 * 
	 * @return List of jobs information.
	 */
	public final List<JobInfo> getJobs() {
		return jobs;
	}

	/**
	 * Sets list of jobs information.
	 * 
	 * @param jobs List of jobs information.
	 */
	public final void setJobs(List<JobInfo> jobs) {
		this.jobs = jobs;
	}
}
