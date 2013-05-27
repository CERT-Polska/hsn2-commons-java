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

package pl.nask.hsn2;

public class RequiredParameterMissingException extends ParameterException {
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = -2187948375051589155L;
	/**
	 * Parameter name.
	 */
	private String paramName;

	public RequiredParameterMissingException(String paramName) {
		super(String.format("No parameter with name=%s found", paramName));
		this.paramName = paramName;
	}

	public String getParamName() {
		return paramName;
	}
}
