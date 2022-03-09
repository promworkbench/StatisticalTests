package org.processmining.statisticaltests.modelmodellogtest;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.statisticaltests.StatisticalTestParameters;

public interface ModelModelLogTestParameters extends StatisticalTestParameters {
	public int getNumberOfSamples();

	public int getSampleSize();

	public XEventClassifier getClassifier();

	public static enum StochasticLanguageDistanceMeasure {
		EMSC, uEMSC
	}

	public StochasticLanguageDistanceMeasure getLanguageDistance();
}
