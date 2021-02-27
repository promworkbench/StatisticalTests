package org.processmining.correlation;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public interface CorrelationParameters {
	public int getNumberOfSamples();

	public XEventClassifier getClassifier();

	public Attribute getAttribute();

	public long getSeed();

	public boolean isDebug();
	
	public int getThreads();
}
