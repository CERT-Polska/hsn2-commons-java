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

package pl.nask.hsn2.bus.connector.objectstore;

import pl.nask.hsn2.bus.api.BusException;

/**
 * The class <code>ObjectStoreConnectorException</code> and its subclasses are a form of
 * <code>Throwable</code> that indicates conditions that a reasonable
 * application might want to catch if any problem with
 * connection to the ObjectStore occurs.
 * 
 *
 */
public class ObjectStoreConnectorException extends BusException {

	private static final long serialVersionUID = -579976522896148843L;
	
	/**
	 * String representation of ResponseType
	 */
	private String responseType;
	
	/**
	 * Detailed error.
	 */
	private String error;

	public ObjectStoreConnectorException() {
		super();
	}

	public ObjectStoreConnectorException(String message) {
		super(message);
	}

	public ObjectStoreConnectorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectStoreConnectorException(String message, String responseType, String error) {
		super(message);
		this.responseType = responseType;
		this.error = error;
	}

	public final String getResponseType() {
		return responseType;
	}

	public final String getError() {
		return error;
	}
}
