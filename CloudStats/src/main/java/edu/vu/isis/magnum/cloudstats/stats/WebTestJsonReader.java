package edu.vu.isis.magnum.cloudstats.stats;

import java.util.ArrayList;
import java.util.Arrays;

public class WebTestJsonReader {

	public static final boolean SINGLE_FIELD = Boolean.valueOf("true");
	final static boolean hasTimestampToRemove = Boolean.getBoolean("false");

	private static final String targetAttr = "count";
	private static final String[] TARGET_FIELDS = { "cpuIdle", "memory",
			"networkEth0", "cpu", "mean" };

	private static final String[] PERFORMANCE_FIELDS = { "cpuIdle", "memory",
			"networkEth0", "cpu" };
	private static final String[] THROUGHPUT_FIELDS = { "count", "mean" };

	ArffFileData fileData;

	private static final String timestampString = "timestamp";

	public WebTestJsonReader(String file, String testName) {

		fileData = new ArffFileData(testName);

		ArrayList<CapturedPerfPointsData> data = CustomJsonReader.getData(file);

		fileData.addNumericaAttribute(targetAttr);
		for (String attributeName : TARGET_FIELDS) {
			fileData.addNumericaAttribute(attributeName);
		}

		for (CapturedPerfPointsData rowData : data) {
			fileData.addDataRow(rowData.values);
		}

		fileData.removeParameterFromData(timestampString);

		fileData.convertCPUIdleToCPUUtilization();

	}

	public String getArffString(ArrayList<String> attributesToUse) {
		return fileData.getOutputFileString(attributesToUse);
	}

}
