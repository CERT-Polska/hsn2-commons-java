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

import pl.nask.hsn2.bus.operations.Attribute;
import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.Reference;

/**
 * This is a builder for <code>Attribute</code> operation.
 * 
 *
 */
public class AttributeBuilder implements OperationBuilder<Attribute> {

	/**
	 * Internal variable.
	 */
	private Attribute attribute;
	
	/**
	 * Default constructor creates empty attribute.
	 * 
	 * @param name Attribute name.
	 */
	public AttributeBuilder(String name) {
		this.attribute = new Attribute(name);
	}
	
	/**
	 * Sets boolean value for the attribute.
	 * 
	 * @param value Value for the attribute
	 * @return This builder instance.
	 */
	public final AttributeBuilder setBool(boolean value) {
		this.attribute.setValue(AttributeType.BOOL, Boolean.valueOf(value));
		return this;
	}

	/**
	 * Sets integer value for the attribute.
	 * 
	 * @param value Value for the attribute
	 * @return This builder instance.
	 */
	public final AttributeBuilder setInteger(int value) {
		this.attribute.setValue(AttributeType.INT, Integer.valueOf(value));
		return this;
	}

	/**
	 * Sets long value for the attribute.
	 * 
	 * @param value Value for the attribute
	 * @return This builder instance.
	 */
	public final AttributeBuilder setTime(long value) {
		this.attribute.setValue(AttributeType.TIME, Long.valueOf(value));
		return this;
	}

	/**
	 * Sets float value for the attribute.
	 * 
	 * @param value Value for the attribute
	 * @return This builder instance.
	 */
	public final AttributeBuilder setFloat(float value) {
		this.attribute.setValue(AttributeType.FLOAT, Float.valueOf(value));
		return this;
	}

	/**
	 * Sets string value for the attribute.
	 * 
	 * @param value Value for the attribute
	 * @return This builder instance.
	 */
	public final AttributeBuilder setString(String value) {
		this.attribute.setValue(AttributeType.STRING, value);
		return this;
	}

	/**
	 * Sets object ref value for the attribute.
	 * 
	 * @param value Value for the attribute
	 * @return This builder instance.
	 */
	public final AttributeBuilder setObjectRef(long value) {
		this.attribute.setValue(AttributeType.OBJECT, Long.valueOf(value));
		return this;
	}

	/**
	 * Sets reference to raw data for the attribute.
	 * 
	 * @param key Identifier of the object in a store
	 * @param storeId Identifier of the storage
	 * @return This builder instance.
	 */
	public final AttributeBuilder setDataRef(long key, int storeId) {
		this.attribute.setValue(AttributeType.BYTES, new Reference(key, storeId));
		return this;
	}

	/**
	 * Returns current Attribute instance.
	 * 
	 * @return Actual Attribute instance.
	 */
	@Override
	public final Attribute build() {
		return this.attribute;
	}

}
