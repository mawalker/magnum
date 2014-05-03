package edu.vu.isis.magnum.cloudstats.stats;

import java.util.HashMap;

public class CapturedPerfPointsData implements
		Comparable<CapturedPerfPointsData> {

	public HashMap<String, String> values;
	public String timestamp;
	// TODO make changes to actually 'use' key? String/long?
	public String uniqueKey;

	public CapturedPerfPointsData() {
		values = new HashMap<String, String>();
		timestamp = "-1";
	}

	// TODO change 'key' to be used here?
	public int compareTo(CapturedPerfPointsData other) {
		return (int) (Long.valueOf(this.timestamp) - Long
				.valueOf(other.timestamp));
	}

	@Override
	public String toString() {
		String rValue = "[ uniqueKey: " + uniqueKey + ", timestamp: " + timestamp
				+ " [";

		for (String key : values.keySet()) {
			rValue += " " + key + ": " + values.get(key);
		}
		rValue += " ] ]";
		return rValue;
	}
}
