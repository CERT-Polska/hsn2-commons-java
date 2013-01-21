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

package pl.nask.hsn2.bus.operations;

/**
 * Types of attributes.
 * 
 *
 */
public enum AttributeType implements Operation {
	
	// empty attribute
    EMPTY {
    	@Override
    	public void assertFits(Object value) {
    		// everything fits an empty type
    	}
    },
    
    // boolean attribute
    BOOL {
    	@Override
    	public void assertFits(Object value) {
    		if (! (value instanceof Boolean))
    			throw new IllegalArgumentException("Boolean instance expected for attribute type BOOL");
    	}
    },
    
    // integer attribute
    INT {
    	@Override
    	public void assertFits(Object value) {
    		if (! (value instanceof Integer))
    			throw new IllegalArgumentException("Integer instance expected for attribute type INT");
    	}
    },
    
    // time attribute
    TIME {
    	@Override
    	public void assertFits(Object value) {
    		if (! (value instanceof Long))
    			throw new IllegalArgumentException("Long instance expected for attribute type TIME");
    	}
    },
    
    // float attribute
    FLOAT {
    	@Override
    	public void assertFits(Object value) {
    		if (! (value instanceof Float))
    			throw new IllegalArgumentException("Float instance expected for attribute type FLOAT");
    	}
    },
    
    // string attribute
    STRING {
    	@Override
    	public void assertFits(Object value) {
	    	if (!(value instanceof String))	
	    		throw new IllegalArgumentException("String instance expected for attribute type STRING");
    	}
    },
    
    // identifier of an object attribute
    OBJECT {
    	@Override
    	public void assertFits(Object value) {
    		if (!(value instanceof Long))
    			throw new IllegalArgumentException("Long instance expected for attribute type OBJECT");
    	}
    },
    
    // raw file data or serialized message
    BYTES {
    	public void assertFits(Object value) {
    		if (!(value instanceof Reference))
    			throw new IllegalArgumentException("Reference instance expected for attribute type BYTES");
    	}
    };

    /**
     * Check if the type of the parameter fits this AttributeType. Will throw an IllegalArgumentException if not.
     * @param value Value to be checked
     */
    public abstract void assertFits(Object value);
}
