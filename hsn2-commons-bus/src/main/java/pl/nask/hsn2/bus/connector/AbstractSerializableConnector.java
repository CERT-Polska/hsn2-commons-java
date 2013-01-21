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

package pl.nask.hsn2.bus.connector;

import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.serializer.MessageSerializer;

/**
 * This is abstract class for connectors implementations with
 * serializer option.
 *
 *
 */
public class AbstractSerializableConnector {

	/**
	 * Serializer to be used.
	 */
	private MessageSerializer<Operation> serializer;

	/**
	 * Default constructor.
	 * 
	 * @param serializer
	 *            Serializer to be used by connector.
	 */
	public AbstractSerializableConnector(MessageSerializer<Operation> serializer) {
		this.serializer = serializer;
	}

	/**
	 * Sets default serializer for the connector.
	 * 
	 * @param serializer
	 *            Serializer to be used by connector.
	 */
	public final void setSerializer(MessageSerializer<Operation> serializer) {
		this.serializer = serializer;
	}

	/**
	 * Gets serializer for the connector.
	 * 
	 * @return Serializer to be used by connector.
	 */
	protected final MessageSerializer<Operation> getSerializer() {
		return this.serializer;
	}

}
