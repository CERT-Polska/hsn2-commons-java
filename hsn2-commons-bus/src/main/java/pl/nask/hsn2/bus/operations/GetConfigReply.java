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

import java.util.Properties;

/**
 * This is an response operation for <code>GetConfigRequest</code>.
 * 
 *
 */
public class GetConfigReply implements Operation {

	/**
	 * Current configuration properties.
	 */
	private Properties properties;
	
	/**
	 * Default constructor with empty properties set.
	 */
	public GetConfigReply() {
		this.properties = new Properties();
	}

	/**
	 * Constructor with provided properties.
	 * 
	 * @param properties Properties with current configuration.
	 */
	public GetConfigReply(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Gets current configuration properties set.
	 * 
	 * @return Current configuration properties.
	 */
	public final Properties getProperties() {
		return properties;
	}

	/**
	 * Sets current configuration properties set.
	 * 
	 * @param properties Current configuration properties.
	 */
	public final void setProperties(Properties properties) {
		this.properties = properties;
	}
}
