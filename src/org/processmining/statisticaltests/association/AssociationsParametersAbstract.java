package org.processmining.statisticaltests.association;

import org.deckfour.xes.classification.XEventClassifier;

public class AssociationsParametersAbstract implements AssociationsParameters {

	private boolean debug;
	private XEventClassifier classifier;
	private long seed;
	private int threads;
	private int numberOfSamples;
	private int sampleSize;
	private CorrelationPlot correlationPlot;

	public AssociationsParametersAbstract(XEventClassifier classifier, int numberOfSamples, int sampleSize,
			boolean debug, long seed, int threads, CorrelationPlot correlationPlot) {
		this.classifier = classifier;
		this.setCorrelationPlot(correlationPlot);
		this.setSampleSize(sampleSize);
		this.setNumberOfSamples(numberOfSamples);
		this.setThreads(threads);
		this.setSeed(seed);
		this.setDebug(debug);
	}

	@Override
	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	@Override
	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	@Override
	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	@Override
	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	@Override
	public CorrelationPlot getCorrelationPlot() {
		return correlationPlot;
	}

	public void setCorrelationPlot(CorrelationPlot correlationPlot) {
		this.correlationPlot = correlationPlot;
	}

}
