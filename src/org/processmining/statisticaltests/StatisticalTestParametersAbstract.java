package org.processmining.statisticaltests;

public class StatisticalTestParametersAbstract implements StatisticalTestParameters {

	private boolean debug;
	private int threads;
	private long seed;

	public StatisticalTestParametersAbstract(boolean debug, int threads, long seed) {
		assert threads > 0;
		this.debug = debug;
		this.threads = threads;
		this.seed = seed;
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
		this.threads = threads;
	}

	@Override
	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

}