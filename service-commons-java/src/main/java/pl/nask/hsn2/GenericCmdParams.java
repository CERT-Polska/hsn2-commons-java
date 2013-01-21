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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericCmdParams {
	private static final Logger LOGGER = LoggerFactory.getLogger("CommandLineParams");
	private Map<String, String> defaults = new HashMap<String, String>();

	private Options options;
	private CommandLine cmd;

	/**
	 * Creates params object based on the command line parameters. If 'help'
	 * parameter was supplied and the parser supports help option, help is
	 * displayed and application exits. If there is an error parsing command
	 * line parameters, help is displayed and System.exit(1) is invoked.
	 * 
	 * @param args
	 * @return
	 */
	public final void parseParams(String[] args) {
		if (options == null) {
			// lazy init
			init();
		}

		int exitCode = 1;
		try {
			this.cmd = new PosixParser().parse(options, args);
			initLogger();

			if (cmd.hasOption("h")) {
				cmd = null;
				exitCode = 0;
			} else {
				printOptions();
				validate();
			}
		} catch (ParseException e) {
			LOGGER.info(e.getMessage());
		} catch (IllegalStateException e) {
			cmd = null;
			LOGGER.info(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (cmd == null) {
			printHelp();
			System.exit(exitCode);
		}
	}

	@SuppressWarnings("static-access")
	private void init() {
		if (options != null) {
			throw new IllegalStateException("Object already initialized");
		}
		initDefaults();

		options = new Options();
		options.addOption(OptionBuilder.withLongOpt("help").withDescription("Prints this help information").create("h"));

		initOptions();
	}

	protected abstract void initLogger();

	/**
	 * Subclass should implement this method to add it's own options The method
	 * is called by the init()
	 */
	protected abstract void initOptions();

	/**
	 * Subclass should implement this method to set option defaults. This method
	 * is called by the init() BEFORE initOptions()
	 */
	protected abstract void initDefaults();

	protected void validate() {
	}

	@SuppressWarnings("static-access")
	protected final void addOption(String optionName, String longOptionName, String argName, String description) {
		if (options == null) {
			throw new IllegalStateException("Not initialized. Call init() first");
		}

		String defaultValue = getDefaultValue(optionName);
		options.addOption(OptionBuilder.withArgName(argName).withLongOpt(longOptionName).isRequired(defaultValue == null).hasArg()
				.withDescription(optionDescription(description, defaultValue)).create(optionName));
	}

	protected final void setDefaultValue(String optionName, String value) {
		if (options != null) {
			throw new IllegalStateException("Object already initialized");
		}

		defaults.put(optionName, value);
	}

	protected final void setDefaultIntValue(String optionName, Integer value) {
		if (value == null) {
			setDefaultValue(optionName, null);
		} else {
			setDefaultValue(optionName, value.toString());
		}
	}

	protected final String getOptionValue(String optionName) {
		String optionValue = cmd.getOptionValue(optionName);
		return optionValue != null ? optionValue : getDefaultValue(optionName);
	}

	protected final Integer getOptionIntValue(String optionName) {
		String optionValue = getOptionValue(optionName);

		return optionValue == null ? null : Integer.parseInt(optionValue);
	}

	@SuppressWarnings("static-access")
	protected final void addOption(OptionNameWrapper optionName, String argName, String description) {
		if (options == null) {
			throw new IllegalStateException("Not initialized. Call init() first");
		}

		String defaultValue = getDefaultValue(optionName);
		options.addOption(OptionBuilder.withArgName(argName).withLongOpt(optionName.getLongName()).isRequired(defaultValue == null).hasArg()
				.withDescription(optionDescription(description, defaultValue)).create(optionName.getName()));
	}

	protected final void setDefaultValue(OptionNameWrapper optionType, String value) {
		setDefaultValue(optionType.getName(), value);
	}

	protected final void setDefaultIntValue(OptionNameWrapper optionType, Integer value) {
		setDefaultIntValue(optionType.getName(), value);
	}

	protected final String getOptionValue(OptionNameWrapper optionType) {
		return getOptionValue(optionType.getName());
	}

	protected final Integer getOptionIntValue(OptionNameWrapper optionType) {
		return getOptionIntValue(optionType.getName());
	}

	protected final String getCmdLineSyntax() {
		return "java -jar jar_file";
	}

	private void printOptions() {
		Option[] opts = cmd.getOptions();
		for (Option op : opts) {
			LOGGER.info("Command line option: {}. Value from command line: {}, default value={}",
					new Object[] { op.getOpt(), op.getValue(), getDefaultValue(op.getOpt()) });
		}
	}

	private String getDefaultValue(String optionName) {
		return defaults.get(optionName);
	}

	private String getDefaultValue(OptionNameWrapper optionName) {
		return defaults.get(optionName.getName());
	}

	private String optionDescription(String description, Object defaultValue) {
		String output = description.trim();
		boolean addDot = !description.trim().endsWith(".");
		if (addDot) {
			output += ".";
		}

		if (defaultValue != null) {
			output += (" Default value='" + defaultValue + "'. ");
		}

		return output;
	}

	private void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getCmdLineSyntax(), options);
	}

	public static final class OptionNameWrapper {
		private final String name;
		private final String longName;

		public OptionNameWrapper(String shortName, String longName) {
			this.name = shortName;
			this.longName = longName;
		}

		public String getName() {
			return name;
		}

		public String getLongName() {
			return longName;
		}
	}

	protected final boolean isOptionInCmd(OptionNameWrapper optionType) {
		return cmd.hasOption(optionType.getName());
	}
}
