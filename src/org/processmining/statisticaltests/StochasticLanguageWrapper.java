package org.processmining.statisticaltests;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.log.StochasticLanguageLog;

public class StochasticLanguageWrapper extends StochasticLanguageLog {

	private final StochasticLanguageLog superLanguage;
	private double[] newProbabilities;

	public StochasticLanguageWrapper(StochasticLanguageLog superLanguage, double[] newProbabilities) {
		super(superLanguage.getActivityKey());
		this.superLanguage = superLanguage;
		this.newProbabilities = newProbabilities;
	}

	public int size() {
		return superLanguage.size();
	}

	public String getTraceString(int traceIndex) {
		return superLanguage.getTraceString(traceIndex);
	}

	public int[] getTrace(int traceIndex) {
		return superLanguage.getTrace(traceIndex);
	}

	public StochasticTraceIterator<int[]> iterator() {
		final StochasticTraceIterator<int[]> superIterator = superLanguage.iterator();
		return new StochasticTraceIterator<int[]>() {
			int i = -1;

			@Override
			public boolean hasNext() {
				return superIterator.hasNext();
			}

			@Override
			public int[] next() {
				i++;
				return superIterator.next();
			}

			@Override
			public double getProbability() {
				return newProbabilities[i];
			}

			@Override
			public int getTraceIndex() {
				return i;
			}
		};
	}

	public Activity2IndexKey getActivityKey() {
		return superLanguage.getActivityKey();
	}

}