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

package pl.nask.hsn2.bus.serializer.protobuf;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.WorkflowStatusRequest;
import pl.nask.hsn2.bus.operations.builder.WorkflowStatusRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>WorkflowStatusRequest</code> operation.
 * 
 *
 */
public class ProtoBufWorkflowStatusRequestMessageSerializer implements MessageSerializer<WorkflowStatusRequest> {

	@Override
	public final Message serialize(WorkflowStatusRequest operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Workflows.WorkflowStatusRequest.Builder wsrb = pl.nask.hsn2.protobuff.Workflows.WorkflowStatusRequest
				.newBuilder()
				.setName(operation.getName());
		if (operation.getRevision() != null) {
			wsrb.setRevision(operation.getRevision());
		}

		return new Message("WorkflowStatusRequest", wsrb.build().toByteArray(), null);
	}

	@Override
	public final WorkflowStatusRequest deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Workflows.WorkflowStatusRequest pbWorkflowStatusRequest =
					pl.nask.hsn2.protobuff.Workflows.WorkflowStatusRequest.parseFrom(message.getBody());
			WorkflowStatusRequestBuilder wsrb = new WorkflowStatusRequestBuilder(pbWorkflowStatusRequest.getName());
			if (pbWorkflowStatusRequest.hasRevision()) {
				wsrb.setRevision(pbWorkflowStatusRequest.getRevision());
			}
			return wsrb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
