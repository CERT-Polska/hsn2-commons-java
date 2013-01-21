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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.Operation;

/**
 * This is default command dispatcher.
 * 
 * This command dispatcher is thread-safe all commands should be as well. 
 * 
 *
 */
public class DefaultCommandDispatcher implements CommandDispatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandDispatcher.class);
	
	private final CommandFactory commandFactory;

	/**
	 * Default constructor.
	 * 
	 * @param commandFactory Factory used for produce commands processors.
	 */
	public DefaultCommandDispatcher(CommandFactory commandFactory) {
		if (commandFactory == null) {
			throw new IllegalArgumentException("Command factory cannot be null.");
		}
		this.commandFactory = commandFactory;
	}

	@Override
	public final Operation dispatch(CommandContext<Operation> context) {

		@SuppressWarnings("unchecked")
		Command<Operation> cmd = (Command<Operation>) commandFactory.getCommandFor(context.getSourceMessage().getType());

		if (cmd != null) {
			LOGGER.debug("Got command processor {}.", cmd.getClass()
					.getName());
			LOGGER.debug("Executing...");
			try {
				return cmd.execute(context);
			} catch (CommandExecutionException e) {
				LOGGER.error("Error executing command.", e);
			}
			LOGGER.debug("...Command executed");
		} else {
			LOGGER.error(
					"No command processor found, discarding operation: {}",
					context.getSourceMessage().getType());
		}
		return null;
	}
}
