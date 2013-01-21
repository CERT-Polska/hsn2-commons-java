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

package pl.nask.hsn2.bus.test.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.operations.builder.OperationBuilder;
import pl.nask.hsn2.bus.utils.PackageScanner;
import pl.nask.hsn2.bus.utils.PackageScannerException;

public class OperationTest {

	private static Logger LOGGER = LoggerFactory.getLogger(OperationTest.class);

	/**
	 * Checks consistency of all <code>Operation</code> implementations.
	 * @throws PackageScannerException 
	 * @throws ClassNotFoundException 
	 * 
	 */
	@Test
	public void testImplementsOperation() throws PackageScannerException, ClassNotFoundException {

		for (Class<?> clazz : PackageScanner.getClassesForPackage("pl.nask.hsn2.bus.operations")) {
			LOGGER.info("Checking instance of Operation: {}", clazz.getName());

			// checks if all class implements Operation
			Assert.assertTrue(hasInterface(clazz, Operation.class), "Class " + clazz + " is expected to implement Operation");

			// DON'T look for builders for the anonymous classes!
			if (!clazz.isAnonymousClass()) {
				String[] tokens = clazz.getName().split("\\.");
				String builderName =
						"pl.nask.hsn2.bus.operations.builder."
								+ tokens[tokens.length - 1]
										+ "Builder";

				LOGGER.info("Checking if appropriate builder exists {}", builderName);
				Class<?> builderClass = Class.forName(builderName);

				// checks if builder implements OperationBuilder
				Assert.assertTrue(hasInterface(builderClass, OperationBuilder.class), "Class " + builderClass + " is expected to implement OperationBuilder");
			}
		}

	
	}

	private boolean hasInterface(Class<?> clazz, Class<?> iface) {
		for (Class<?> c : clazz.getInterfaces()) {
	        if (c.equals(iface)) {
	            return true;
	        }
	    }  
		
		if (clazz.getSuperclass() != null) {
			return hasInterface(clazz.getSuperclass(), iface);
		} else {
			return false;
		}
	}
}
