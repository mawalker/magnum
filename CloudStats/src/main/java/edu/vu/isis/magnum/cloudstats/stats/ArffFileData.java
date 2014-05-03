package edu.vu.isis.magnum.cloudstats.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ArffFileData {

	private String relationName;
	private HashMap<String, String> attributeTypes;
	private ArrayList<String> attributes;

	private ArrayList<HashMap<String, String>> data;

	public ArffFileData(String relationName) {
		this.relationName = relationName;
		attributes = new ArrayList<String>();
		attributeTypes = new HashMap<String, String>();
		data = new ArrayList<HashMap<String, String>>();
	}

	public void addDataRow(HashMap<String, String> row) {
		data.add(row);
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public ArrayList<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}

	public ArrayList<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(ArrayList<HashMap<String, String>> data) {
		this.data = data;
	}

	public void addAttribute(String name, String type) {
		attributeTypes.put(name, type);
	}

	public void addNumericaAttribute(String name) {
		attributes.add(name);
		attributeTypes.put(name, "numeric");
	}

	private String endl = "\n";

	public void SetUnixLineEndings() {
		endl = "\n";
	}

	public void SetMSLineEndings() {
		endl = "\r\n";
	}

	/**
	 * Get the String of the ARFF file format for this data
	 * 
	 * @return String of file
	 */
	public String getOutputFileString(ArrayList<String> attributesToUse) {
		String rValue = "";

		// give relation name
		rValue += "@relation '" + relationName + "'" + endl;

		for (String attr : attributesToUse) {
			rValue += "@attribute " + attr + " numeric" + endl;
		}

		rValue += "@data" + endl;

		for (HashMap<String, String> row : data) {
			rValue += getOutputFileDataLine(row, attributesToUse) + endl;
		}
		return rValue;
	}

	/**
	 * Get a row of data as a CSV String
	 * 
	 * @param row
	 *           data to convert
	 * @param attributes
	 *           determines order of parameters
	 * @return String CSV of a row of data
	 */
	public String getOutputFileDataLine(HashMap<String, String> row,
			ArrayList<String> attributes) {
		String rValue = "";

		Iterator<String> it = (Iterator<String>) attributes.iterator();

		while (it.hasNext()) {
			String key = it.next();
			rValue += row.get(key);
			if (it.hasNext() == true) {
				rValue += ", ";
			}
		}

		return rValue;
	}

	/**
	 * Remove a parameter from this data, both from attribute list, and 'column'
	 * of data
	 * 
	 * @param parameterToRemove
	 */
	public void removeParameterFromData(String parameterToRemove) {
		if (attributes.contains(parameterToRemove) == true) {
			attributes.remove(parameterToRemove);
			attributeTypes.remove(parameterToRemove);
			for (HashMap<String, String> row : data) {
				row.remove(parameterToRemove);
			}
		}
	}

	/**
	 * Conversion of 'cpuIdle' to 'cpuUsage'
	 * 
	 * @param attributes
	 *           String List of attributes
	 * @param rows
	 *           rows to alter
	 */
	public void convertCPUIdleToCPUUtilization() {

		int index = attributes.indexOf("cpuIdle");
		if (index != -1) {
			attributes.add(index, "cpuUsage");
			attributes.remove("cpuIdle");

			Iterator<HashMap<String, String>> iter = data.iterator();

			while (iter.hasNext()) {
				HashMap<String, String> row = iter.next();
				String temp = row.get("cpuIdle");
				String newValue = "" + (100.0 - Double.valueOf(temp));
				row.remove("cpuIdle");
				row.put("cpuUsage", newValue);
			}
		}
	}

}
