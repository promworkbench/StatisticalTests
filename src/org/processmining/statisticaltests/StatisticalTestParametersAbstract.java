package org.processmining.statisticaltests;

public class StatisticalTestParametersAbstract implements StatisticalTestParameters {

	private boolean debug;
	private int threads;
	private long seed;
	private double alpha;

	public StatisticalTestParametersAbstract(boolean debug, int threads, long seed, double alpha) {
		assert threads > 0;

		this.debug = debug;
		this.threads = threads;
		this.seed = seed;
		this.alpha = alpha;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		assert threads > 0;
		this.threads = threads;
	}

	@Override
	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	@Override
	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

}