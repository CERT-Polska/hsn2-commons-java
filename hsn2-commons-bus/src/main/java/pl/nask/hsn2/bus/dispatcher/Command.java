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

import pl.nask.hsn2.bus.operations.Operation;

/**
 * This is general interface for commands.
 * Commands basically are atomic operations performed by
 * the Framework or any service. 
 * 
 *
 */
public interface Command<T extends Operation> {

	/**
	 * Executes command.
	 * 
	 * @throws CommandExecutionException If there is a problem with processing command
	 *                      <code>CommandException</code> will rise.
	 */
    Operation execute(CommandContext<T> context) throws CommandExecutionException;
}
