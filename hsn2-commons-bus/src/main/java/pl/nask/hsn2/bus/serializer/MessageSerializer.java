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

package pl.nask.hsn2.bus.serializer;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.Operation;

/**
 * This interface is used by serializers to serialize and deserialize
 * generic bus <code>Messages</code>.
 * 
 * <code>serialize()</code> method provides generic bus <code>Message</code>
 * basing on business operation instance. 
 * 
 * <code>deserialize()</code> method provides business operation instance
 * basing on generic bus <code>Message</code>.
 * 
 *
 * @param <T>
 */
public interface MessageSerializer<T extends Operation> {

	/**
	 * Serializes business operation to bus message.
	 * 
	 * @param operation Operation to be serialized.
	 * @return Bus message.
	 * @throws MessageSerializerException If there is any problem
	 *                      with serialization, the exception will thrown.
	 */
	Message serialize(T operation) throws MessageSerializerException;
	
	/**
	 * Deserializes bus Messages to business operation.
	 * 
	 * @param message Bus message to be deserialized.
	 * @return Business operation.
	 * @throws MessageSerializerException If there is any problem
	 *                      with serialization, the exception will thrown.
	 */
	T deserialize(Message message) throws MessageSerializerException;
}
