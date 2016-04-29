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

package pl.nask.hsn2.bus.test.serializer;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.JobListReply;
import pl.nask.hsn2.bus.operations.JobStatus;
import pl.nask.hsn2.bus.operations.builder.JobInfoBuilder;
import pl.nask.hsn2.bus.operations.builder.JobListReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class JobListReplyMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void jobListReplySerializeNoJobsTest() throws MessageSerializerException {
		JobListReply operation = new JobListReplyBuilder().build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "JobListReply");
		
		JobListReply deserialized = (JobListReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getJobs());
		Assert.assertEquals(deserialized.getJobs().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void jobListReplySerializeWithJobsTest() throws MessageSerializerException {		
		JobListReplyBuilder operation = new JobListReplyBuilder();
		for (JobStatus js: JobStatus.values()) {
			operation.addJobInfo(new JobInfoBuilder(js.ordinal() + 1, js).build());
		}
		
		Message message = serializer.serialize(operation.build());
		
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "JobListReply");
		
		JobListReply deserialized = (JobListReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getJobs());
		Assert.assertEquals(deserialized.getJobs().size(), JobStatus.values().length);
		
		for (JobStatus js: JobStatus.values()) {
			JobInfo j = deserialized.getJobs().get(js.ordinal());
			Assert.assertEquals(j.getId(), js.ordinal() + 1);
			Assert.assertEquals(j.getStatus(), js);
		}
	}

}
