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
import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectResponse;
import pl.nask.hsn2.bus.operations.builder.ObjectDataBuilder;
import pl.nask.hsn2.bus.operations.builder.ObjectResponseBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class ObjectResponseMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void objectResponseGETSerializeNoObjectsTest() throws MessageSerializerException {
		ObjectResponse operation = new ObjectResponseBuilder(ObjectResponse.ResponseType.SUCCESS_GET).build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectResponse");
		
		ObjectResponse deserialized = (ObjectResponse) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), ObjectResponse.ResponseType.SUCCESS_GET);
		Assert.assertNotNull(deserialized.getData());
		Assert.assertEquals(deserialized.getData().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectResponseGETSerializeWithDataTest() throws MessageSerializerException {
		ObjectResponse operation = new ObjectResponseBuilder(
				ObjectResponse.ResponseType.SUCCESS_GET).addData(
				new ObjectDataBuilder()
						.setId(4L)
						.addIntAttribute("someInt", 6)
						.addStringAttribute("someString", "blablabla")
						.addTimeAttribute("someTime", 6666L)
						.build())
				.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectResponse");
		
		ObjectResponse deserialized = (ObjectResponse) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), ObjectResponse.ResponseType.SUCCESS_GET);
		Assert.assertNotNull(deserialized.getData());
		Assert.assertEquals(deserialized.getData().size(), 1);
		ObjectData od = (ObjectData) deserialized.getData().toArray()[0];
		Assert.assertNotNull(od.findAttribute("someInt", AttributeType.INT));
		Assert.assertEquals(od.findAttribute("someInt", AttributeType.INT).getInteger(), 6);
		Assert.assertNotNull(od.findAttribute("someString", AttributeType.STRING));
		Assert.assertEquals(od.findAttribute("someString", AttributeType.STRING).getString(), "blablabla");
		Assert.assertNotNull(od.findAttribute("someTime", AttributeType.TIME));
		Assert.assertEquals(od.findAttribute("someTime", AttributeType.TIME).getTime(), 6666L);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectResponsePUTSerializeTest() throws MessageSerializerException {
		ObjectResponse operation = new ObjectResponseBuilder(
				ObjectResponse.ResponseType.SUCCESS_PUT)
			.addObject(2L)
			.addObject(3L)
			.addObject(5L)
			.build();

		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectResponse");

		ObjectResponse deserialized = (ObjectResponse) serializer
				.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), ObjectResponse.ResponseType.SUCCESS_PUT);
		Assert.assertNotNull(deserialized.getObjects());
		Assert.assertEquals(deserialized.getObjects().size(), 3);
		Assert.assertTrue(deserialized.getObjects().contains(2L));
		Assert.assertTrue(deserialized.getObjects().contains(3L));
		Assert.assertTrue(deserialized.getObjects().contains(5L));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectResponseUPDATESerializeTest() throws MessageSerializerException {
		ObjectResponse operation = new ObjectResponseBuilder(
				ObjectResponse.ResponseType.SUCCESS_UPDATE)
			.addConflict(2L)
			.addConflict(3L)
			.addConflict(5L)
			.build();

		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectResponse");

		ObjectResponse deserialized = (ObjectResponse) serializer
				.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), ObjectResponse.ResponseType.SUCCESS_UPDATE);
		Assert.assertNotNull(deserialized.getConflicts());
		Assert.assertEquals(deserialized.getConflicts().size(), 3);
		Assert.assertTrue(deserialized.getConflicts().contains(2L));
		Assert.assertTrue(deserialized.getConflicts().contains(3L));
		Assert.assertTrue(deserialized.getConflicts().contains(5L));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectResponseFAILURESerializeTest() throws MessageSerializerException {
		ObjectResponse operation = new ObjectResponseBuilder(
				ObjectResponse.ResponseType.FAILURE)
			.setError("some error")
			.build();

		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectResponse");

		ObjectResponse deserialized = (ObjectResponse) serializer
				.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), ObjectResponse.ResponseType.FAILURE);
		Assert.assertEquals(deserialized.getError(), "some error");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectResponsePARGETSerializeWithDataTest() throws MessageSerializerException {
		ObjectResponse operation = new ObjectResponseBuilder(
				ObjectResponse.ResponseType.PARTIAL_GET).addData(
				new ObjectDataBuilder()
						.setId(4L)
						.addIntAttribute("someInt", 6)
						.addStringAttribute("someString", "blablabla")
						.addTimeAttribute("someTime", 6666L)
						.build())
				.addMissing(3L)
				.addMissing(6L)
				.addMissing(9L)
				.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectResponse");
		
		ObjectResponse deserialized = (ObjectResponse) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), ObjectResponse.ResponseType.PARTIAL_GET);
		Assert.assertNotNull(deserialized.getData());
		Assert.assertEquals(deserialized.getData().size(), 1);
		ObjectData od = (ObjectData) deserialized.getData().toArray()[0];
		Assert.assertNotNull(od.findAttribute("someInt", AttributeType.INT));
		Assert.assertEquals(od.findAttribute("someInt", AttributeType.INT).getInteger(), 6);
		Assert.assertNotNull(od.findAttribute("someString", AttributeType.STRING));
		Assert.assertEquals(od.findAttribute("someString", AttributeType.STRING).getString(), "blablabla");
		Assert.assertNotNull(od.findAttribute("someTime", AttributeType.TIME));
		Assert.assertEquals(od.findAttribute("someTime", AttributeType.TIME).getTime(), 6666L);
		Assert.assertNotNull(deserialized.getMissing());
		Assert.assertEquals(deserialized.getMissing().size(), 3);
		Assert.assertTrue(deserialized.getMissing().contains(3L));
		Assert.assertTrue(deserialized.getMissing().contains(6L));
		Assert.assertTrue(deserialized.getMissing().contains(9L));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectResponsePARUPDATESerializeWithDataTest() throws MessageSerializerException {
		ObjectResponse operation = new ObjectResponseBuilder(
				ObjectResponse.ResponseType.PARTIAL_UPDATE)
				.addMissing(3L)
				.addMissing(6L)
				.addMissing(9L)
				.addConflict(13L)
				.addConflict(16L)
				.addConflict(19L)
				.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectResponse");
		
		ObjectResponse deserialized = (ObjectResponse) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), ObjectResponse.ResponseType.PARTIAL_UPDATE);
		Assert.assertNotNull(deserialized.getMissing());
		Assert.assertEquals(deserialized.getMissing().size(), 3);
		Assert.assertTrue(deserialized.getMissing().contains(3L));
		Assert.assertTrue(deserialized.getMissing().contains(6L));
		Assert.assertTrue(deserialized.getMissing().contains(9L));
		Assert.assertNotNull(deserialized.getConflicts());
		Assert.assertEquals(deserialized.getConflicts().size(), 3);
		Assert.assertTrue(deserialized.getConflicts().contains(13L));
		Assert.assertTrue(deserialized.getConflicts().contains(16L));
		Assert.assertTrue(deserialized.getConflicts().contains(19L));
	}

}
