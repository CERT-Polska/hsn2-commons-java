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
 * This is an response operation for <code>SetConfigRequest</code>.
 * 
 *
 */
public class SetConfigReply implements Operation {

	private boolean success;
	
	/**
	 * Default constructor for unsuccessful
	 * configuration set operation.
	 */
	public SetConfigReply() {
		this.success = false;
	}
	
	/**
	 * Constructor with success flag provided.
	 * @param success
	 */
	public SetConfigReply(boolean success) {
		this.success = success;
	}

	/**
	 * Checks if configuration has been applied successfully
	 * by <code>SetConfigRequest</code> operation.
	 * 
	 * @return <code>true</code> if configuration
	 *         has been applied successfully. 
	 */
	public final boolean isSuccess() {
		return success;
	}

	/**
	 * Sets a success flag.
	 * 
	 * @param success Expected success flag.
	 */
	public final void setSuccess(boolean success) {
		this.success = success;
	}
}
