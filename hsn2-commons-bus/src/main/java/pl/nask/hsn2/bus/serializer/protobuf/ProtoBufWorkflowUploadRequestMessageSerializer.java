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

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.WorkflowUploadRequest;
import pl.nask.hsn2.bus.operations.builder.WorkflowUploadRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>JobListRequest</code> operation.
 * 
 *
 */
public class ProtoBufWorkflowUploadRequestMessageSerializer implements MessageSerializer<WorkflowUploadRequest> {

	@Override
	public final Message serialize(WorkflowUploadRequest operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Workflows.WorkflowUploadRequest wur = pl.nask.hsn2.protobuff.Workflows.WorkflowUploadRequest
				.newBuilder()
				.setName(operation.getName())
				.setContent(operation.getContent())
				.setOverwrite(operation.isOverride())
				.build();

		return new Message("WorkflowUploadRequest", wur.toByteArray(), null);
	}

	@Override
	public final WorkflowUploadRequest deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Workflows.WorkflowUploadRequest pbWorkflowUploadRequest =
					pl.nask.hsn2.protobuff.Workflows.WorkflowUploadRequest.parseFrom(message.getBody());
			WorkflowUploadRequestBuilder wurb = new WorkflowUploadRequestBuilder(
					pbWorkflowUploadRequest.getName(),
					pbWorkflowUploadRequest.getContent(),
					pbWorkflowUploadRequest.getOverwrite());
			return wurb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
