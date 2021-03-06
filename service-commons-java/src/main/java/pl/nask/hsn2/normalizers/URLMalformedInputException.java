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

package pl.nask.hsn2.normalizers;

public class URLMalformedInputException extends Exception {

	public URLMalformedInputException(String msg) {
		super(msg);
	}

	public URLMalformedInputException() {
		super();
	}

	public URLMalformedInputException(Throwable e) {
		super(e);
	}

	public URLMalformedInputException(String msg, Throwable e) {
		super(msg,e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5031344636477430097L;

}
