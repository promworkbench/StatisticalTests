package org.processmining.statisticaltests.association;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class CorrelationParametersDefault extends CorrelationParametersAbstract {

	public static final int defaultNumberOfSamples = 100000;
	public static final XEventClassifier defaultClassifier = new XEventNameClassifier();
	public static final boolean defaultDebug = false;
	public static final int defaultThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

	public CorrelationParametersDefault(Attribute attribute) {
		super(defaultNumberOfSamples, defaultClassifier, attribute, System.currentTimeMillis(), defaultDebug,
				defaultThreads);
	}
}
