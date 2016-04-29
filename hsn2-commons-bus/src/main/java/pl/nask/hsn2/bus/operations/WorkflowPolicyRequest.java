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

/**
 * This is an operation to change workflow policy.
 * 
 *
 */
public class WorkflowPolicyRequest implements Operation {

	/**
	 * The name of the workflow.
	 */
	private String name;
	
	/**
	 * Enabled flag for the workflow.
	 */
	private boolean enabled;
	
	/**
	 * Default constructor.
	 * 
	 * @param name The name of the workflow.
	 * @param enabled Flag if workflow should be enabled.
	 */
	public WorkflowPolicyRequest(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	/**
	 * Gets name of the workflow.
	 * 
	 * @return The name of the workflow.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Checks of enabled flag is set.
	 * 
	 * @return Enabled flag is set or not.
	 */
	public final boolean isEnabled() {
		return enabled;
	}
}
