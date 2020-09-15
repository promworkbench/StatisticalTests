package org.processmining.statisticaltests;

import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;

public class StochasticLanguageWrapper implements StochasticLanguage {

	private final StochasticLanguage superLanguage;
	private double[] newProbabilities;

	public StochasticLanguageWrapper(StochasticLanguage superLanguage, double[] newProbabilities) {
		this.superLanguage = superLanguage;
		this.newProbabilities = newProbabilities;
	}

	public int size() {
		return superLanguage.size();
	}

	public String[] getTraceString(int traceIndex) {
		return superLanguage.getTraceString(traceIndex);
	}

	public int[] getTrace(int traceIndex) {
		return superLanguage.getTrace(traceIndex);
	}

	public StochasticTraceIterator iterator() {
		final StochasticTraceIterator superIterator = superLanguage.iterator();
		return new StochasticTraceIterator() {
			int i = -1;

			public boolean hasNext() {
				return superIterator.hasNext();
			}

			public int[] nextIntegerTrace() {
				i++;
				return superIterator.nextIntegerTrace();
			}

			public String[] next() {
				i++;
				return superIterator.next();
			}

			public double getProbability() {
				return newProbabilities[i];
			}
		};
	}

	public Activity2IndexKey getActivityKey() {
		return superLanguage.getActivityKey();
	}

}