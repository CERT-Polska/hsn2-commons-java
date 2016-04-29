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

import pl.nask.hsn2.bus.operations.Reference;

/**
 * This is a builder for <code>Reference</code> operation.
 * 
 *
 */
public class ReferenceBuilder implements OperationBuilder<Reference>{

	/**
	 * Internal variable.
	 */
	private Reference reference;
	
	/**
	 * Default constructor.
	 * 
	 * @param key Object reference.
	 */
	public ReferenceBuilder(long key) {
		this.reference = new Reference(key);
	}

	/**
	 * Constructor creates reference with provided
	 * object ref and store id.
	 *  
	 * @param key Object identifier.
	 * @param store Store identifier where is the object.
	 */
	public ReferenceBuilder(long key, int store) {
		this.reference = new Reference(key, store);
	}

	/**
	 * Returns current Reference instance.
	 * 
	 * @return Actual Reference instance.
	 */
	@Override
	public final Reference build() {
		return this.reference;
	}

}
