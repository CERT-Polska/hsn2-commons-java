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

package pl.nask.hsn2.bus.test.serializer;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.WorkflowListReply;
import pl.nask.hsn2.bus.operations.builder.WorkflowBasicInfoBuilder;
import pl.nask.hsn2.bus.operations.builder.WorkflowListReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class WorkflowListReplyMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void workflowListReplySerializeNoJobsTest() throws MessageSerializerException {
		WorkflowListReply operation = new WorkflowListReplyBuilder().build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "WorkflowListReply");
		
		WorkflowListReply deserialized = (WorkflowListReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getWorkflows());
		Assert.assertEquals(deserialized.getWorkflows().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void workflowListReplySerializeWithWorkflowsTest() throws MessageSerializerException {
		WorkflowListReply operation = new WorkflowListReplyBuilder()
			.addWorkflowBasicInfo(new WorkflowBasicInfoBuilder("some1").setEnabled(true).build())
			.addWorkflowBasicInfo(new WorkflowBasicInfoBuilder("some2").setEnabled(false).build())
			.addWorkflowBasicInfo(new WorkflowBasicInfoBuilder("some3").setEnabled(true).build())
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "WorkflowListReply");
		
		WorkflowListReply deserialized = (WorkflowListReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getWorkflows());
		Assert.assertEquals(deserialized.getWorkflows().size(), 3);
		Assert.assertEquals(deserialized.getWorkflows().get(0).getName(), "some1");
		Assert.assertEquals(deserialized.getWorkflows().get(0).isEnabled(), true);
		Assert.assertEquals(deserialized.getWorkflows().get(1).getName(), "some2");
		Assert.assertEquals(deserialized.getWorkflows().get(1).isEnabled(), false);
		Assert.assertEquals(deserialized.getWorkflows().get(2).getName(), "some3");
		Assert.assertEquals(deserialized.getWorkflows().get(2).isEnabled(), true);
	}

}
