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
import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectRequest;
import pl.nask.hsn2.bus.operations.QueryStructure;
import pl.nask.hsn2.bus.operations.QueryStructure.QueryType;
import pl.nask.hsn2.bus.operations.builder.ObjectDataBuilder;
import pl.nask.hsn2.bus.operations.builder.ObjectRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class ObjectRequestMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void objectRequestGETSerializeNoObjectsTest() throws MessageSerializerException {
		ObjectRequest operation = new ObjectRequestBuilder(ObjectRequest.RequestType.GET, 4L).build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectRequest");
		
		ObjectRequest deserialized = (ObjectRequest) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getRequestType(), ObjectRequest.RequestType.GET);
		Assert.assertEquals(deserialized.getJobId(), 4L);
		Assert.assertNotNull(deserialized.getObjects());
		Assert.assertEquals(deserialized.getObjects().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectRequestGETSerializeWithObjectsTest() throws MessageSerializerException {
		ObjectRequest operation = new ObjectRequestBuilder(ObjectRequest.RequestType.GET, 4L)
			.addObject(3L)
			.addObject(6L)
			.addObject(9L)
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectRequest");
		
		ObjectRequest deserialized = (ObjectRequest) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getRequestType(), ObjectRequest.RequestType.GET);
		Assert.assertEquals(deserialized.getJobId(), 4L);
		Assert.assertNotNull(deserialized.getObjects());
		Assert.assertEquals(deserialized.getObjects().size(), 3);
		Assert.assertTrue(deserialized.getObjects().contains(3L));
		Assert.assertTrue(deserialized.getObjects().contains(6L));
		Assert.assertTrue(deserialized.getObjects().contains(9L));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectRequestPUTSerializeTest() throws MessageSerializerException {
		ObjectRequest operation = new ObjectRequestBuilder(
				ObjectRequest.RequestType.PUT, 4L)
			.setTaskId(6)
			.addData(
					new ObjectDataBuilder()
						.setId(4L)
						.addIntAttribute("someInt", 6)
						.addStringAttribute("someString", "blablabla")
						.addTimeAttribute("someTime", 6666L).build())
			.build();

		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectRequest");

		ObjectRequest deserialized = (ObjectRequest) serializer
				.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getRequestType(), ObjectRequest.RequestType.PUT);
		Assert.assertEquals(deserialized.getJobId(), 4L);
		Assert.assertEquals(deserialized.getTaskId(), 6);
		Assert.assertNotNull(deserialized.getData());
		Assert.assertEquals(deserialized.getData().size(), 1);
		ObjectData od = (ObjectData) deserialized.getData().toArray()[0];
		Assert.assertEquals(od.getId(), 4L);
		Assert.assertNotNull(od.findAttribute("someInt", AttributeType.INT));
		Assert.assertEquals(od.findAttribute("someInt", AttributeType.INT).getInteger(), 6);
		Assert.assertNotNull(od.findAttribute("someString", AttributeType.STRING));
		Assert.assertEquals(od.findAttribute("someString", AttributeType.STRING).getString(), "blablabla");
		Assert.assertNotNull(od.findAttribute("someTime", AttributeType.TIME));
		Assert.assertEquals(od.findAttribute("someTime", AttributeType.TIME).getTime(), 6666L);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectRequestUPDATESerializeTest() throws MessageSerializerException {
		ObjectRequest operation = new ObjectRequestBuilder(
				ObjectRequest.RequestType.UPDATE, 4L)
			.setOverride(true)
			.addData(
					new ObjectDataBuilder()
						.setId(4L)
						.addIntAttribute("someInt", 6)
						.addStringAttribute("someString", "blablabla")
						.addTimeAttribute("someTime", 6666L).build())
			.build();

		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectRequest");

		ObjectRequest deserialized = (ObjectRequest) serializer
				.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getRequestType(), ObjectRequest.RequestType.UPDATE);
		Assert.assertEquals(deserialized.getJobId(), 4L);
		Assert.assertEquals(deserialized.isOverride(), true);
		Assert.assertNotNull(deserialized.getData());
		Assert.assertEquals(deserialized.getData().size(), 1);
		ObjectData od = (ObjectData) deserialized.getData().toArray()[0];
		Assert.assertEquals(od.getId(), 4L);
		Assert.assertNotNull(od.findAttribute("someInt", AttributeType.INT));
		Assert.assertEquals(od.findAttribute("someInt", AttributeType.INT).getInteger(), 6);
		Assert.assertNotNull(od.findAttribute("someString", AttributeType.STRING));
		Assert.assertEquals(od.findAttribute("someString", AttributeType.STRING).getString(), "blablabla");
		Assert.assertNotNull(od.findAttribute("someTime", AttributeType.TIME));
		Assert.assertEquals(od.findAttribute("someTime", AttributeType.TIME).getTime(), 6666L);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void objectRequestQUERYSerializeTest() throws MessageSerializerException {
		ObjectRequest operation = new ObjectRequestBuilder(
				ObjectRequest.RequestType.QUERY, 4L)
			.addByAttributeNameQuery("someName1", true)
			.addByAttributeNameQuery("someName2", false)
			.addByAttributeNameQuery("someName3", true)
			.build();

		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "ObjectRequest");

		ObjectRequest deserialized = (ObjectRequest) serializer
				.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getRequestType(), ObjectRequest.RequestType.QUERY);
		Assert.assertEquals(deserialized.getJobId(), 4L);
		Assert.assertNotNull(deserialized.getQuery());
		Assert.assertEquals(deserialized.getQuery().size(), 3);
		int res = 0;
		for (QueryStructure qs : deserialized.getQuery()) {
			Assert.assertEquals(qs.getType(), QueryType.BY_ATTR_NAME);
			if ("someName1".equals(qs.getAttributeName())
					&& qs.isNegate()) {
				res += 1;
			}
			if ("someName2".equals(qs.getAttributeName())
					&& !qs.isNegate()) {
				res += 10;
			}
			if ("someName3".equals(qs.getAttributeName())
					&& qs.isNegate()) {
				res += 100;
			}
		}
		Assert.assertEquals(res, 111);
	}

}
