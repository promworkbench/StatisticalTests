package org.processmining.statisticaltests;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public interface CategoricalComparisonParameters {
	public XEventClassifier getClassifier();

	public Attribute getAttribute();

	public double getAlpha();
}
