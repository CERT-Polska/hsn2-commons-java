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
 * This is attribute representation operation.
 * 
 *
 */
public class Attribute implements Operation {

	/**
	 * The name of the attribute;
	 */
	private final String name;
	
	/**
	 * The type of the attribute.
	 */
	private AttributeType type;
	
	/**
	 * The value of the attribute.
	 */
	private Object value;

	/**
	 * Default constructor creates empty attribute.
	 * 
	 * @param name Name of the attribute.
	 */
	public Attribute(String name) {
		this(name, AttributeType.EMPTY, null);
	}

	/**
	 * Creates attribute with provided type and value.
	 * 
	 * @param name Name of the attribute.
	 * @param type Type of the attribute
	 * @param value Value of the attribute.
	 * 
	 * @throws IllegalArgumentException if the name is an empty String or the value doesn't fit the type (@see {@link #setValue(AttributeType, Object)})
	 */
	public Attribute(String name, AttributeType type, Object value) {
		if (name == null || "".equals(name)) {
			throw new IllegalArgumentException("Name of the attribute cannot be neither null or empty string.");
		}
		
		this.name = name;
		setValue(type, value);
	}
	
	/**
	 * Sets attribute with type and value.
	 * 
	 * @param type Type of the attribute.
	 * @param value The value of the attribute.
	 * @throws IllegalArgumentException if the type is null or if the value does not fit the type parameter.
	 */
	public final void setValue(AttributeType type, Object value) {
		if (type == null) {
			throw new IllegalArgumentException("Attribute (" + name + ") type cannot be null.");
		}

		if (value == null
				&& type != AttributeType.EMPTY) {
			throw new IllegalStateException("Null value allowed for EMPTY type only. name: " + name +", type: " + type.name());
		}
		
		type.assertFits(value);

		this.type = type;
		this.value = value;
	}

	/**
	 * Sets value of a simple type (EMPTY, STRING, INT, TIME, BOOL, FLOAT). For OBJECT and BYTES use {@link #setObjectRef(Long))} and {@link #setDataRef(Reference))}.
	 * The type of an attribute is derived from the value's class.
	 * 
	 * @param value Value to be set.
	 */
	public final void setSimpleValue(Object value) {
		if (value == null) {
			this.type = AttributeType.EMPTY;
		} else if (value instanceof String) {
			this.type = AttributeType.STRING;
		} else if (value instanceof Integer) {
			this.type = AttributeType.INT;
		} else if (value instanceof Long) {
			this.type = AttributeType.TIME;
		} else if (value instanceof Boolean) {
			this.type = AttributeType.BOOL;
		} else if (value instanceof Float) {
			this.type = AttributeType.FLOAT;
		} else {
			throw new IllegalArgumentException("Cannot map value to Attribute (" + name + "): " + value + ", " + value.getClass());
		}

		this.value = value;
	}
	
	/**
	 * Sets the value of OBJECT type to the attribute
	 * 
	 * @param value Value to be set
	 * @throws IllegalArgumentException if the argument is null
	 */
	public final void setObjectRef(Long value) {
		setValue(AttributeType.OBJECT, value);
	}
	
	/**
	 * Sets the value of BYTES type to the attribute
	 * 
	 * @param value Value to be set
	 * @throws IllegalArgumentException if the argument is null
	 */
	public final void setDataRef(Reference value) {
		setValue(type, value);
	}
	
	/**
	 * Return current value.
	 * 
	 * @return The value of attribute.
	 */
	public final Object getValue() {
		return this.value;
	}

	/**
	 * Gets name of the attribute.
	 * 
	 * @return Name of the attribute.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets type of the attribute.
	 * 
	 * @return Type of the attribute.
	 */
	public final AttributeType getType() {
		return type;
	}
	
	/**
	 * Gets boolean value of the attribute.
	 * 
	 * @return Attribute value.
	 */
	public final boolean getBool() {
		if (type != AttributeType.BOOL) {
			throw new IllegalStateException("Can't get bool value for attribute (" + name + ") type " + type);
		}
		return (Boolean) value;
	}
	
	/**
	 * Gets integer value of the attribute.
	 * 
	 * @return Attribute value.
	 */
	public final int getInteger() {
		if (type != AttributeType.INT) {
			throw new IllegalStateException("Can't get int value for attribute (" + name + ") type " + type);
		}
		return (Integer) value;
	}
	
	/**
	 * Gets time value of the attribute.
	 * 
	 * @return Attribute value.
	 */
	public final long getTime() {
		if (type != AttributeType.TIME) {
			throw new IllegalStateException("Can't get time value for attribute (" + name + ") type " + type);
		}
		return (Long) value;
	}

	/**
	 * Gets float value of the attribute.
	 * 
	 * @return Attribute value.
	 */
	public final float getFloat() {
		if (type != AttributeType.FLOAT) {
			throw new IllegalStateException("Can't get float value for attribute (" + name + ") type " + type);
		}
		return (Float) value;
	}

	/**
	 * Gets string value of the attribute.
	 * 
	 * @return Attribute value.
	 */
	public final String getString() {
		if (type != AttributeType.STRING) {
			throw new IllegalStateException("Can't get string value for attribute (" + name + ") type " + type);
		}
		return (String) value;
	}

	/**
	 * Gets object ref value of the attribute.
	 * 
	 * @return Attribute value.
	 */
	public final long getObejectRef() {
		if (type != AttributeType.OBJECT) {
			throw new IllegalStateException("Can't get object ref value for attribute (" + name + ") type " + type);
		}
		return (Long) value;
	}

	/**
	 * Gets raw data value of the attribute.
	 * 
	 * @return Attribute value.
	 */
	public final Reference getBytes() {
		if (type != AttributeType.BYTES) {
			throw new IllegalStateException("Can't get bytes value for attribute type (" + name + ") " + type);
		}
		return (Reference) value;
	}

	/**
	 * Checks if attribute has expected type value.
	 * 
	 * @param type Expected type of the attribute.
	 * @return <code>true</code> if attribute has expected type.
	 */
	public final boolean isTypeOf(AttributeType type) {
		return (this.type == type);
	}

	@Override
	public final String toString() {
		return new StringBuilder(64)
			.append("Attribute={")
			.append("name=").append(name).append(",")
			.append("type=").append(type).append(",")
			.append("value=").append(value).append("}")
			.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attribute other = (Attribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
