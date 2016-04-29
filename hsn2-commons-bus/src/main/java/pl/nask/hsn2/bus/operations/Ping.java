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
 * This is simple ping operation.
 * 
 *
 */
public class Ping implements Operation {

	/**
	 * Reqiest or response.
	 */
	private boolean response = false;
	
	/**
	 * Default constructor of ping request.
	 */
	public Ping() {
		this.response = false;
	}
	
	/**
	 * Constructor with param if it's request or response.
	 * @param response
	 */
	public Ping(boolean response) {
		this.response = response;
	}

	/**
	 * Checks if it's response.
	 * @return <code>true</code> if it's response.
	 */
	public final boolean isResponse() {
		return response;
	}

	/**
	 * Sets response flag.
	 * 
	 * @param response Response flag.
	 */
	public final void setResponse(boolean response) {
		this.response = response;
	}
}
