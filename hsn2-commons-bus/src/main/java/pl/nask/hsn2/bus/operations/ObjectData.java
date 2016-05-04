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

import java.util.HashSet;
import java.util.Set;

/**
 * This is part of operation represents single object store entity.
 * 
 *
 */
public class ObjectData implements Operation {

	/**
	 * Attributes of object store entity.
	 */
	private Set<Attribute> attributes;
	
	/**
	 * Identifier of object store entity.
	 * Can be 0 if it's a part of PUT request.
	 */
	private long id;
	
	/**
	 * Default constructor of the entity
	 * with no id and empty attributes set.
	 */
	public ObjectData() {
		this(new HashSet<Attribute>());
	}

	/**
	 * Constructor creates object with provided attributes
	 * and identifier 0.
	 * 
	 * @param attributes Provided attributes for object.
	 */
	public ObjectData(Set<Attribute> attributes) {
		this(0, attributes);
	}

	/**
	 * Constructor creates object with provided identifier
	 * and attributes set. If attributes is null empty set
	 * will be created.
	 * 
	 * @param id Identifier for the object.
	 * @param attributes Set of attributes.
	 */
	public ObjectData(long id, Set<Attribute> attributes) {
		this.id = id;
		if (attributes == null) {
			this.attributes = new HashSet<Attribute>();
		} else {
			this.attributes = attributes;
		}
	}

	/**
	 * Gets set of attributes for the object data.
	 * 
	 * @return Set of attributes.
	 */
	public final Set<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * Sets attributes for the object data.
	 * 
	 * @param attributes Set of attributes.
	 */
	public final void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Gets identifier for the object data.
	 * 
	 * @return Identifier of the object data.
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Sets identifier for the object data.
	 * 
	 * @param id Identifier of the object data.
	 */
	public final void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Finds attribute with provided name and type.
	 * 
	 * @param name The name of looking for attribute.
	 * @param type The type of attribute.
	 * @return Found attribute or null if cannot be found.
	 */
	public final Attribute findAttribute(String name, AttributeType type) {
		
		if (name == null || type == null)
			throw new IllegalArgumentException("Illegal arguments, name and type cannot be null.");
		
		for (Attribute attr : this.attributes) {
			if (type.equals(attr.getType())
					&& name.equals(attr.getName())) {
				return attr;
			}
		}
		return null;
	}
	
	@Override
	public final String toString() {
		return new StringBuffer(128)
			.append("DataObject={")
			.append("id=").append(id).append(",")
			.append("attributes=").append(attributes)
			.append("}").toString();
	}
}
