package org.processmining.statisticaltests.loglogunknownprocesstest;

import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.statisticaltests.StatisticalTestParametersDefault;

public class LogLogUnknownProcessTestParametersDefault extends LogLogUnknownProcessTestParametersAbstract {

	public static final int defaultNumberOfSamples = 10000;

	public LogLogUnknownProcessTestParametersDefault() {
		super(StatisticalTestParametersDefault.defaultDebug, StatisticalTestParametersDefault.defaultThreads,
				System.currentTimeMillis(), defaultNumberOfSamples, MiningParameters.defaultClassifier,
				MiningParameters.defaultClassifier);
	}

}
