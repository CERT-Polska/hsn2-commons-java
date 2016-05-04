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

package pl.nask.hsn2.wrappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.nask.hsn2.protobuff.Object.Attribute;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;
import pl.nask.hsn2.protobuff.Object.ObjectData;

public class ObjectDataWrapper {

    private Map<String, Attribute> attributeMap = new HashMap<String, Attribute>();
    private long objectDataId;

    public ObjectDataWrapper(ObjectData objectData) {
        objectDataId = objectData.getId();

        List<Attribute> attrs = objectData.getAttrsList();
        for (Attribute attr: attrs) {
            attributeMap.put(attr.getName().toLowerCase(), attr);
        }
    }

    /**
     * @return the ID of the ObjectData
     */
    public long getId() {
        return objectDataId;
    }

    /**
     *
     * @return the value of <b>url_original</b> attribute
     */
    public String getUrlOriginal() {
        return getString("url_original");
    }

    /**
     *
     * @return the value of <b>url_normalized</b> attribute
     */
    public String getUrlNormalized() {
        return getString("url_normalized");
    }

    public String getUrlForProcessing() {
        String original = getUrlOriginal();

        return original != null ? original : getUrlNormalized();
    }

    /**
     * generic method to get the String attribute from the ObjectData instance.
     * @param key attribute name
     * @return attrbute value or null if the attribute is not set
     * @throws UnsupportedOperationException if the real type of an attribute is not STRING
     */
    public String getString(String key) {
        Attribute value = getAttribute(key, Type.STRING);
        return value == null ? null : value.getDataString();
    }

    /**
     * generic method to get the Integer attribute from the ObjectData instance.
     * @param key attribute name
     * @return attrbute value or null if the attribute is not set
     * @throws UnsupportedOperationException if the real type of an attribute is not INT
     */
    public Integer getInt(String key) {
        Attribute value = getAttribute(key, Type.INT);
        return value == null ? null : value.getDataInt();
    }

    /**
     * generic method to get the Boolean attribute from the ObjectData instance.
     * @param key attribute name
     * @return attrbute value or null if the attribute is not set
     * @throws UnsupportedOperationException if the real type of an attribute is not BOOL
     */
    public Boolean getBoolean(String key) {
        Attribute value = getAttribute(key, Type.BOOL);
        return value == null ? null : value.getDataBool();
    }

    /**
     * generic method to get the time (Long) attribute from the ObjectData instance.
     * @param key attribute name
     * @return attrbute value or null if the attribute is not set
     * @throws UnsupportedOperationException if the real type of an attribute is not TIME
     */
    public Long getTime(String key) {
        Attribute value = getAttribute(key, Type.TIME);
        return value == null ? null : value.getDataTime();
    }

    /**
     * a generic method to get the id of a resource in the DataStore from the ObjectData instance.
     * @param key attribute name
     * @return attrbute value or null if the attribute is not set
     * @throws UnsupportedOperationException if the real type of an attribute is not BYTES
     */
    public Long getReferenceId(String key) {
        Attribute value = getAttribute(key, Type.BYTES);
        return value == null ? null : value.getDataBytes().getKey();
    }

    /**
     * a generic method to get the id of another ObjectData object from the ObjectData instance.
     * @param key attribute name
     * @return attrbute value or null if the attribute is not set
     * @throws UnsupportedOperationException if the real type of an attribute is not BYTES
     */
    public Long getObjectId(String key) {
        Attribute value = getAttribute(key, Type.OBJECT);
        return value == null ? null : value.getDataObject();
    }

    Attribute getAttribute(String key, Type requiredType) {
        Attribute value = attributeMap.get(key.toLowerCase());
        if (value == null)
            return null;
        if (value.getType() != requiredType)
            throw new UnsupportedOperationException(String.format("Attribute %s is a type of %s, not %s", key, value.getType(), requiredType));

        return value;
    }

    @Override
    public String toString() {
        return "ObjectDataWrapper(id=" + objectDataId + ", data=" + attributeMap + ")";
    }

    AttributeWrapper getAttributeWrapper(String key) {
        Attribute attr = attributeMap.get(key.toLowerCase());
        if (attr == null) {
            return null;
        } else {
            return new AttributeWrapper(attr);
        }

    }

    public Set<String> getAttributeNames() {
        return attributeMap.keySet();
    }
}
