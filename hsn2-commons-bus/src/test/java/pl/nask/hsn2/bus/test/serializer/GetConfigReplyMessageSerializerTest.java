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
import pl.nask.hsn2.bus.operations.GetConfigReply;
import pl.nask.hsn2.bus.operations.builder.GetConfigReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class GetConfigReplyMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void getConfigReplyNoPropsSerializeTest() throws MessageSerializerException {
		GetConfigReply operation = new GetConfigReplyBuilder()
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "GetConfigReply");
		
		GetConfigReply deserialized = (GetConfigReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getProperties());
		Assert.assertEquals(deserialized.getProperties().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getConfigReplyWithPropsSerializeTest() throws MessageSerializerException {
		GetConfigReply operation = new GetConfigReplyBuilder()
			.addProperty("p1", "ttt")
			.addProperty("p2", "ooo")
			.addProperty("p2", "uuu")
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "GetConfigReply");
		
		GetConfigReply deserialized = (GetConfigReply) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getProperties());
		Assert.assertEquals(deserialized.getProperties().size(), 2);
		Assert.assertEquals(deserialized.getProperties().getProperty("p1"), "ttt");
		Assert.assertEquals(deserialized.getProperties().getProperty("p2"), "uuu");
	}

}
