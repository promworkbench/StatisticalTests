package org.processmining.statisticaltests.modelmodellogtest;

import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.statisticaltests.StatisticalTestParametersDefault;

public class ModelModelLogTestParametersDefault extends ModelModelLogTestParametersAbstract {

	public static final int defaultNumberOfSamples = 500;
	public static final int defaultSampleSize = 500;

	public ModelModelLogTestParametersDefault() {
		super(StatisticalTestParametersDefault.defaultDebug, StatisticalTestParametersDefault.defaultThreads,
				System.currentTimeMillis(), StatisticalTestParametersDefault.defaultAlpha, defaultNumberOfSamples,
				defaultSampleSize, MiningParameters.defaultClassifier);
	}

}
