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

package pl.nask.hsn2.bus.dispatcher;

import java.util.HashMap;
import java.util.Map;

import pl.nask.hsn2.bus.operations.Operation;

/**
 * Simple implementation of <code>CommandFactory</code> with
 * internal map of commands.
 * 
 *
 */
public class MapBasedCommandFactory implements CommandFactory {

	/**
	 * Internal map of commands.
	 */
	private Map<String, Command<? extends Operation>> commands = new HashMap<String, Command<? extends Operation>>();
	
	/**
	 * Constructor with no commands map.
	 */
	public MapBasedCommandFactory() {
		this(null);
	}
	
	/**
	 * Constructor with commands map provided.
	 * 
	 * @param commandsMap Commands map.
	 */
	public MapBasedCommandFactory(Map<String, Command<? extends Operation>> commandsMap) {
		setCommandsMap(commandsMap);
	}
	
	/**
	 * Sets commands map.
	 * 
	 * @param commandsMap Commands map to be used by the factory.
	 */
	public final void setCommandsMap(Map<String, Command<? extends Operation>> commandsMap) {
		commands.clear();
		if (commandsMap != null && !commandsMap.isEmpty()) {
			commands.putAll(commandsMap);
		}
	}
	
	/**
	 * Add single command to internal command map.
	 * 
	 * @param commandName Command name.
	 * @param command Command instance.
	 */
	public final void addCommand(String commandName, Command<? extends Operation> command) {
		this.commands.put(commandName, command);
	}
	
	@Override
	public final Command<? extends Operation> getCommandFor(String commandName) {
		return commands.get(commandName);
	}

}
