package org.processmining.statisticaltests.logcategorical;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.StatisticalTestParametersAbstract;

public class LogCategoricalTestParametersAbstract extends StatisticalTestParametersAbstract
		implements LogCategoricalTestParameters {

	private int numberOfSamples;
	private int sampleSize;
	private Attribute attribute;
	private XEventClassifier classifier;

	public LogCategoricalTestParametersAbstract(boolean debug, int threads, long seed, int numberOfSamples,
			int sampleSize, Attribute attribute, XEventClassifier classifier) {
		super(debug, threads, seed);
		this.numberOfSamples = numberOfSamples;
		this.sampleSize = sampleSize;
		this.attribute = attribute;
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

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}
}
