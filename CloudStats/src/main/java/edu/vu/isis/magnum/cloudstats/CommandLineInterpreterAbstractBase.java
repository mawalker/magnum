package edu.vu.isis.magnum.cloudstats;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public abstract class CommandLineInterpreterAbstractBase {

	/*
	 * Required Methods
	 */

	/**
	 * This sets the output format to 80 character width
	 */
	private static class ClientHelpFormatter extends HelpFormatter {

		public String renderHelpString(Options options) {
			StringBuffer sb = new StringBuffer();
			this.renderOptions(sb, helpOutputWidth, options, helpLeftPad,
					helpDescPad);
			return sb.toString();
		}
	}

	// The Command entered at runtime
	private CommandLine cmd;

	private static int helpOutputWidth = 80;
	private static int helpLeftPad = 0;
	private static int helpDescPad = 10;

	/**
	 * Set the Output Width of the Help message.
	 * 
	 * @param helpOutputWidth
	 *           the width size
	 */
	public static void setHelpOutputWidth(int helpOutputWidth) {
		CommandLineInterpreterAbstractBase.helpOutputWidth = helpOutputWidth;
	}

	/**
	 * Set the Output Left Padding of the Help message.
	 * 
	 * @param helpOutputWidth
	 *           the left padding size
	 */
	public static void setHelpLeftPad(int helpLeftPad) {
		CommandLineInterpreterAbstractBase.helpLeftPad = helpLeftPad;
	}

	/**
	 * Set the Output Description Padding of the Help message.
	 * 
	 * @param helpOutputWidth
	 *           the description padding size
	 */
	public static void setHelpDescPad(int helpDescPad) {
		CommandLineInterpreterAbstractBase.helpDescPad = helpDescPad;
	}

	private HashMap<String, String> parameterArguements = new HashMap<String, String>();
	private HashMap<String, Boolean> hasParameter = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> hasOption = new HashMap<String, Boolean>();
	private HashMap<String, String> optionPossibilities = new HashMap<String, String>();

	private boolean addHelpOption = true;
	static String helpOptionName = "help";

	/**
	 * Abstract CreateOptionsList requirement
	 * 
	 * @return Options to be use for parsing
	 */
	abstract Options createOptionsList();

	/**
	 * Abstract for customLogicForRequiredOptions requirement
	 * 
	 * @return if provided arguments can be processed and ran properly
	 */
	abstract boolean customLogicForRequiredOptions();

	/**
	 * Get the 'HELP' message String
	 * 
	 * @return Help message String
	 */
	public String getHelpString() {
		// formatter to format string nicely
		ClientHelpFormatter formatter = new ClientHelpFormatter();

		String rValue = formatter.renderHelpString(getOptionsAddHelp());
		// get filename of jar
		String jarName = new java.io.File(
				CommandLineInterpreterAbstractBase.class.getProtectionDomain()
						.getCodeSource().getLocation().getPath()).getName();
		rValue = "usage: " + jarName + "\n" + rValue;
		return rValue;
	}

	/**
	 * Get the options from the implementation child class
	 * and add the help option, if not disabled.
	 * 
	 * @return the Options
	 */
	private Options getOptionsAddHelp() {
		Options optionsList = createOptionsList();
		if (addHelpOption) {
			Option helpOption = new Option("h", helpOptionName, false,
					"print this message.");
			optionsList.addOption(helpOption);
		}
		setupMaps(optionsList);
		return optionsList;
	}

	/**
	 * Get the parameter value of an option
	 * 
	 * @param option
	 *           String to lookup
	 * @return the parameter from the command line for this option, or null if
	 *         wasn't provided
	 */
	public final String getParameter(String option) {
		if (optionPossibilities.containsKey(option)) {
			return parameterArguements.get(optionPossibilities.get(option));
		} else {
			return null;
		}
	}

	/**
	 * If the option provided has a parameter
	 * 
	 * @param option
	 *           to look up
	 * @return if the option has a parameter
	 */
	public final boolean hasParameter(String option) {
		if (optionPossibilities.containsKey(option)) {
			return hasParameter.get(optionPossibilities.get(option));
		} else {
			return false;
		}
	}

	public final boolean hasOption(String option) {
		if (optionPossibilities.containsKey(option)) {
			return hasOption.get(optionPossibilities.get(option));
		} else {
			return false;
		}
	}

	/**
	 * Instruct the parsing of the command line.
	 * 
	 * @return if processing should continue
	 */
	final public boolean parseCommandLineArgs(String[] args) {

		/*
		 * This method should not need changed for other CLI implementations.
		 */

		Options options = getOptionsAddHelp();
		CommandLineParser parser = new PosixParser();

		cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("ERORR #2 Parsing of CLI failed  :"
					+ e.getMessage());
			printHelpAndExit();
		}

		for (Option option : cmd.getOptions()) {
			hasOption.put(optionPossibilities.get(option.getOpt()), true);
		}

		@SuppressWarnings("unchecked")
		// thats what it is... this is silly warning...
		// getOptions() should return a Collection<Options>, not just a
		// Collection...
		Iterator<Option> iter = options.getOptions().iterator();

		while (iter.hasNext()) {
			Option option = iter.next();
			boolean optionExists = cmd.hasOption(option.getOpt());
			hasParameter.put(option.getOpt(), optionExists);
			if (optionExists) {
				String str = cmd.getOptionValue(option.getOpt());
				parameterArguements.put(option.getOpt(), str);
			}
		}

		if (hasParameter(helpOptionName)) {
			printHelpAndExit();
		}

		if (customLogicForRequiredOptions() == false) {
			// if missing required parameters, state so.
			System.err.println("Missing some required parameters.");
			printHelpAndExit();
		}
		return true;
	}

	/**
	 * Print the Help Lines and exit the application.
	 */
	private void printHelpAndExit() {
		System.err.println(getHelpString());
		System.exit(0);
	}

	final public void setAddHelpOption(boolean value) {
		this.addHelpOption = value;
	}

	@SuppressWarnings("unchecked")
	// thats what it is... this is silly warning...
	// getOptions() should return a Collection<Options>, not just a Collection...
	private void setupMaps(Options options) {
		for (Option option : (Collection<Option>) options.getOptions()) {
			optionPossibilities.put(option.getOpt(), option.getOpt());
			optionPossibilities.put(option.getLongOpt(), option.getOpt());
		}
	}

}
