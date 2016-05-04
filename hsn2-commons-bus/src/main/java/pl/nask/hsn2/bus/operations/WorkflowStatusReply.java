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
 * This is response operation for <code>WorkflowStatusRequest</code>.
 * 
 *
 */
public class WorkflowStatusReply implements Operation {

	/**
	 * The workflow is valid.
	 */
	private boolean valid;

	/**
	 * The workflow is enabled.
	 */
	private boolean enabled;

	/**
	 * The workflow revision information.
	 */
	private WorkflowRevisionInfo info;
	
	/**
	 * Optional workflow description.
	 */
	private String description;

	/**
	 * Default constructor.
	 * 
	 * @param valid Flag if the workflow is valid. 
	 * @param enabled Flag if the workflow is enabled.
	 * @param info Revision information about the workflow.
	 */
	public WorkflowStatusReply(boolean valid, boolean enabled, WorkflowRevisionInfo info) {
		this(valid, enabled, info, null);
	}

	/**
	 * Constructor with description provided.
	 * 
	 * @param valid Flag if the workflow is valid. 
	 * @param enable Flag if the workflow is enabled.
	 * @param info Revision information about the workflow.
	 * @param description Description of the workflow.
	 */
	public WorkflowStatusReply(boolean valid, boolean enable, WorkflowRevisionInfo info, String description) {
		this.valid = valid;
		this.enabled = enable;
		this.info = info;
		this.description = description;
	}

	/**
	 * Gets description of the workflow.
	 * 
	 * @return Description of the workflow.
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Sets description of the workflow.
	 * 
	 * @param description Description of the workflow.
	 */
	public final void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Checks if valid flag is set.
	 * 
	 * @return The workflow is valid.
	 */
	public final boolean isValid() {
		return valid;
	}

	/**
	 * Checks if valid flag is set.
	 * 
	 * @return <code>true</code> if the workflow is enabled,
	 *         <code>false> otherwise
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * Gets revision info about the workflow.
	 * 
	 * @return revision info for the workflow.
	 */
	public final WorkflowRevisionInfo getInfo() {
		return info;
	}
}
