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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.Operation;

/**
 * This is cache wrapper for any <code>CommandFactory</code>. The best
 * performance can be achieved if commands will be singletons and can be reused.
 * If the factory creates such instances they can be cached. Not all factories
 * will create singletons (not recommended) this wrapper is optional.
 * 
 * 
 */
public class CachableCommandFactory implements CommandFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(CachableCommandFactory.class);
	
	/**
	 * Real <code>CommandFactory</code>.
	 */
	private CommandFactory delegate;
	
	/**
	 * Cache of commands created by delegate.
	 */
	private Map<String, Command<? extends Operation>> cache = new HashMap<String, Command<? extends Operation>>();
	
	/**
	 * Default constructor.
	 * 
	 * @param delegate Real command factory.
	 */
	public CachableCommandFactory(CommandFactory delegate) {
		if (delegate == null) {
			throw new IllegalArgumentException("CommandFactory need to be provided.");
		}
		this.delegate = delegate;
	}
	
	@Override
	public final Command<? extends Operation> getCommandFor(String commandName) {
		
		Command<? extends Operation> command = cache.get(commandName);
		if (command == null) {
			command = delegate.getCommandFor(commandName);
			if (command != null) {
				LOGGER.debug("Adding command to cache.");
				cache.put(commandName, command);
			}
		} else {
			LOGGER.debug("Found command in cache.");
		}
		return command;
	}

}
