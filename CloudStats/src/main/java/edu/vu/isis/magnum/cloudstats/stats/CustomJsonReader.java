package edu.vu.isis.magnum.cloudstats.stats;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.apache.wink.json4j.OrderedJSONObject;

public class CustomJsonReader {

	public static final boolean SINGLE_FIELD = Boolean.valueOf("true");
	final static boolean hasTimestampToRemove = Boolean.getBoolean("false");

	private static final String targetAttr = "count";
	private static final String[] TARGET_FIELDS = { "cpuIdle", "memory",
			"networkEth0", "cpu", "mean" };

	private static final String[] PERFORMANCE_FIELDS = { "cpuIdle", "memory",
			"networkEth0", "cpu" };
	private static final String[] THROUGHPUT_FIELDS = { "count", "mean" };

	private static final String parentDir = "/home/rangerz/Documents/vandy/magnum/data-2013-04-24/";
	private static final String[] directories = { "bm-9-tornade", "bm-2-go",
			"bm-3-nodejs", "bm-7-netty", "bm-1-jetty-servlet", };

	private static final String[] files = {

	"c1.medium-throughput-perm-processed.json",
			"c1.xlarge-throughput-perm-processed.json",
			"c3.large-throughput-perm-processed.json",
			"c3.xlarge-throughput-perm-processed.json",
			"m3.xlarge-throughput-perm-processed.json" };

	int counterDone = 0;

	// public
	// String getARFFString(ArrayList<CapturedPerfPointsData> data,
	// ArrayList<String> otherAttr,
	// String targetAttr,
	// String testName)
	// throws IOException {
	//
	// ArrayList<String> attributes = new ArrayList<String>();
	// attributes.add(targetAttr);
	// attributes.addAll(otherAttr);
	//
	// return getOutputFileString(testName, attributes, data);
	//
	// }

	// public
	// String getOutputFileString(String testName,
	// ArrayList<String> attributes,
	// ArrayList<CapturedPerfPointsData> rows) {
	// // fix CPU-Idle to CPU-usage-%
	// convertCPUIdleToCPUUtilization(attributes, rows);
	//
	// // declare newline character(s)
	// String endl = "\n";
	//
	// // give relation name
	// String rValue = "@relation '" + testName + "'" + "\n";
	//
	// if (hasTimestampToRemove == false) {
	// attributes.remove("timestamp");
	// }
	//
	// // list attributes
	// for (String attr : attributes) {
	// rValue += "@attribute " + attr + " numeric" + endl;
	// }
	//
	// rValue += "@data" + endl;
	//
	// for (CapturedPerfPointsData capturedPerfPointsData : rows) {
	// rValue += getOutputFileDataLine(capturedPerfPointsData, attributes) +
	// endl;
	// }
	//
	// return rValue;
	// }

	public void convertCPUIdleToCPUUtilization(ArrayList<String> attributes,
			ArrayList<CapturedPerfPointsData> rows) {

		int index = attributes.indexOf("cpuIdle");
		if (index != -1) {
			attributes.add(index, "cpuUsage");
			attributes.remove("cpuIdle");

			for (CapturedPerfPointsData data : rows) {
				String temp = data.values.get("cpuIdle");
				data.values.remove("cpuIdle");
				data.values.put("cpuUsage", "" + (100.0 - Double.valueOf(temp)));
			}
		}
	}

	public String getOutputFileDataLine(CapturedPerfPointsData data,
			ArrayList<String> attributes) {
		@SuppressWarnings("unchecked")
		ArrayList<String> attributeList = (ArrayList<String>) attributes.clone();
		String value = "";
		if (hasTimestampToRemove == true) {
			attributeList.remove("timestamp");
			value = data.timestamp + ",";
		}
		Iterator<String> it = (Iterator<String>) attributeList.iterator();
		while (it.hasNext()) {
			String key = it.next();
			value += data.values.get(key);
			if (it.hasNext() == true) {
				value += ",";
			}
		}
		return value;
	}

	public static ArrayList<CapturedPerfPointsData> getData(String fileLocation) {
		ArrayList<CapturedPerfPointsData> rValue = new ArrayList<CapturedPerfPointsData>();

		try {
			OrderedJSONObject ordered = new OrderedJSONObject(new FileReader(
					fileLocation));

			OrderedJSONObject throughputPoints = (OrderedJSONObject) ordered
					.get("capturedThroughputPoints");
			Iterator<Object> throughputDataIter = throughputPoints.keys();
			/*
			 */

			OrderedJSONObject perfPoints = (OrderedJSONObject) ordered
					.get("capturedPerfPoints");
			Iterator<Object> tempIter = perfPoints.keys();
			String tempKey = (String) tempIter.next();
			JSONObject perfData = (JSONObject) perfPoints.get(tempKey);
			Iterator<Object> perfDataIter = perfData.keys();

			/*
			 */

			ArrayList<CapturedPerfPointsData> performanceData = new ArrayList<CapturedPerfPointsData>();
			ArrayList<CapturedPerfPointsData> throughputData = new ArrayList<CapturedPerfPointsData>();

			while (throughputDataIter.hasNext() && perfDataIter.hasNext()) {

				CapturedPerfPointsData performanceTestData = new CapturedPerfPointsData();
				CapturedPerfPointsData throughputTestData = new CapturedPerfPointsData();

				String nextPerfData = (String) perfDataIter.next();
				String nextThghData = (String) throughputDataIter.next();

				// System.out.println("*** perf: " + nextPerfData
				// + "  througput: " + nextThghData);

				JSONObject dataForPerf = (JSONObject) perfData.get(nextPerfData);
				// System.out.println(data);
				performanceTestData.timestamp = nextPerfData;

				for (String field : PERFORMANCE_FIELDS) {
					performanceTestData.values.put(field, dataForPerf.get(field)
							.toString());
				}
				performanceData.add(performanceTestData);

				JSONObject dataForThroughput = (JSONObject) throughputPoints
						.get(nextThghData);
				// System.out.println(data);
				throughputTestData.timestamp = nextThghData;

				for (String field : THROUGHPUT_FIELDS) {
					throughputTestData.values.put(field, dataForThroughput
							.get(field).toString());
				}
				throughputData.add(throughputTestData);

			}

			Collections.sort(performanceData);
			Collections.sort(throughputData);

			for (int i = 0; i < 10; i++) {
				CapturedPerfPointsData temp = new CapturedPerfPointsData();
				CapturedPerfPointsData preformance = performanceData.get(i);
				CapturedPerfPointsData throughput = throughputData.get(i);
				temp.timestamp = preformance.timestamp;
				for (String key : preformance.values.keySet()) {
					temp.values.put(key, preformance.values.get(key));
				}
				for (String key : throughput.values.keySet()) {
					temp.values.put(key, throughput.values.get(key));
				}
				rValue.add(temp);
			}

		} catch (FileNotFoundException e) {
		} catch (JSONException e) {
		}
		return rValue;
	}
}
