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

import pl.nask.hsn2.bus.dispatcher.CachableCommandFactory;
import pl.nask.hsn2.bus.dispatcher.Command;
import pl.nask.hsn2.bus.dispatcher.CommandExecutionException;
import pl.nask.hsn2.bus.dispatcher.CommandFactory;
import pl.nask.hsn2.bus.dispatcher.ReflectionCommandFactory;
import pl.nask.hsn2.bus.operations.Ping;

public class ReflectionCommandFactoryTest {

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void nullArrayArgumentTest() {
		String[] packages = null;
		new ReflectionCommandFactory(packages);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void emptyArrayArgumentTest() {
		String[] packages = new String[]{};
		new ReflectionCommandFactory(packages);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void nullStringArgumentTest() {
		String packageName = null;
		new ReflectionCommandFactory(packageName);
	}
	
	
	@Test
	public void simpleTest() throws CommandExecutionException {
		
		CommandFactory cf = new ReflectionCommandFactory("pl.nask.hsn2.bus.commands");
		
		@SuppressWarnings("unchecked")
		Command<Ping> command = (Command<Ping>) cf.getCommandFor("Ping");
		
		Assert.assertNotNull(command);
	}
	
	@Test
	public void simnpleTestWithCache() throws CommandExecutionException {
		
		CommandFactory cf = new CachableCommandFactory(
				new ReflectionCommandFactory("pl.nask.hsn2.bus.commands"));
		
		@SuppressWarnings("unchecked")
		Command<Ping> command1 = (Command<Ping>) cf.getCommandFor("Ping");
		@SuppressWarnings("unchecked")
		Command<Ping> command2 = (Command<Ping>) cf.getCommandFor("Ping");
		@SuppressWarnings("unchecked")
		Command<Ping> command3 = (Command<Ping>) cf.getCommandFor("Ping");
		
		Assert.assertNotNull(command1);
		Assert.assertNotNull(command2);
		Assert.assertNotNull(command3);
		Assert.assertEquals(command1 == command2, true);
		Assert.assertEquals(command1 == command3, true);
	}
	
}
