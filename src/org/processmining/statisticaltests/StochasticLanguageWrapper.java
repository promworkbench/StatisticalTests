package org.processmining.statisticaltests;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;

public class StochasticLanguageWrapper implements StochasticLanguage<TotalOrder> {

	private final StochasticLanguage<TotalOrder> superLanguage;
	private double[] newProbabilities;

	public StochasticLanguageWrapper(StochasticLanguage<TotalOrder> superLanguage, double[] newProbabilities) {
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

	public StochasticTraceIterator<TotalOrder> iterator() {
		final StochasticTraceIterator<TotalOrder> superIterator = superLanguage.iterator();
		return new StochasticTraceIterator<TotalOrder>() {
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