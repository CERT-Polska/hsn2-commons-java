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

import java.util.Map;
import java.util.Set;

import pl.nask.hsn2.bus.operations.Attribute;
import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.Reference;

/**
 * This is a builder for <code>ObjectData</code> operation.
 * 
 *
 */
public class ObjectDataBuilder implements OperationBuilder<ObjectData> {

	/**
	 * Internal variable.
	 */
	private ObjectData objectData;
	
	/**
	 * Default constructor.
	 */
	public ObjectDataBuilder() {
		this.objectData = new ObjectData();
	}

	/**
	 * Sets attributes for the object data.
	 * 
	 * @param attributes Attributes to set.
	 */
	public ObjectDataBuilder(Set<Attribute> attributes) {
		this.objectData = new ObjectData(attributes);
	}

	/**
	 * Adds attribute to the object data.
	 * 
	 * @param attribute Attribute to be added.
	 * 
	 * @return This builder instance.
	 */
	public final ObjectDataBuilder addAttribute(Attribute attribute) {
		this.objectData.getAttributes().add(attribute);
		return this;
	}

	/**
	 * Adds time attribute type.
	 * 
	 * @param name Name of the attribute.
	 * @param time Value of the attribute.
	 */
	public final ObjectDataBuilder addTimeAttribute(String name, long time) {
		addAttribute(new Attribute(name, AttributeType.TIME, time));
		return this;
	}

	/**
	 * Adds int attribute.
	 * @param name Name of the attribute.
	 * @param value Value of the attribute.
	 */
	public final ObjectDataBuilder addIntAttribute(String name, int value) {
		addAttribute(new Attribute(name, AttributeType.INT, value));
		return this;
	}

	/**
	 * Adds string attribute.
	 * @param name Name of the attribute.
	 * @param value Value of the attribute.
	 */
	public final ObjectDataBuilder addStringAttribute(String name, String value) {
		addAttribute(new Attribute(name, AttributeType.STRING, value));
		return this;
	}
	
	/**
	 * Adds boolean attribute
	 * @param name
	 * @param value
	 */
	public final ObjectDataBuilder addBoolAttribute(String name, boolean value) {
		addAttribute(new Attribute(name, AttributeType.BOOL, value));
		return this;
	}
	
	/**
	 * Adds an attribute representing a reference to a resource in a data store
	 * @param name
	 * @param storeId
	 * @param referenceId
	 */
	public void addRefAttribute(String name, int storeId, long referenceId) {
		addAttribute(new Attribute(name, AttributeType.BYTES, new Reference(referenceId, storeId)));		
	}
	
	public void addObjAttribute(String name, long objectId) {
		addAttribute(new Attribute(name, AttributeType.OBJECT, objectId));
	}

	/**
	 * TODO: Comment it!
	 * @param prefix
	 * @param map1
	 * @param map2
	 */
	public final ObjectDataBuilder addMaps(String prefix, Map<String, Integer> map1, Map<String, Integer> map2) {
        for (Map.Entry<String, Integer> e: map1.entrySet()) {
            Integer i1 = map2.get(e.getKey());
            if (i1 == null)
                i1 = 0;
            addStringAttribute(prefix + e.getKey(),  i1 + "/" + e.getValue());
        }
        return this;
	}
	
	/**
	 * Sets identifier for the object data.
	 * 
	 * @param id Identifier of the object data.
	 * 
	 * @return This builder instance.
	 */
	public final ObjectDataBuilder setId(long id) {
		this.objectData.setId(id);
		return this;
	}

	/**
	 * Returns current ObjectData instance.
	 * 
	 * @return Actual ObjectData instance.
	 */

	@Override
	public final ObjectData build() {
		return this.objectData;
	}
}
