package org.processmining.correlation;

import java.util.Random;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public abstract class CorrelationParametersAbstract implements CorrelationParameters {

	private int numberOfSamples;
	private int sampleSize;
	private XEventClassifier classifier;
	private Attribute attribute;
	private Random random;

	public CorrelationParametersAbstract(int numberOfSamples, int sampleSize, XEventClassifier classifier,
			Attribute attribute, Random random) {
		this.numberOfSamples = numberOfSamples;
		this.sampleSize = sampleSize;
		this.classifier = classifier;
		this.attribute = attribute;
		this.random = random;
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

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

}
