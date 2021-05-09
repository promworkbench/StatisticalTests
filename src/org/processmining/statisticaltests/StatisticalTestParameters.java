package org.processmining.statisticaltests;

public interface StatisticalTestParameters {
	public boolean isDebug();

	public int getThreads();

	public long getSeed();

	public double getAlpha();
}
