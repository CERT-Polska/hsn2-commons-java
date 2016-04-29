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
import pl.nask.hsn2.bus.operations.TaskCompleted;
import pl.nask.hsn2.bus.operations.builder.TaskCompletedBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class TaskCompletedMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void taskCompletedSerializeNoWarnsNoObjsTest() throws MessageSerializerException {
		TaskCompleted operation = new TaskCompletedBuilder(5, 6).build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "TaskCompleted");
		
		TaskCompleted deserialized = (TaskCompleted) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getJobId(), 5);
		Assert.assertEquals(deserialized.getTaskId(), 6);
		Assert.assertNotNull(deserialized.getWarnings());
		Assert.assertEquals(deserialized.getWarnings().size(), 0);
		Assert.assertNotNull(deserialized.getObjects());
		Assert.assertEquals(deserialized.getObjects().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void taskCompletedSerializeWithWarnsWithObjsTest() throws MessageSerializerException {
		TaskCompleted operation = new TaskCompletedBuilder(5, 6)
			.addWarning("warning 1")
			.addWarning("warning 1")
			.addWarning("warning 2")
			.addWarning("warning 3")
			.addNewObjectId(1L)
			.addNewObjectId(6L)
			.addNewObjectId(9L)
			.addNewObjectId(30L)
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "TaskCompleted");
		
		TaskCompleted deserialized = (TaskCompleted) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getJobId(), 5);
		Assert.assertEquals(deserialized.getTaskId(), 6);
		Assert.assertNotNull(deserialized.getWarnings());
		Assert.assertEquals(deserialized.getWarnings().size(), 4);
		Assert.assertEquals(deserialized.getWarnings().get(0), "warning 1");
		Assert.assertEquals(deserialized.getWarnings().get(1), "warning 1");
		Assert.assertEquals(deserialized.getWarnings().get(2), "warning 2");
		Assert.assertEquals(deserialized.getWarnings().get(3), "warning 3");
		Assert.assertEquals(deserialized.getObjects().size(), 4);
		Assert.assertTrue(deserialized.getObjects().contains(1L));
		Assert.assertTrue(deserialized.getObjects().contains(6L));
		Assert.assertTrue(deserialized.getObjects().contains(9L));
		Assert.assertTrue(deserialized.getObjects().contains(30L));
	}

}
