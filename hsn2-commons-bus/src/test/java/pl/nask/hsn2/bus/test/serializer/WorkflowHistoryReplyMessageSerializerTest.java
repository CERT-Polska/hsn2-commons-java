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
import pl.nask.hsn2.bus.operations.WorkflowHistoryReply;
import pl.nask.hsn2.bus.operations.WorkflowRevisionInfo;
import pl.nask.hsn2.bus.operations.builder.WorkflowHistoryReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class WorkflowHistoryReplyMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void workflowHistoryReplySerializeNoHistoryTest() throws MessageSerializerException {
		WorkflowHistoryReply operation = new WorkflowHistoryReplyBuilder().build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "WorkflowHistoryReply");
		
		WorkflowHistoryReply deserialized = (WorkflowHistoryReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getHistory());
		Assert.assertEquals(deserialized.getHistory().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void workflowHistoryReplySerializeWithHistoryTest() throws MessageSerializerException {
		WorkflowHistoryReply operation = new WorkflowHistoryReplyBuilder()
			.addHistoryEntry(new WorkflowRevisionInfo("rev1", 111L))
			.addHistoryEntry(new WorkflowRevisionInfo("rev2", 222L))
			.addHistoryEntry(new WorkflowRevisionInfo("rev3", 333L))
			.addHistoryEntry(new WorkflowRevisionInfo("rev4", 444L))
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "WorkflowHistoryReply");
		
		WorkflowHistoryReply deserialized = (WorkflowHistoryReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getHistory());
		Assert.assertEquals(deserialized.getHistory().size(), 4);

		Assert.assertNotNull(deserialized.getHistory().get(0));
		Assert.assertEquals(deserialized.getHistory().get(0).getRevision(), "rev1");
		Assert.assertEquals(deserialized.getHistory().get(0).getMtime(), 111L);

		Assert.assertNotNull(deserialized.getHistory().get(1));
		Assert.assertEquals(deserialized.getHistory().get(1).getRevision(), "rev2");
		Assert.assertEquals(deserialized.getHistory().get(1).getMtime(), 222L);

		Assert.assertNotNull(deserialized.getHistory().get(2));
		Assert.assertEquals(deserialized.getHistory().get(2).getRevision(), "rev3");
		Assert.assertEquals(deserialized.getHistory().get(2).getMtime(), 333L);

		Assert.assertNotNull(deserialized.getHistory().get(3));
		Assert.assertEquals(deserialized.getHistory().get(3).getRevision(), "rev4");
		Assert.assertEquals(deserialized.getHistory().get(3).getMtime(), 444L);

	}
}
