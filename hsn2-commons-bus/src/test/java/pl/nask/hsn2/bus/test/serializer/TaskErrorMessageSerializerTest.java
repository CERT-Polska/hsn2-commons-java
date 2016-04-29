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
import pl.nask.hsn2.bus.operations.TaskError;
import pl.nask.hsn2.bus.operations.TaskErrorReasonType;
import pl.nask.hsn2.bus.operations.builder.TaskErrorBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class TaskErrorMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void taskErrorSerializeNoDescTest() throws MessageSerializerException {
		TaskError operation = new TaskErrorBuilder(5, 6, TaskErrorReasonType.INPUT).build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "TaskError");
		
		TaskError deserialized = (TaskError) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getJobId(), 5);
		Assert.assertEquals(deserialized.getTaskId(), 6);
		Assert.assertEquals(deserialized.getReason(), TaskErrorReasonType.INPUT);
		Assert.assertNull(deserialized.getDescription());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void taskErrorSerializeWithDescTest() throws MessageSerializerException {
		TaskError operation = new TaskErrorBuilder(5, 6, TaskErrorReasonType.PARAMS)
			.setDescription("some desc").build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "TaskError");
		
		TaskError deserialized = (TaskError) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getJobId(), 5);
		Assert.assertEquals(deserialized.getTaskId(), 6);
		Assert.assertEquals(deserialized.getReason(), TaskErrorReasonType.PARAMS);
		Assert.assertEquals(deserialized.getDescription(), "some desc");
	}

}
