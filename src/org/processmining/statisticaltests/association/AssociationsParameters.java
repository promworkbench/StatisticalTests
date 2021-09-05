package org.processmining.statisticaltests.association;

import org.deckfour.xes.classification.XEventClassifier;

public interface AssociationsParameters {

	public boolean isDebug();

	public XEventClassifier getClassifier();

	public int getThreads();

	public int getNumberOfSamples();

	public int getSampleSize();

	public long getSeed();

}