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
import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.JobListReply;
import pl.nask.hsn2.bus.operations.builder.JobListReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>JobListReply</code> operation.
 * 
 *
 */
public class ProtoBufJobListReplyMessageSerializer implements MessageSerializer<JobListReply> {

	@Override
	public final Message serialize(JobListReply operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Jobs.JobListReply.Builder jlrb
			= pl.nask.hsn2.protobuff.Jobs.JobListReply
				.newBuilder();
		
		pl.nask.hsn2.protobuff.Jobs.JobInfo.Builder jib = pl.nask.hsn2.protobuff.Jobs.JobInfo.newBuilder();
		for (JobInfo jobInfo : operation.getJobs()) {
			jlrb.addJobs(ProtoBufJobInfoSerializer.serializeJobInfo(jib, jobInfo));
		}

		return new Message("JobListReply", jlrb.build().toByteArray(), null);
	}

	@Override
	public final JobListReply deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Jobs.JobListReply pbJobListReply =
					pl.nask.hsn2.protobuff.Jobs.JobListReply.parseFrom(message.getBody());
			JobListReplyBuilder jlrb = new JobListReplyBuilder();
			if (pbJobListReply.getJobsCount() > 0) {
				for (pl.nask.hsn2.protobuff.Jobs.JobInfo jobInfo : pbJobListReply.getJobsList()) {
					jlrb.addJobInfo(ProtoBufJobInfoSerializer.deserializeJobInfo(jobInfo));
				}
			}
			return jlrb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
