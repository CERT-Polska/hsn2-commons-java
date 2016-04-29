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

package pl.nask.hsn2.bus.serializer.protobuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;
import pl.nask.hsn2.bus.utils.PackageScanner;
import pl.nask.hsn2.bus.utils.PackageScannerException;

/**
 * This is Protocol Buffer main serializer.
 * 
 *
 */
public final class ProtoBufMessageSerializer implements MessageSerializer<Operation> {

	/**
	 * Internal logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProtoBufMessageSerializer.class);
	
	/**
	 * Serializers and deserializers.
	 */
	private static Map<Class<?>, MessageSerializer<?>> operationSerialiers = null;
	private static Map<String, MessageSerializer<?>> operationDeserializers = null;
	
	static {
		buildSerializerMap();
	}

	/**
	 * Static methods builds map of available serializers and deserializers.
	 */
	private static void buildSerializerMap() {
		LOGGER.debug("Building serializers for operations.");
		operationSerialiers = new HashMap<Class<?>, MessageSerializer<?>>();
		operationDeserializers = new HashMap<String, MessageSerializer<?>>();
		try {
			LOGGER.debug("Looking for operations.");
			List<Class<?>> operationsClasses = PackageScanner.getClassesForPackage("pl.nask.hsn2.bus.operations");
			LOGGER.debug("Found {} operations.", operationsClasses.size());
			for (Class<?> operation : operationsClasses) {
				String serializerName = new StringBuilder(
						"pl.nask.hsn2.bus.serializer.protobuf.")
						.append("ProtoBuf")
						.append(operation.getSimpleName())
						.append("MessageSerializer")
						.toString();
				try {
					MessageSerializer<?> serializer = (MessageSerializer<?>) Class.forName(serializerName).newInstance();
					operationSerialiers.put(operation, serializer);
					operationDeserializers.put(operation.getSimpleName(), serializer);
				} catch (ClassNotFoundException e) {
					serializerName = new StringBuilder(
							"pl.nask.hsn2.bus.serializer.protobuf.")
							.append("ProtoBuf")
							.append(operation.getSimpleName())
							.append("Serializer")
							.toString();
					try {
						Class.forName(serializerName);
						// it's looks it's not an operation ;)
					} catch (ClassNotFoundException e1) {
						LOGGER.warn("Serializer cannot be found for operation: {}", operation.getSimpleName());
					}
				} catch (InstantiationException e) {
					LOGGER.error("Serializer cannot be instantiated: {}", serializerName);
				} catch (IllegalAccessException e) {
					LOGGER.error("Serializer cannot be instantiated, illegal access to constructor: {}", serializerName);
				}
			}
			LOGGER.debug("Serializers built. Found {} serializers.", operationSerialiers.size());
		} catch (PackageScannerException e) {
			LOGGER.error("Cannot scan operations, serializers map cannot be built.");
		}
	}
	
	/**
	 * Default constructor.
	 */
	public ProtoBufMessageSerializer() {
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Message serialize(Operation operation) throws MessageSerializerException {
		MessageSerializer<Operation> serializer =
				(MessageSerializer<Operation>) operationSerialiers.get(operation.getClass());

		if (serializer == null) {
			throw new MessageSerializerException("There is no serializer for: " + operation.getClass());
		}

		return serializer.serialize(operation);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Operation deserialize(Message message) throws MessageSerializerException {
		MessageSerializer<Operation> deserializer =
				(MessageSerializer<Operation>) operationDeserializers.get(message.getType());

		if (deserializer == null) {
			throw new MessageSerializerException("There is no deserializer for: " + message.getType());
		}

		return deserializer.deserialize(message);
	}
}
