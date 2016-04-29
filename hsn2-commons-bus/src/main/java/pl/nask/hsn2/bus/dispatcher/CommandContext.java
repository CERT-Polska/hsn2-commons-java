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

package pl.nask.hsn2.bus.dispatcher;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.Operation;

/**
 * This is a context of command execution via <code>CommandDispatcher</code>.
 * 
 *
 * @param <T> Command to be executed.
 */
public class CommandContext<T extends Operation> {

	/**
	 * Original message from transport layer.
	 */
	private Message sourceMessage;
	
	/**
	 * Incoming operation.
	 */
	private T sourceOperation;
	
	/**
	 * Default constructor.
	 */
	public CommandContext() {
		super();
	}

	/**
	 * Gets source low level message.
	 * 
	 * @return Transport level message.
	 */
	public final Message getSourceMessage() {
		return sourceMessage;
	}

	/**
	 * Sets low level message.
	 * 
	 * @param sourceMessage Transport level message.
	 */
	public final void setSourceMessage(Message sourceMessage) {
		this.sourceMessage = sourceMessage;
	}

	/**
	 * Gets incoming operation.
	 * 
	 * @return Operation.
	 */
	public final T getSourceOperation() {
		return sourceOperation;
	}

	/**
	 * Sets operation.
	 * 
	 * @param sourceOperation Operation.
	 */
	public final void setSourceOperation(T sourceOperation) {
		this.sourceOperation = sourceOperation;
	}
}
