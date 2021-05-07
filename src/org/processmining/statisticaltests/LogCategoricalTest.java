package org.processmining.statisticaltests;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;

public class LogCategoricalTest {

	/**
	 * 
	 * @param p
	 * @param alpha
	 * @return true: reject null-hypothesis that at least one categorical value
	 *         associates with a different process
	 */
	public static boolean rejectHypothesisForSingleTest(double p, double alpha) {
		return p < alpha;
	}

	public static double test(XLog log, final LogCategoricalTestParameters parameters, final ProMCanceller canceller) {
		AtomicInteger e = new AtomicInteger(0);
		AtomicInteger n = new AtomicInteger(0);

		final List<XTrace> traces = StatisticalTestUtils.filterTracesCategorical(parameters.getAttribute(), log, true);

		if (traces == null) {
			return Double.NaN;
		}

		final AtomicInteger nextSampleNumber = new AtomicInteger(0);

		Thread[] threads = new Thread[parameters.getThreads()];

		for (int thread = 0; thread < threads.length; thread++) {
			final int thread2 = thread;
			threads[thread] = new Thread(new Runnable() {
				public void run() {

					Random random = new Random(parameters.getSeed() + thread2);

					int sampleNumber = nextSampleNumber.getAndIncrement();
					while (sampleNumber < parameters.getNumberOfSamples()) {

						if (canceller.isCancelled()) {
							return;
						}

						int[] sample = StatisticalTestUtils.getSample(traces,
								Math.max(parameters.getSampleSize(), traces.size()), random);

						for (int i = 0; i < sample.length; i++) {
							for (int j = i + 1; j < sample.length; j++) {

							}
						}
					}
				}
			});
		}

		double p = 1 - e.get() / (1.0 * n.get());

		return p;
	}

}
