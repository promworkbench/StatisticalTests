package org.processmining.statisticaltests;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public interface LogCategoricalTestParameters extends StatisticalTestParameters {

	public int getNumberOfSamples();

	public int getSampleSize();

	public Attribute getAttribute();

	public XEventClassifier getClassifier();

}