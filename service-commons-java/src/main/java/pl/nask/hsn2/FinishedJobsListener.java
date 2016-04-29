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

package pl.nask.hsn2;

import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.protobuff.Jobs.JobFinished;
import pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder;

public class FinishedJobsListener extends FinishedJobsListenerAbstract {
	private static final Logger LOG = LoggerFactory.getLogger(FinishedJobsListener.class);
	private ConcurrentSkipListSet<Long> knownFinishedJobs = new ConcurrentSkipListSet<>();
	
	@Override
	protected void process(JobFinished data) {
		long jobId = data.getJob();
		knownFinishedJobs.add(jobId);
		LOG.info("Finished job added to list. (id={})", jobId);
	}
	
	@Override
	protected void process(JobFinishedReminder data) {
		long jobId = data.getJob();
		knownFinishedJobs.add(jobId);
		LOG.info("Finished job added to list (reminded). (id={})", jobId);
	}

	public boolean isJobFinished(long jobId) {
		return knownFinishedJobs.contains(jobId);
	}
}
