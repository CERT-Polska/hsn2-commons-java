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

import pl.nask.hsn2.bus.operations.GetConfigReply;

/**
 * This is a builder for <code>GetConfigReply</code> operation.
 * 
 *
 */
public class GetConfigReplyBuilder implements OperationBuilder<GetConfigReply> {

	/**
	 * Internal variable
	 */
	private GetConfigReply getConfigReply;
	
	/**
	 * Constructor created builder with empty
	 * properties set for <code>GetConfigReply</code>.
	 */
	public GetConfigReplyBuilder() {
		this.getConfigReply = new GetConfigReply();
	}
	
	/**
	 * Constructor created <code>GetConfigReply</code>
	 * with provided properties set.
	 * 
	 * @param properties Properties for this configuration.
	 */
	public GetConfigReplyBuilder(Properties properties) {
		this.getConfigReply = new GetConfigReply(properties);
	}
	
	/**
	 * Adds property for current configuration reply.
	 * 
	 * @param key Property key.
	 * @param value Property value.
	 * @return This builder instance.
	 */
	public final GetConfigReplyBuilder addProperty(String key, String value) {
		this.getConfigReply.getProperties().put(key, value);
		return this;
	}
	
	/**
	 * Returns current GetConfigReply instance.
	 * 
	 * @return Actual GetConfigReply instance.
	 */
	@Override
	public final GetConfigReply build() {
		return this.getConfigReply;
	}
}
