package org.processmining.statisticaltests;

import org.deckfour.xes.classification.XEventClassifier;

public abstract class ParametersAbstract implements Parameters {

	private int sampleSize;
	private int numberOfSamples;
	private long seed;
	private XEventClassifier classifierA;
	private XEventClassifier classifierB;

	public ParametersAbstract(int sampleSize, int numberOfSamples, long seed, XEventClassifier classifierA,
			XEventClassifier classifierB) {
		this.sampleSize = sampleSize;
		this.numberOfSamples = numberOfSamples;
		this.seed = seed;
		this.classifierA = classifierA;
		this.classifierB = classifierB;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public XEventClassifier getClassifierA() {
		return classifierA;
	}

	public void setClassifierA(XEventClassifier classifierA) {
		this.classifierA = classifierA;
	}

	public XEventClassifier getClassifierB() {
		return classifierB;
	}

	public void setClassifierB(XEventClassifier classifierB) {
		this.classifierB = classifierB;
	}

}
