package org.processmining.correlation;

import java.util.Random;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public interface CorrelationParameters {
	public int getNumberOfSamples();

	public int getSampleSize();

	public XEventClassifier getClassifier();

	public Attribute getAttribute();

	public Random getRandom();
}
