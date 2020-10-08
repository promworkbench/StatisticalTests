package org.processmining.statisticaltests;

import org.deckfour.xes.classification.XEventClassifier;

public interface Parameters {
	public int getSampleSize();

	public int getNumberOfReSamples();

	public long getSeed();

	public XEventClassifier getClassifierA();

	public XEventClassifier getClassifierB();
}