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

package pl.nask.hsn2.bus.operations.builder;

import pl.nask.hsn2.bus.operations.InfoError;
import pl.nask.hsn2.bus.operations.InfoType;

/**
 * This is a builder for <code>InfoError</code> operation.
 * 
 *
 */
public class InfoErrorBuilder implements OperationBuilder<InfoError> {

	/**
	 * Internal variable.
	 */
	private InfoError infoError;
	
	/**
	 * Default constructor.
	 * 
	 * @param type Type of the request.
	 * @param reason Reason of the error.
	 */
	public InfoErrorBuilder(InfoType type, String reason) {
		this.infoError = new InfoError(type, reason);
	}

	/**
	 * Returns current InfoError instance.
	 * 
	 * @return Actual InfoError instance.
	 */
	@Override
	public final InfoError build() {
		return this.infoError;
	}

}
