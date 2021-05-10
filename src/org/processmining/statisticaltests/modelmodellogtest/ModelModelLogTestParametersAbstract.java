package org.processmining.statisticaltests.modelmodellogtest;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.statisticaltests.StatisticalTestParametersAbstract;

public class ModelModelLogTestParametersAbstract extends StatisticalTestParametersAbstract
		implements ModelModelLogTestParameters {

	private int numberOfSamples;
	private int sampleSize;
	private XEventClassifier classifier;

	public ModelModelLogTestParametersAbstract(boolean debug, int threads, long seed, double alpha, int numberOfSamples,
			int sampleSize, XEventClassifier classifier) {
		super(debug, threads, seed, alpha);
		this.numberOfSamples = numberOfSamples;
		this.sampleSize = sampleSize;
		this.classifier = classifier;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

}
