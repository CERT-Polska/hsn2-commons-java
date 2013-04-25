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

package pl.nask.hsn2;

import java.io.File;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GenericServiceInfo {
	private static final Logger LOG = LoggerFactory.getLogger(GenericServiceInfo.class);
	private static String serviceName;

	private GenericServiceInfo() {
		// Can't instantiate utility class.
	}

	static void setServiceName(String serviceName) {
		GenericServiceInfo.serviceName = serviceName;
	}

	public static String getServiceName() {
		return serviceName;
	}

	/**
	 * @param clazz
	 *            main service class
	 * @return directory path where jar is placed
	 * @throws URISyntaxException
	 */
	public static String getServicePath(Class<?> clazz) throws URISyntaxException {
		String clazzPath = clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		File clazzFile = new File(clazzPath);
		String corePath = clazzFile.getParent();
		LOG.debug("Jar location: {}", corePath);
		return corePath;
	}
}
