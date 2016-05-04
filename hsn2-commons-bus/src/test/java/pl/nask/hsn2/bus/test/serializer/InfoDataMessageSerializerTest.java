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
import pl.nask.hsn2.bus.operations.Attribute;
import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.InfoData;
import pl.nask.hsn2.bus.operations.InfoType;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.Reference;
import pl.nask.hsn2.bus.operations.builder.InfoDataBuilder;
import pl.nask.hsn2.bus.operations.builder.ObjectDataBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class InfoDataMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void infoDataSerializeTest() throws MessageSerializerException {
		ObjectData od = new ObjectDataBuilder()
			.setId(5L)
			.addAttribute(new Attribute("enabled", AttributeType.BOOL, true))
			.addAttribute(new Attribute("name", AttributeType.STRING, "some name"))
			.addAttribute(new Attribute("created", AttributeType.TIME, 6L))
			.addAttribute(new Attribute("some_object", AttributeType.OBJECT, 9L))
			.addAttribute(new Attribute("objCount", AttributeType.INT, 19))
			.addAttribute(new Attribute("raw data", AttributeType.BYTES, new Reference(7L, 4)))
			.build();
		
		InfoData operation = new InfoDataBuilder(InfoType.JOB, od)
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "InfoData");
		
		InfoData deserialized = (InfoData) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), InfoType.JOB);
		Assert.assertNotNull(deserialized.getData());
		Assert.assertEquals(deserialized.getData().getId(), 5L);
		Assert.assertNotNull(deserialized.getData().findAttribute("enabled", AttributeType.BOOL));
		Assert.assertEquals(deserialized.getData().findAttribute("enabled", AttributeType.BOOL).getBool(), true);
		Assert.assertNotNull(deserialized.getData().findAttribute("name", AttributeType.STRING));
		Assert.assertEquals(deserialized.getData().findAttribute("name", AttributeType.STRING).getString(), "some name");
		Assert.assertNotNull(deserialized.getData().findAttribute("created", AttributeType.TIME));
		Assert.assertEquals(deserialized.getData().findAttribute("created", AttributeType.TIME).getTime(), 6L);
		Assert.assertNotNull(deserialized.getData().findAttribute("some_object", AttributeType.OBJECT));
		Assert.assertEquals(deserialized.getData().findAttribute("some_object", AttributeType.OBJECT).getObejectRef(), 9L);
		Assert.assertNotNull(deserialized.getData().findAttribute("objCount", AttributeType.INT));
		Assert.assertEquals(deserialized.getData().findAttribute("objCount", AttributeType.INT).getInteger(), 19);
		Assert.assertNotNull(deserialized.getData().findAttribute("raw data", AttributeType.BYTES));
		Assert.assertNotNull(deserialized.getData().findAttribute("raw data", AttributeType.BYTES).getBytes());
		Assert.assertEquals(deserialized.getData().findAttribute("raw data", AttributeType.BYTES).getBytes().getKey(), 7L);
		Assert.assertEquals(deserialized.getData().findAttribute("raw data", AttributeType.BYTES).getBytes().getStore(), 4);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void infoDataNoAttrsSerializeTest() throws MessageSerializerException {
		ObjectData od = new ObjectDataBuilder()
			.setId(51L)
			.build();
		
		InfoData operation = new InfoDataBuilder(InfoType.COMPONENT, od)
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "InfoData");
		
		InfoData deserialized = (InfoData) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getType(), InfoType.COMPONENT);
		Assert.assertNotNull(deserialized.getData());
		Assert.assertEquals(deserialized.getData().getId(), 51L);
		Assert.assertNotNull(deserialized.getData().getAttributes());
		Assert.assertEquals(deserialized.getData().getAttributes().size(), 0);
	}

}
