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

import pl.nask.hsn2.bus.operations.SetConfigReply;

public class SetConfigReplyBuilder implements OperationBuilder<SetConfigReply> {

	/**
	 * Internal variable.
	 */
	private SetConfigReply setConfigReply;
	
	/**
	 * Default constructor with success=false flag.
	 */
	public SetConfigReplyBuilder() {
		this.setConfigReply = new SetConfigReply();
	}
	
	/**
	 * Constructor with success flag provided.
	 * 
	 * @param success Success flag.
	 */
	public SetConfigReplyBuilder(boolean success) {
		this.setConfigReply = new SetConfigReply(success);
	}
	
	/**
	 * Sets success flag.
	 * 
	 * @param success Success flag.
	 * 
	 * @return This builder instance.
	 */
	public final SetConfigReplyBuilder setSuccess(boolean success) {
		this.setConfigReply.setSuccess(success);
		return this;
	}
	
	/**
	 * Returns current SetConfigReply instance.
	 * 
	 * @return Actual SetConfigReply instance.
	 */
	@Override
	public final SetConfigReply build() {
		return this.setConfigReply;
	}
}
