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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.RequiredParameterMissingException;
import pl.nask.hsn2.protobuff.Service.Parameter;


/**
 * A wrapper for the map of the parameters which come with the job.
 */
public class ParametersWrapper {
    private static final Set<String> TRUE_VALUES = new HashSet<String>(Arrays.asList("true", "1", "yes", "t", "y"));

    private Map<String, String> map = new HashMap<String, String>();

    public ParametersWrapper() {}

    public ParametersWrapper(List<Parameter> parameters) {
        if (parameters != null) {
            for (Parameter p: parameters) {
                map.put(p.getName().toLowerCase(Locale.ENGLISH), p.getValue());
            }
        }
    }

    public ParametersWrapper(Map<String, String> params) {
        if (params == null) {
            return;
        }

        for (Map.Entry<String, String> e: params.entrySet()) {
            map.put(e.getKey().toLowerCase(), e.getValue());
        }
    }

    public ParametersWrapper(Properties params) {
        if (params == null) {
            return;
        }

        for (Enumeration<Object> e = params.keys(); e.hasMoreElements(); /**/) {
        	String key = (String) e.nextElement();
        	String value = params.getProperty(key);
        	map.put(key.toLowerCase(), value);
        }
    }

    public boolean hasParam(String paramName) {
        return map.containsKey(paramName.toLowerCase(Locale.ENGLISH) );
    }

    /**
     * @param paramName
     * @return
     */
    public String get(String paramName) throws RequiredParameterMissingException {
        return _getRequiredValue(paramName);
    }

    public String get(String paramName, String defaultValue) {
        String value = _getValue(paramName);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public boolean getBoolean(String paramName) throws RequiredParameterMissingException {
        String value = _getRequiredValue(paramName);
        return convertToBoolean(value);
    }

    public boolean getBoolean(String paramName, boolean defaultValue) {
        String value = _getValue(paramName);
        if (value == null) {
            return defaultValue;
        } else {
            return convertToBoolean(value);
        }
    }

    public int getInt(String paramName, int defaultValue) throws ParameterException {
        String value = _getValue(paramName);
        if (value == null) {
            return defaultValue;
        } else {
            return convertToInt(value);
        }
    }

    public int getInt(String paramName) throws ParameterException {
        String value = _getRequiredValue(paramName);
        return convertToInt(value);
    }

    private String _getValue(String paramName) {
        String value = map.get(paramName);
        if (value == null) {
            value = map.get(paramName.toLowerCase());
        }

        return value;
    }

    private String _getRequiredValue(String paramName) throws RequiredParameterMissingException {
        String value = _getValue(paramName);
        if (value == null) {
            throw new RequiredParameterMissingException(paramName);
        } else {
            return value;
        }
    }

    private int convertToInt(String value) throws ParameterException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParameterException(e.getMessage(), e);
        }
    }

    private boolean convertToBoolean(String value) {
        return TRUE_VALUES.contains(value.toLowerCase());
    }

    @Override
    public String toString() {
        return map != null ? map.toString() : "[]";
    }
}
