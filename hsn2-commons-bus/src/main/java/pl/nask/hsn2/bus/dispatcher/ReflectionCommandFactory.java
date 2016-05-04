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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.Operation;

/**
 * This is an implementation of <code>CommandFactory</code>
 * basing on available commands in specified packages.
 * 
 * NOTE: This factory assume that commands have 'Cmd' suffix.
 * 
 *
 */
public class ReflectionCommandFactory implements CommandFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionCommandFactory.class);
	
	/**
	 * Packages to be scanned.
	 */
	private String[] packageNames = new String[]{};

	/**
	 * Default constructor with one package provided.
	 * 
	 * @param packageName Package to be scanned for commands.
	 */
	public ReflectionCommandFactory(String packageName){
		if (packageName == null) {
			throw new IllegalArgumentException("Null package name is not allowed.");
		}
		setPackagesNames(new String[]{packageName});
	}

	/**
	 * Constructor with provided many packages to be scanned.
	 * 
	 * @param packageNames Array of packages to be scanned for commands.
	 */
	public ReflectionCommandFactory(String[] packageNames){
		setPackagesNames(packageNames);
	}
	
	/**
	 * Sets packages names for this factory.
	 * 
	 * @param packageNames Array of packages names.
	 */
	public final void setPackagesNames(String[] packageNames) {
		if (packageNames == null  || packageNames.length == 0) {
			throw new IllegalArgumentException("Packages cannot be null or empty.");
		}
		this.packageNames = packageNames.clone();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final Command<Operation> getCommandFor(String commandName) {
		for (String packageName : packageNames) {
			try {
				Class<?> clazz = Class.forName(packageName + "." + commandName + "Cmd");
				return (Command<Operation>) clazz.newInstance();
			} catch (ClassNotFoundException e) {
				LOGGER.debug("Cannot find class for command with name: {} in package {}", commandName, packageName);
			} catch (InstantiationException e) {
				LOGGER.debug("Cannot instantiate command with name: {} in package {}", commandName, packageName);
			} catch (IllegalAccessException e) {
				LOGGER.debug("Cannot access to command with name: {} in package {}", commandName, packageName);
			}
		}
		LOGGER.error("Cannot find commans with name: {}", commandName);
		return null;
	}

}
