package edu.vu.isis.magnum.cloudstats;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CommandLineInterpreter extends CommandLineInterpreterAbstractBase {

	public static String fieldsOptionName = "fields";
	public static String fileInputOptionName = "inputfile";
	public static String fileListOptionName = "fileLists";
	public static String outputFileOptionName = "outputFile";
	public static String singleAttributesOptionName = "singleAttribute";
	public static String testPowerSetOptionName = "powerSet";
	public static String selectStatMethodOptionName = "statMethod";


	/**
	 * Determine if required options were given or not.
	 * 
	 * @return
	 */
	@Override
	final boolean customLogicForRequiredOptions() {
		return (hasParameter(fileInputOptionName) || hasParameter(fileListOptionName));
	}

	/**
	 * Create Options List
	 * 
	 * @return Options for the
	 */
	@Override
	protected Options createOptionsList() {
		Options options = new Options();

		Option fileOption = new Option("f", fileInputOptionName, true,
				"File to process");

		Option fileListOption = new Option("F", fileListOptionName, true,
				"File of Files to process");

		Option fieldsOption = new Option("s", fieldsOptionName, true,
				"Select Fields to use in Calculations. (Optional)");

		Option outputFileOption = new Option("w", outputFileOptionName, true,
				"Write output to this file. (Optional)");

		Option singleAttributeOption = new Option("c",
				singleAttributesOptionName, false,
				"Choose Single-Only Attribute for Calculations. (Optional)");

		Option powerSetAttributeOption = new Option("p", testPowerSetOptionName,
				false, "Enable Power Set calculation of Attributes List.");

		Option selectStatisticalMethodOption = new Option("m",
				selectStatMethodOptionName, true,
				"Select statistical method to use for calculations. (arg=> Linear|...)");

		options.addOption(fileOption);
		options.addOption(fileListOption);
		options.addOption(fieldsOption);
		options.addOption(outputFileOption);
		options.addOption(singleAttributeOption);
		options.addOption(powerSetAttributeOption);
		options.addOption(selectStatisticalMethodOption);

		return options;
	}

}
