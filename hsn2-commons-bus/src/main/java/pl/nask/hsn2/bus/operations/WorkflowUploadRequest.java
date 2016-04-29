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
 * This is a request operation to upload selected workflow into the framework.
 * 
 *
 */
public class WorkflowUploadRequest implements Operation {

	/**
	 * The name of the workflow.
	 */
	private String name;
	
	/**
	 * The content of the workflow.
	 */
	private String content;
	
	/**
	 * Flag if the workflow should be override if already exists.
	 */
	private boolean override;
	

	/**
	 * Default constructor.
	 * 
	 * @param name The name of the workflow.
	 * @param content Content of the workflow.
	 * @param override Flag if workflow must be override
	 */
	public WorkflowUploadRequest(String name, String content, boolean override) {
		this.name = name;
		this.content = content;
		this.override = override;
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
	 * Gets content of the workflow.
	 * 
	 * @return Content of the workflow.
	 */
	public final String getContent() {
		return content;
	}

	/**
	 * Checks if override flag is set.
	 * 
	 * @return Override flag.
	 */
	public final boolean isOverride() {
		return override;
	}
}
