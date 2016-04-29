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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.commands.PingCmd;
import pl.nask.hsn2.bus.dispatcher.CombinedCommandFactory;
import pl.nask.hsn2.bus.dispatcher.Command;
import pl.nask.hsn2.bus.dispatcher.CommandFactory;
import pl.nask.hsn2.bus.dispatcher.MapBasedCommandFactory;
import pl.nask.hsn2.bus.operations.Operation;

public class CombinedCommandFactoryTest {

	private Command<? extends Operation> simpleCommand = new PingCmd();

	
	@Test
	@SuppressWarnings("unchecked")
	public void simpleCombinedCommandFactoryTest() {
		CombinedCommandFactory factory = new CombinedCommandFactory();
		Assert.assertNull(factory.getCommandFor("some command"));
		Assert.assertNull(factory.getCommandFor(null));
		
		factory = new CombinedCommandFactory(null);
		Assert.assertNull(factory.getCommandFor("some command"));
		Assert.assertNull(factory.getCommandFor(null));

		factory = new CombinedCommandFactory(Collections.EMPTY_LIST);
		Assert.assertNull(factory.getCommandFor("some command"));
		Assert.assertNull(factory.getCommandFor(null));

		List<CommandFactory> factoriesList = new ArrayList<CommandFactory>();
		factory.setFactories(factoriesList);
		Assert.assertNull(factory.getCommandFor("some command"));
		Assert.assertNull(factory.getCommandFor(null));

		MapBasedCommandFactory factory1 = new MapBasedCommandFactory();
		factory1.addCommand("c1", simpleCommand);
		MapBasedCommandFactory factory2 = new MapBasedCommandFactory();
		factory2.addCommand("c2", simpleCommand);
		factoriesList.add(factory1);
		factoriesList.add(factory2);
		factory.setFactories(factoriesList);
		Assert.assertNotNull(factory.getCommandFor("c1"));
		Assert.assertNotNull(factory.getCommandFor("c2"));
	}
}
