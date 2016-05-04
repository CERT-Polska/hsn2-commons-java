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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.commands.PingCmd;
import pl.nask.hsn2.bus.dispatcher.Command;
import pl.nask.hsn2.bus.dispatcher.MapBasedCommandFactory;
import pl.nask.hsn2.bus.operations.Operation;

public class MapBasedCommandFactoryTest {

	private Command<? extends Operation> simpleCommand = new PingCmd();
	
	@Test
	@SuppressWarnings("unchecked")
	public void simpleMapBasedCommandFactoryTest() {
		MapBasedCommandFactory factory = new MapBasedCommandFactory();
		
		Assert.assertNull(factory.getCommandFor("some command name"));
		factory.addCommand("command1", simpleCommand);
		Assert.assertNotNull(factory.getCommandFor("command1"));
		Assert.assertNull(factory.getCommandFor(null));

		factory.setCommandsMap(null);
		Assert.assertNull(factory.getCommandFor("command1"));
		
		factory = new MapBasedCommandFactory(Collections.EMPTY_MAP);
		Assert.assertNull(factory.getCommandFor("command1"));
		
		Map<String, Command<? extends Operation>> commandsMap = new HashMap<String, Command<? extends Operation>>();
		commandsMap.put("c1", simpleCommand);
		commandsMap.put("c2", simpleCommand);
		commandsMap.put("c3", simpleCommand);
		factory = new MapBasedCommandFactory(commandsMap);
		factory.addCommand("c4", simpleCommand);
		Assert.assertNotNull(factory.getCommandFor("c1"));
		Assert.assertNotNull(factory.getCommandFor("c2"));
		Assert.assertNotNull(factory.getCommandFor("c3"));
		Assert.assertNotNull(factory.getCommandFor("c4"));
	}
}
