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

import java.util.Properties;

/**
 * This is an operation to set new configuration of the framework.
 * 
 *
 */
public class SetConfigRequest implements Operation {

	/**
	 * Should be configuration overridden?
	 */
	private boolean override = false;
	
	/**
	 * New configuration to be applied in framework.
	 */
	private Properties properties;

	/**
	 * Default constructor with empty configuration.
	 */
	public SetConfigRequest() {
		this.properties = new Properties();
	}

	/**
	 * Constructor with configuration provided.
	 * 
	 * @param properties Configuration to be applied to a framework.
	 */
	public SetConfigRequest(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Constructor with configuration provided and override flag.
	 * @param override
	 * @param properties
	 */
	public SetConfigRequest(boolean override, Properties properties) {
		this(properties);
		this.override = override;
	}

	/**
	 * Checks if override flag is set.
	 * 
	 * @return <code>true</code> of configuration will overridden.
	 */
	public final boolean isOverride() {
		return override;
	}

	/**
	 * Sets override flag.
	 * 
	 * @param override Override flag.
	 */
	public final void setReplace(boolean override) {
		this.override = override;
	}

	/**
	 * Gets current configuration properties prepared for SetConfigRequest.
	 * 
	 * @return Prepared configuration.
	 */
	public final Properties getProperties() {
		return properties;
	}

	/**
	 * Sets configuration to be applied by SetConfigRequest.
	 * 
	 * @param properties Configuration to be applied.
	 */
	public final void setProperties(Properties properties) {
		this.properties = properties;
	}
}
