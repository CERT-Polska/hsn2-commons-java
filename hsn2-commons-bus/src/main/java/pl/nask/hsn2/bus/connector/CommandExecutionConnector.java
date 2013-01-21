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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.ConsumeHandlerException;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.dispatcher.CommandContext;
import pl.nask.hsn2.bus.dispatcher.CommandDispatcher;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

/**
 * This is a connector which can be attached to <code>ConsumeEndPointHandler</code>
 * and will forward any incoming operations to process by <code>CommandDispatcher</code>.
 * 
 * This connector is serializable type which means is using any type of
 * <code>MessageSerializer<code> to deserialize incoming messages and
 * serialize them before send back.
 * 
 *
 */
public class CommandExecutionConnector extends AbstractSerializableConnector implements ConsumeEndPointHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutionConnector.class);
	
	private CommandDispatcher dispatcher = null;
	private FireAndForgetEndPoint responseEndpoint;
	
	public CommandExecutionConnector(MessageSerializer<Operation> serializer,
			FireAndForgetEndPoint responseEndPoint,
			CommandDispatcher dispatcher) {
		super(serializer);
		this.responseEndpoint = responseEndPoint;
		this.dispatcher = dispatcher;
	}

	@Override
	public final void handleMessage(Message message) throws ConsumeHandlerException {
		try {

			LOGGER.debug("Got message: {}", message);
			
			Operation operation = getSerializer().deserialize(message);
			
			LOGGER.debug("Deserialized operation: {}", operation);

			CommandContext<Operation> ctx = new CommandContext<Operation>();
			ctx.setSourceMessage(message);
			ctx.setSourceOperation(operation);

			LOGGER.debug("Dispatching...");
			Operation response = dispatcher.dispatch(ctx);

			LOGGER.debug("Command executed (response={})", response);

			if (response != null) {
				Message responseMessage = getSerializer().serialize(response);
				responseMessage.setDestination(message.getReplyTo());
				responseMessage.setCorrelationId(message.getCorrelationId());
				LOGGER.debug("Sending back the response message...{}", responseMessage);
				responseEndpoint.sendNotify(responseMessage);
				LOGGER.debug("Response message sent.");
			}
		} catch (MessageSerializerException e) {
			throw new ConsumeHandlerException(e);
		} catch (BusException e) {
			throw new ConsumeHandlerException("Error sending response.", e);
		}
	}
}
