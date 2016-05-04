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

import java.util.ArrayList;
import java.util.List;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.TaskRequest;
import pl.nask.hsn2.bus.operations.builder.TaskRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;
import pl.nask.hsn2.protobuff.Service.Parameter;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>TaskRequest</code> operation.
 * 
 *
 */
public class ProtoBufTaskRequestMessageSerializer implements MessageSerializer<TaskRequest> {

	@Override
	public final Message serialize(TaskRequest operation)
			throws MessageSerializerException {
		
		List<Parameter> parameters = new ArrayList<Parameter>();

		pl.nask.hsn2.protobuff.Service.Parameter.Builder pBuilder
			= pl.nask.hsn2.protobuff.Service.Parameter.newBuilder();

		for (String key : operation.getParameters().stringPropertyNames()) {
			pBuilder.clear();
			pBuilder.setName(key);
			pBuilder.setValue(operation.getParameters().getProperty(key));
			parameters.add(pBuilder.build());
		}
		
		pl.nask.hsn2.protobuff.Process.TaskRequest tr = pl.nask.hsn2.protobuff.Process.TaskRequest.newBuilder()
    	        .setJob(operation.getJob())
    	        .setTaskId(operation.getTaskId())
    	        .setObject(operation.getObject())
    	        .addAllParameters(parameters)
    	        .build();

		return new Message("TaskRequest", tr.toByteArray(), null);
	}

	@Override
	public final TaskRequest deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Process.TaskRequest pbTaskRequest =
					pl.nask.hsn2.protobuff.Process.TaskRequest.parseFrom(message.getBody());
			TaskRequestBuilder trb = new TaskRequestBuilder(
					pbTaskRequest.getJob(),
					pbTaskRequest.getTaskId(),
					pbTaskRequest.getObject());
			if (pbTaskRequest.getParametersCount() > 0) {
				for (pl.nask.hsn2.protobuff.Service.Parameter param : pbTaskRequest.getParametersList()) {
					trb.addParameter(param.getName(), param.getValue());
				}
			}
			return trb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
