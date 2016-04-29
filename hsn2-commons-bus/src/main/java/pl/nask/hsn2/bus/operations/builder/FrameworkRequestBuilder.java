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

package pl.nask.hsn2.bus.operations.builder;

import pl.nask.hsn2.bus.operations.FrameworkReqType;
import pl.nask.hsn2.bus.operations.FrameworkRequest;

/**
 * This is a builder for <code>FrameworkRequest</code> operation.
 * 
 *
 */
public class FrameworkRequestBuilder implements OperationBuilder<FrameworkRequest>{

	/**
	 * Internal variable.
	 */
	private FrameworkRequest frameworkRequest;
	
	/**
	 * Default constructor.
	 * 
	 * @param type Type of the framework request.
	 */
	public FrameworkRequestBuilder(FrameworkReqType type) {
		this.frameworkRequest = new FrameworkRequest(type);
	}
	
	/**
	 * Sets new request type.
	 * 
	 * @param type Type of the framework request.
	 * 
	 * @return This builder instance.
	 */
	public final FrameworkRequestBuilder setType(FrameworkReqType type) {
		this.frameworkRequest.setType(type);
		return this;
	}
	
	/**
	 * Returns current FrameworkRequest instance.
	 * 
	 * @return Actual FrameworkRequest instance.
	 */
	@Override
	public final FrameworkRequest build() {
		return this.frameworkRequest;
	}

}
