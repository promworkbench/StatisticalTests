package org.processmining.statisticaltests.association;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

public class AssociationsParametersDefault extends AssociationsParametersAbstract {

	public static final boolean defaultDebug = false;
	public static final XEventClassifier defaultClassifier = new XEventNameClassifier();
	public final static int defaultNumberOfSamples = 500;
	public final static int defaultSampleSize = 1000;
	public static final int defaultThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

	public AssociationsParametersDefault() {
		super(defaultClassifier, defaultNumberOfSamples, defaultSampleSize, defaultDebug, System.currentTimeMillis(),
				defaultThreads, new CorrelationPlot());
	}

}
