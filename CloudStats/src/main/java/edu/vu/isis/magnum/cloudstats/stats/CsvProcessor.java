package edu.vu.isis.magnum.cloudstats.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import edu.vu.isis.magnum.cloudstats.CommandLineInterpreter;

public class CsvProcessor {

	private String EOL = "\n";
	private String DIV = ",";

	public String createCsvLine(String testName, HashMap<String, Double> results)
			throws Exception {
		String rValue = "";

		Iterator<String> iter = columnsToPrint.iterator();

		String csvDataLine = testName + DIV;

		while (iter.hasNext()) {
			csvDataLine += results.get(iter.next());
			if (iter.hasNext()) {
				csvDataLine += DIV;
			}
		}

		rValue = csvDataLine + EOL;

		return rValue;
	}

	public void setEolString(String value) {
		EOL = value;
	}

	public void setDivString(String value) {
		DIV = value;
	}

	public final static String[] COLUMNS_TO_PRINT = { "correlationCoefficient",
			"meanAbsoluteError", "errorRate", "relativeAbsoluteError",
			"rootRelativeSquaredError" };

	// set default
	ArrayList<String> columnsToPrint = new ArrayList<String>(
			Arrays.asList(COLUMNS_TO_PRINT));

	public void setColumnsToPrint(ArrayList<String> columnsToPrint) {
		this.columnsToPrint = columnsToPrint;
	}

	public String getCsvHeader(CommandLineInterpreter cli) {
		ArrayList<String> colToPrint = columnsToPrint;

		String csvFile = "NumVariables; TestName; ";

		Iterator<String> colIter = colToPrint.iterator();

		while (colIter.hasNext()) {
			csvFile += colIter.next();
			if (colIter.hasNext()) {
				csvFile += "; ";
			} else {
				csvFile += "\n";
			}
		}
		return csvFile;
	}

}
