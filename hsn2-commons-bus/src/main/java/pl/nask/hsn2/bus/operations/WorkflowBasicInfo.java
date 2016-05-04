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
 * This is an operation providing basic information about workflow.
 * 
 *
 */
public class WorkflowBasicInfo implements Operation {

	/**
	 * The name of a workflow.
	 */
	private String name;
	
	/**
	 * Flag if workflow is enabled.
	 */
	private boolean enabled;

	/**
	 * Default constructor.
	 * 
	 * @param name The name of a workflow.
	 */
	public WorkflowBasicInfo(String name) {
		this(name, false);
	}
	
	/**
	 * Constructor with enabled flag provided.
	 * 
	 * @param name The name of a workflow.
	 * @param enabled Flag if workflow is enabled.
	 */
	public WorkflowBasicInfo(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
	}

	/**
	 * Chacks flag of a workflow is enabled.
	 * 
	 * @return <code>true</code> if enabled flag set.
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets enabled flag.
	 * 
	 * @param enabled Enabled flag.
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets name of a workflow.
	 * 
	 * @return Tha neme of a workflow.
	 */
	public final String getName() {
		return name;
	}
}
