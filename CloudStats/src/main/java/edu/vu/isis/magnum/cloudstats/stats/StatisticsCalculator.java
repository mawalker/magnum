package edu.vu.isis.magnum.cloudstats.stats;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.DefaultListModel;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.experiment.Experiment;

public class StatisticsCalculator {

	public static HashMap<String, Double> calculateStatistics(String testName,
			String arffString) throws Exception {
		InputStream stream = null;
		stream = new ByteArrayInputStream(arffString.getBytes("UTF-8"));

		// if (stream == null) { throw new
		// Exception("Error Processing Statistics."); }

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream,
				"UTF-8"));

		Instances data = new Instances(reader);
		data.setClassIndex(0);

		String[] options = weka.core.Utils.splitOptions("-S 0 -R 1.0E-8");

		LinearRegression model = new LinearRegression();
		model.setOptions(options);
		model.buildClassifier(data);
		model.distributionForInstance(data.firstInstance());

		/*
		 * Setup 'Experiment'
		 */
		Experiment exp = new Experiment();
		exp.setPropertyArray(new Classifier[0]);
		exp.setUsePropertyIterator(true);
		// Set to use LinearRegression

		DefaultListModel<LinearRegression> modelList = new DefaultListModel<LinearRegression>();
		modelList.addElement(model);
		exp.setDatasets(modelList);

		/*
		 * Perform Evaluation
		 */
		Evaluation evaluationTest = new Evaluation(data);
		evaluationTest.evaluateModel(model, data);

		HashMap<String, Double> results = getResults(evaluationTest);
		return results;
	}

	public static HashMap<String, Double> getResults(Evaluation results)
			throws Exception {
		HashMap<String, Double> rValue = new HashMap<String, Double>();

		rValue.put("correlationCoefficient", results.correlationCoefficient());
		rValue.put("meanAbsoluteError", results.meanAbsoluteError());
		rValue.put("errorRate", results.errorRate());
		rValue.put("relativeAbsoluteError", results.relativeAbsoluteError());
		rValue.put("rootRelativeSquaredError", results.rootRelativeSquaredError());
		rValue.put("avgCost", results.avgCost());
		rValue.put("correct", results.correct());
		rValue.put("incorrect", results.incorrect());

		return rValue;

	}

}
