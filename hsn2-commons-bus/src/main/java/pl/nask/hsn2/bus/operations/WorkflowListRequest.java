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
 * This is request for list of workflows operation.
 * 
 *
 */
public class WorkflowListRequest implements Operation {

	/**
	 * Flag if request needs only enabled workflows.
	 */
	private boolean enabledOnly;
	
	/**
	 * Default constructor. All workflows request.
	 */
	public WorkflowListRequest() {
		this(false);
	}
	
	/**
	 * Constructor with flag if only enabled workflows will be requested.
	 * 
	 * @param enabledOnly If <code>true</code> only enabled workflows will be returned,
	 *                    if <code>flase</code> all workflows will be returned.
	 */
	public WorkflowListRequest(boolean enabledOnly) {
		this.enabledOnly = enabledOnly;
	}

	/**
	 * Checks if only enabled flag is set.
	 * 
	 * @return <code>true</code> if request is for only enabled workflows,
	 *         <code>false</code> if request is for all workflows.
	 */
	public final boolean isEnabledOnly() {
		return enabledOnly;
	}

	/**
	 * Sets flag only enabled workflows of the request.
	 *  
	 * @param enabledOnly The flag of only enables workflows.
	 */
	public final void setEnabledOnly(boolean enabledOnly) {
		this.enabledOnly = enabledOnly;
	}
}
