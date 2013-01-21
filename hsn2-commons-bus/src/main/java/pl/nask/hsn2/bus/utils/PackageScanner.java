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

package pl.nask.hsn2.bus.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PackageScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageScanner.class);
	
	private PackageScanner() {
		// this is utility class and cannot be instantiated
	}
	
	public static List<Class<?>> getClassesForPackage(String pkgname)
			throws PackageScannerException {

		try {
			String relPath = pkgname.replace('.', '/');
			LOGGER.debug("Looking for resource: {}", relPath);
			Enumeration<URL> resource = Thread.currentThread().getContextClassLoader().getResources(relPath);
	
			List<Class<?>> classes = new ArrayList<Class<?>>(); 
			while (resource.hasMoreElements()) {
				URL packageURL = resource.nextElement();
				LOGGER.debug("URL Path: {}", packageURL);
				if (packageURL.getProtocol().equals("jar")) {
					LOGGER.debug("Resource is a part of JAR.");
					String jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
					jarFileName = jarFileName.substring(5,jarFileName.indexOf('!'));
					JarFile jf = null;
					try {
						jf = new JarFile(jarFileName);
						Enumeration<JarEntry> jarEntries = jf.entries();
						while(jarEntries.hasMoreElements()){
							String entryName = jarEntries.nextElement().getName();
							if (entryName.startsWith(relPath)
									&& entryName.length() > relPath.length() + 6
									&& entryName.endsWith(".class")) {
								entryName = entryName.replace('/', '.').substring(0, entryName.length()-6);

								Class<?> clazz = Class.forName(entryName);

								// we don't care about interfaces and enums
								if (clazz.isInterface() || clazz.isEnum()) {
									continue;
								}
								classes.add(clazz);
							}
						}
					} finally {
						if (jf != null)
							jf.close();
					}
				} else {
					LOGGER.debug("Resource is not a part of JAR.");

					URI uriPath = new URI(packageURL.toString());
					File directory = new File(uriPath);

					if (directory == null || !directory.exists()) {
				        throw new PackageScannerException("No directory for " + packageURL);
					}

					for (String filename : directory.list()) {
						if (!filename.endsWith(".class")) {
							continue;
						}
						String className = pkgname + '.' + filename.substring(0, filename.length() - 6);
						Class<?> clazz = Class.forName(className);
						
						// we don't care about interfaces and enums
						if (clazz.isInterface() || clazz.isEnum()) {
							continue;
						}
						classes.add(clazz);
					}
				}
			}
			LOGGER.debug("Found {} classes.", classes.size());
			return classes;
		} catch (IOException e) {
			throw new PackageScannerException(e);
		} catch (URISyntaxException e) {
			throw new PackageScannerException(e);
		} catch (ClassNotFoundException e) {
			throw new PackageScannerException(e);
		}
	}

}
