package org.processmining.statisticaltests.logcategoricaltest;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.statisticaltests.StatisticalTestParametersDefault;

public class LogCategoricalTestParametersDefault extends LogCategoricalTestParametersAbstract {

	public static final int defaultNumberOfSamples = 10000;
	public static final int defaultSampleSize = 500;
	public static final XEventClassifier defaultClassifier = MiningParameters.defaultClassifier;

	public LogCategoricalTestParametersDefault(Attribute attribute) {
		super(StatisticalTestParametersDefault.defaultDebug, StatisticalTestParametersDefault.defaultThreads,
				System.currentTimeMillis(), StatisticalTestParametersDefault.defaultAlpha, defaultNumberOfSamples,
				defaultSampleSize, attribute, defaultClassifier);
	}

}