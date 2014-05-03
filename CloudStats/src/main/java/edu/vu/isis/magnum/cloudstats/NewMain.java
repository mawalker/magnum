package edu.vu.isis.magnum.cloudstats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.vu.isis.magnum.cloudstats.stats.CsvProcessor;
import edu.vu.isis.magnum.cloudstats.stats.StatisticsCalculator;
import edu.vu.isis.magnum.cloudstats.stats.WebTestJsonReader;

public class NewMain {

	private static final String[] TARGET_FIELDS = { "cpuUsage", "memory",
			"networkEth0", "cpu", "mean" };

	public final static String[] COLUMNS_TO_PRINT = { "correlationCoefficient",
			"meanAbsoluteError", "errorRate", "relativeAbsoluteError",
			"rootRelativeSquaredError" };

	private static final String[] TEST_DIRECTORIES = { "bm-9-tornade",
			"bm-2-go", "bm-3-nodejs", "bm-7-netty", "bm-1-jetty-servlet", };

	private static final String[] FILE_NAMES = {
			"c1.medium-throughput-perm-processed.json",
			"c1.xlarge-throughput-perm-processed.json",
			"c3.large-throughput-perm-processed.json",
			"c3.xlarge-throughput-perm-processed.json",
			"m3.xlarge-throughput-perm-processed.json" };

	static ArrayList<ArrayList<String>> cartesiantProductTests = Utils
			.calculateCartesianProduct(new String[][] { TEST_DIRECTORIES,
					FILE_NAMES, TARGET_FIELDS });

	public static void main(String[] args) throws Exception {

		/*
		 * Create main to do logic, and cli to parse arguments
		 */
		NewMain main = new NewMain();
		CommandLineInterpreter cli = new CommandLineInterpreter();

		/*
		 * Parse arguments
		 */
		cli.parseCommandLineArgs(args);

		CsvProcessor csvProcessor = new CsvProcessor();

		ArrayList<String> colToPrint = main.getColumnsToPrint(cli);

		ArrayList<String> jsonFilesList = main.getJsonFilesToProcess(cli);

		String csvHeaderString = csvProcessor.getCsvHeader(cli);

		String csvFile = csvHeaderString;

		ArrayList<List<String>> tests = main.getAttributesToCalculate(cli);

		for (List<String> list : tests) {
			list.add(0, "count");

			for (String filePath : jsonFilesList) {
				csvFile += list.size() + "; ";
				int startChar = (filePath.length() - (filePath.length() / 2));
				csvFile += getCSVLine(
						filePath.subSequence(startChar, filePath.length()).toString()
								+ list, filePath, (ArrayList<String>) list, colToPrint);
			}

		}
		main.outputBasedOnParameters(cli, csvFile);

	}

	public ArrayList<String> getColumnsToPrint(CommandLineInterpreter cli) {
		ArrayList<String> colToPrint = new ArrayList<String>(
				Arrays.asList(COLUMNS_TO_PRINT));
		return colToPrint;
	}

	public ArrayList<String> getJsonFilesToProcess(CommandLineInterpreter cli)
			throws IOException {
		ArrayList<String> jsonFilesList = new ArrayList<String>();

		if (cli.hasParameter(CommandLineInterpreter.fileInputOptionName) == true) {
			/*
			 * Single input file given
			 */
			String inputFile = cli
					.getParameter(CommandLineInterpreter.fileInputOptionName);
			jsonFilesList.add(inputFile);
		} else if (cli.hasParameter(CommandLineInterpreter.fileListOptionName) == true) {
			/*
			 * File with File paths per line given
			 */
			String jsonFileListPath = cli
					.getParameter(CommandLineInterpreter.fileListOptionName);

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(jsonFileListPath));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				jsonFilesList.add(sCurrentLine);
			}
			br.close();
		}
		return jsonFilesList;
	}

	public ArrayList<List<String>> getAttributesToCalculate(
			CommandLineInterpreter cli) {
		ArrayList<List<String>> tests = new ArrayList<List<String>>();

		ArrayList<String> attributesToUse = new ArrayList<String>(
				Arrays.asList(TARGET_FIELDS));
		String powerOption = CommandLineInterpreter.testPowerSetOptionName;
		// calculate power set if true
		if (cli.hasParameter(powerOption) == true) {
			// create new copy, with same values
			ArrayList<String> attributesToUse_Temp = new ArrayList<String>(
					attributesToUse);
			Collections.copy(attributesToUse_Temp, attributesToUse);
			attributesToUse_Temp.remove("count");

			tests = (ArrayList<List<String>>) Utils
					.calculatePowerSet(attributesToUse_Temp);
			tests.remove(0); // empty set
		} else {
			String singleAttrOption = CommandLineInterpreter.singleAttributesOptionName;
			if (cli.hasParameter(singleAttrOption)) {
				for (String string : attributesToUse) {
					ArrayList<String> temp = new ArrayList<String>();
					temp.add(string);
					tests.add(temp);
				}

			} else {
				tests.add(attributesToUse);
			}
		}

		return tests;
	}

	public void outputBasedOnParameters(CommandLineInterpreter cli,
			String csvString) throws IOException {
		if (cli.hasParameter(CommandLineInterpreter.outputFileOptionName)) {
			String outputFilePath = cli
					.getParameter(CommandLineInterpreter.outputFileOptionName);
			System.out.println("output path = " + outputFilePath);
			BufferedWriter bf = new BufferedWriter(new FileWriter(outputFilePath));
			bf.write(csvString);
			bf.flush();
			bf.close();
		} else {
			System.out.println(csvString);
		}
	}

	public String getJsonFilePath(CommandLineInterpreter cli) throws Exception {
		if (cli.hasParameter(CommandLineInterpreter.fileInputOptionName)) {
			return cli.getParameter(CommandLineInterpreter.fileInputOptionName);
		} else {
			throw new Exception("Input JSON File not provided.");
		}
	}

	public void calculateMultipleFiles(CommandLineInterpreter cli)
			throws Exception {

		CsvProcessor csvProcessor = new CsvProcessor();
		ArrayList<String> colToPrint = getColumnsToPrint(cli);
		ArrayList<String> jsonFilesList = getJsonFilesToProcess(cli);
		String csvHeaderString = csvProcessor.getCsvHeader(cli);
		String csvFile = csvHeaderString;
		ArrayList<List<String>> tests = getAttributesToCalculate(cli);

		for (List<String> list : tests) {
			list.add(0, "count");

			for (String filePath : jsonFilesList) {
				csvFile += list.size() + "; ";
				int startChar = (filePath.length() - (filePath.length() / 2));
				csvFile += getCSVLine(
						filePath.subSequence(startChar, filePath.length()).toString()
								+ list, filePath, (ArrayList<String>) list, colToPrint);
			}

		}
		outputBasedOnParameters(cli, csvFile);
	}

	public static String getCSVLine(String testName, String filePath,
			ArrayList<String> attributesToUse, ArrayList<String> colToPrint)
			throws Exception {
		WebTestJsonReader reader = new WebTestJsonReader(filePath, testName);
		String file = reader.getArffString(attributesToUse);

		HashMap<String, Double> results = StatisticsCalculator
				.calculateStatistics(testName, file);

		CsvProcessor csvProcessor = new CsvProcessor();
		csvProcessor.setDivString("; ");
		csvProcessor.setColumnsToPrint(colToPrint);
		return csvProcessor.createCsvLine(testName, results);

	}

}
