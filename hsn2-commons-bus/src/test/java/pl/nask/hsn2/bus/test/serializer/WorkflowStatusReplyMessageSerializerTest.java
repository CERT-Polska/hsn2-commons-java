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
import pl.nask.hsn2.bus.operations.WorkflowRevisionInfo;
import pl.nask.hsn2.bus.operations.WorkflowStatusReply;
import pl.nask.hsn2.bus.operations.builder.WorkflowStatusReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class WorkflowStatusReplyMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void workflowStatusReplySerializeNoDescTest() throws MessageSerializerException {
		
		WorkflowRevisionInfo info = new WorkflowRevisionInfo("someRev", 66667L);
		
		WorkflowStatusReply operation = new WorkflowStatusReplyBuilder(true, false, info).build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "WorkflowStatusReply");
		
		WorkflowStatusReply deserialized = (WorkflowStatusReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertTrue(deserialized.isValid());
		Assert.assertFalse(deserialized.isEnabled());
		Assert.assertNotNull(deserialized.getInfo());
		Assert.assertEquals(deserialized.getInfo().getRevision(), "someRev");
		Assert.assertEquals(deserialized.getInfo().getMtime(), 66667L);
		Assert.assertNull(deserialized.getDescription());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void workflowStatusReplySerializeWithDescTest() throws MessageSerializerException {
		
		WorkflowRevisionInfo info = new WorkflowRevisionInfo("someRev", 66667L);
		
		WorkflowStatusReply operation = new WorkflowStatusReplyBuilder(true, false, info)
			.setDescription("some Description")
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "WorkflowStatusReply");
		
		WorkflowStatusReply deserialized = (WorkflowStatusReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertTrue(deserialized.isValid());
		Assert.assertFalse(deserialized.isEnabled());
		Assert.assertNotNull(deserialized.getInfo());
		Assert.assertEquals(deserialized.getInfo().getRevision(), "someRev");
		Assert.assertEquals(deserialized.getInfo().getMtime(), 66667L);
		Assert.assertEquals(deserialized.getDescription(), "some Description");
	}
}
