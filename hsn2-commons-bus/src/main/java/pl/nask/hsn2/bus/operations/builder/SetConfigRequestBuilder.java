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

import java.util.Properties;

import pl.nask.hsn2.bus.operations.SetConfigRequest;

/**
 * This is a builder for <code>SetConfigRequest</code> operation.
 * 
 *
 */
public class SetConfigRequestBuilder implements OperationBuilder<SetConfigRequest> {

	/**
	 * Internal variable
	 */
	private SetConfigRequest setConfigRequest;
	
	/**
	 * Default configuration with no override flag set and empty configuration.
	 */
	public SetConfigRequestBuilder() {
		this.setConfigRequest = new SetConfigRequest();
	}
	
	/**
	 * Constructor with provided configuration to be applied
	 * by <code>SetConfigRequest</code> operation.
	 * 
	 * @param properties Configuration to be applied.
	 */
	public SetConfigRequestBuilder(Properties properties) {
		this.setConfigRequest = new SetConfigRequest(properties);
	}

	/**
	 * Constructor with provided configuration to be applied and override flag.
	 * @param override <code>true</code> if <code>SetConfigRequest</code>
	 *        should override current framework configuration.
	 * @param properties Configuration to be applied
	 *        by <code>SetConfigRequest</code> operation.
	 */
	public SetConfigRequestBuilder(boolean override, Properties properties) {
		this.setConfigRequest = new SetConfigRequest(override, properties);
	}

	/**
	 * Adds property to the configuration which will be shipped to a framework.
	 * 
	 * @param key Configuration key.
	 * @param value Property value.
	 * 
	 * @return Tihs builder instance.
	 */
	public final SetConfigRequestBuilder addProperty(String key, String value) {
		setConfigRequest.getProperties().put(key, value);
		return this;
	}
	
	/**
	 * Returns current SetConfigRequest instance.
	 * 
	 * @return Actual SetConfigRequest instance.
	 */
	@Override
	public final SetConfigRequest build() {
		return this.setConfigRequest;
	}
}
