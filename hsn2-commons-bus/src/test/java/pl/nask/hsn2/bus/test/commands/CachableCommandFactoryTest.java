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

package pl.nask.hsn2.bus.test.commands;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.commands.PingCmd;
import pl.nask.hsn2.bus.dispatcher.CachableCommandFactory;
import pl.nask.hsn2.bus.dispatcher.Command;
import pl.nask.hsn2.bus.dispatcher.MapBasedCommandFactory;
import pl.nask.hsn2.bus.operations.Ping;

public class CachableCommandFactoryTest {

	@Test(expectedExceptions={IllegalArgumentException.class})
	public void cachableCommandFactoryNullTest() {
		new CachableCommandFactory(null);
	}

	@Test
	public void cachableCommandFactoryTest() {
		
		MapBasedCommandFactory delegate = new MapBasedCommandFactory();
		delegate.addCommand("c1", new PingCmd());
		
		CachableCommandFactory factory = new CachableCommandFactory(delegate);
		
		Assert.assertNull(factory.getCommandFor("unknown"));
	
		@SuppressWarnings("unchecked")
		Command<Ping> cmd = (Command<Ping>) factory.getCommandFor("c1");
	
		Assert.assertNotNull(cmd);
		Assert.assertEquals(factory.getCommandFor("c1"), cmd); // the same bcoz coming from cache
	}
}
