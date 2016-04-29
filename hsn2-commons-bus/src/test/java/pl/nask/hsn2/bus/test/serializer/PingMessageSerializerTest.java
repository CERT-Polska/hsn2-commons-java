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
import pl.nask.hsn2.bus.operations.Ping;
import pl.nask.hsn2.bus.operations.builder.PingBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class PingMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void pingSerializeTest() throws MessageSerializerException {

		Ping operation = new PingBuilder().build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "Ping");
		
		Ping deserialized = (Ping) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertFalse(deserialized.isResponse());
		
		operation = new PingBuilder(true).build(); // this is response
		message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "Ping");
		
		deserialized = (Ping) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertTrue(deserialized.isResponse());
	}

}
