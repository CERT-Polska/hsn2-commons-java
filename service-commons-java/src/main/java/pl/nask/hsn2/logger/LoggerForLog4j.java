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

package pl.nask.hsn2.logger;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public final class LoggerForLog4j implements LoggerManager {
	private static LoggerForLog4j loggerForLog4j;

	private Level defaultLevel;
	private String defaultLogFile;
	private FileAppender fileAppender;
	private Logger rootLogger;

	private LoggerForLog4j(){
		rootLogger = Logger.getRootLogger();
		defaultLevel = rootLogger.getLevel();
		fileAppender = (FileAppender) rootLogger.getAppender("PRIMARY");
		defaultLogFile = fileAppender.getFile();
	}

	@Override
	public void setLogLevel(String levelName) {
		Level level = Level.toLevel(levelName, defaultLevel);
		rootLogger.setLevel(level);
	}

	@Override
	public void setLogFile(String path) {
		if (fileAppender != null){
			fileAppender.setFile(path);
			fileAppender.activateOptions();
		}
	}

	@Override
	public String getDefaultLogLevel(){
		return defaultLevel.toString();
	}

	@Override
	public String getDefaultLogFile() {
		return defaultLogFile;
	}

	public static synchronized LoggerManager getInstance() {
		if (loggerForLog4j == null){
			loggerForLog4j = new LoggerForLog4j();
		}
		return loggerForLog4j;
	}
}
