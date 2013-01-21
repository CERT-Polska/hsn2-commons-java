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
 * This is a response operation for WorkflowPolicyRequest</code>.
 * 
 *
 */
public class WorkflowPolicyReply implements Operation {

	/**
	 * Previous value of enabled flag for the workflow.
	 */
	private boolean previous;

	/**
	 * Default constructor.
	 * 
	 * @param previous Previous value of the enabled flag.
	 */
	public WorkflowPolicyReply(boolean previous) {
		this.previous = previous;
	}

	/**
	 * Checks previous value of enabled flag.
	 *  
	 * @return <code>true</code> if previously enabled flag was set.
	 */
	public final boolean getPrevious() {
		return previous;
	}
}
