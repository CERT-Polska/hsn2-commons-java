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

import pl.nask.hsn2.protobuff.Object.Attribute;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;

public class AttributeWrapper {

    private final Attribute attr;

    public AttributeWrapper(Attribute attr) {
        this.attr = attr;
    }

    public Object getValue() {
        switch (attr.getType()) {
        case BOOL: return attr.getDataBool();
        case BYTES: return attr.getDataBytes();
        case FLOAT: return attr.getDataFloat();
        case INT: return attr.getDataInt();
        case OBJECT: return attr.getDataObject();
        case STRING: return attr.getDataString();
        case TIME: return attr.getDataTime();
        case EMPTY: 
        default:
            return null;
        }
    }

    public boolean isReference() {
        return attr.getType() == Type.BYTES;
    }
}
