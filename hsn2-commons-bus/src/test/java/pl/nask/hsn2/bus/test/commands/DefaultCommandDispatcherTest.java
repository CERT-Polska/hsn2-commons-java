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

package pl.nask.hsn2.bus.test.commands;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.commands.PingCmd;
import pl.nask.hsn2.bus.dispatcher.CommandContext;
import pl.nask.hsn2.bus.dispatcher.CommandDispatcher;
import pl.nask.hsn2.bus.dispatcher.DefaultCommandDispatcher;
import pl.nask.hsn2.bus.dispatcher.MapBasedCommandFactory;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.operations.Ping;

public class DefaultCommandDispatcherTest {

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void nullCommandFactoryTest() {
		new DefaultCommandDispatcher(null);
	}
	
	@Test
	public void simpleTest() {
		MapBasedCommandFactory mapFactory = new MapBasedCommandFactory();
		mapFactory.addCommand("c1", new PingCmd());
		
		CommandDispatcher dispatcher = new DefaultCommandDispatcher(mapFactory);
		
		CommandContext<Operation> context = new CommandContext<Operation>();
		context.setSourceOperation(new Ping());
		
		context.setSourceMessage(new Message("c2", null, null));
		Assert.assertNull(dispatcher.dispatch(context));

		context.setSourceMessage(new Message("c1", null, null));
		Assert.assertNotNull(dispatcher.dispatch(context));
	}
}
