package org.processmining.statisticaltests;

public interface LogCategoricalTestParameters extends CategoricalComparisonParameters {
	public long getSeed();

	public int getNumberOfSamples();

	public int getSampleSize();

	public int getThreads();
}
