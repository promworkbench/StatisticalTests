package org.processmining.statisticaltests.loglogunknownprocesstest;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.statisticaltests.StatisticalTestParameters;

public interface LogLogUnknownProcessTestParameters extends StatisticalTestParameters {
	public int getNumberOfSamples();

	public XEventClassifier getClassifierA();

	public XEventClassifier getClassifierB();
}
