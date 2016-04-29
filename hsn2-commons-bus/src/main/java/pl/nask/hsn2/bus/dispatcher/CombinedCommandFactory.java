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

package pl.nask.hsn2.bus.dispatcher;

import java.util.LinkedList;
import java.util.List;

import pl.nask.hsn2.bus.operations.Operation;

/**
 * This is implementation of <code>CommandFactory</code> which allows
 * to combine more then one factory together.
 * 
 *
 */
public class CombinedCommandFactory implements CommandFactory {

	/**
	 * Internal factories list.
	 */
	private List<CommandFactory> factories = new LinkedList<CommandFactory>();
	
	/**
	 * Default constructor with no factories provided.
	 */
	public CombinedCommandFactory() {
		this(null);
	}
	
	/**
	 * Constructor with factories provided.
	 * 
	 * @param factories List of factories to be used.
	 */
	public CombinedCommandFactory(List<CommandFactory> factories) {
		setFactories(factories);
	}

	/**
	 * Sets factories list to be used by this combined command factory.
	 * 
	 * @param factories List of command factories.
	 */
	public final void setFactories(List<CommandFactory> factories) {
		this.factories.clear();
		if (factories != null && !factories.isEmpty()) {
			this.factories.addAll(factories);
		}
	}

	@Override
	public final Command<? extends Operation> getCommandFor(String commandName) {
		Command<? extends Operation> command = null;
		for (CommandFactory factory : this.factories) {
			command = factory.getCommandFor(commandName);
			if (command != null)
				break;
		}
		return command;
	}

}
