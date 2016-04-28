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

package pl.nask.hsn2.connector.REST;

public class DataResponse {
	private final boolean isSuccesful;
	private final long keyId;
	private final String error;

	/**
	 * Constructor used to get successful data response.
	 * 
	 * @param key
	 *            Key ID of the object.
	 */
	public DataResponse(long key) {
		keyId = key;
		isSuccesful = true;
		error = null;
	}

	/**
	 * Constructor used to get successful data response.
	 * 
	 * @param key
	 *            Key ID of the object.
	 */
	public DataResponse(String errorMsg) {
		error = errorMsg;
		isSuccesful = false;
		keyId = -1;
	}

	/**
	 * Returns response success state.
	 * 
	 * @return <code>true</code> if connection was successful, <code>false</code> otherwise.
	 */
	public boolean isSuccesful() {
		return isSuccesful;
	}

	/**
	 * Returns key ID of stored object.
	 * 
	 * @return Key ID of stored object. If storing wasn't successful it returns <code>-1</code>.
	 */
	public long getKeyId() {
		return keyId;
	}

	/**
	 * Returns error message, if there is any.
	 * 
	 * @return Error message or <code>null</code> if action was successful.
	 */
	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return DataResponse.class.getSimpleName() + "{success=" + isSuccesful + ",key=" + keyId + ",errorMsg=" + error + "}";
	}
}
