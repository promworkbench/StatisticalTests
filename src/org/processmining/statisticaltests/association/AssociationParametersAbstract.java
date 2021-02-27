package org.processmining.statisticaltests.association;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public abstract class AssociationParametersAbstract implements AssociationParameters {

	private int numberOfSamples;
	private XEventClassifier classifier;
	private Attribute attribute;
	private long seed;
	private boolean debug;
	private int threads;

	public AssociationParametersAbstract(int numberOfSamples, XEventClassifier classifier, Attribute attribute,
			long seed, boolean debug, int threads) {
		this.numberOfSamples = numberOfSamples;
		this.classifier = classifier;
		this.attribute = attribute;
		this.seed = seed;
		this.debug = debug;
		this.threads = threads;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
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

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

}
