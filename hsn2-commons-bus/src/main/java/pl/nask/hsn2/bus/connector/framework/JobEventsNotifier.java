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

package pl.nask.hsn2.bus.connector.framework;

import pl.nask.hsn2.bus.connector.AbstractServicesConnector;
import pl.nask.hsn2.bus.operations.JobStatus;

/**
 * This is an interface for notifying about job processing.
 * 
 * 
 */
public interface JobEventsNotifier extends AbstractServicesConnector{

	/**
	 * Notifies that job has been started.
	 * 
	 * @param jobId
	 *            Job Identifier
	 */
	void jobStarted(long jobId);

	/**
	 * Notifies that the job has finished.
	 * 
	 * @param jobId
	 *            Job identifier
	 * @param status
	 *            Final status of the job
	 */
	void jobFinished(long jobId, JobStatus status);

	/**
	 * Sends reminder that the job has finished already (or does not exists). Used in case when service (for any reason)
	 * has not been informed that job is finished and is still processing tasks for it. Does not send more than one
	 * reminder message per minute.
	 * 
	 * @param jobId
	 *            Job identifier
	 * @param status
	 *            Final status of the job
	 * @param offendingTask
	 *            Task that raised notification
	 */
	void jobFinishedReminder(long jobId, JobStatus status, int offendingTask);
}
