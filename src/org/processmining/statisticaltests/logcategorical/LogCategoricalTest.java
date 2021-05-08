package org.processmining.statisticaltests.logcategorical;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;
import org.processmining.statisticaltests.StatisticalTest;
import org.processmining.statisticaltests.helperclasses.DistanceCache;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;

public class LogCategoricalTest implements StatisticalTest<XLog, LogCategoricalTestParameters> {

	/**
	 * 
	 * @param p
	 * @param alpha
	 * @return true: reject null-hypothesis that at least one categorical value
	 *         associates with a different process
	 */
	public boolean rejectHypothesisForSingleTest(double p, double alpha) {
		return p < alpha;
	}

	public double test(XLog log, final LogCategoricalTestParameters parameters, final ProMCanceller canceller) {
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
					DistanceCache distances = new DistanceCache(parameters.getClassifier());

					int sampleNumber = nextSampleNumber.getAndIncrement();
					while (sampleNumber < parameters.getNumberOfSamples()) {

						if (canceller.isCancelled()) {
							return;
						}

						int[] sample = StatisticalTestUtils.getSample(traces,
								Math.max(parameters.getSampleSize(), traces.size()), random);

						//initialise result variables
						int countA = 0;
						BigDecimal sumA = BigDecimal.ZERO;
						int countR = 0;
						BigDecimal sumR = BigDecimal.ZERO;

						//perform sample
						for (int i = 0; i < sample.length; i++) {
							for (int j = i + 1; j < sample.length; j++) {
								int indexA = sample[i];
								int indexB = sample[j];

								XTrace traceA = traces.get(indexA);
								XTrace traceB = traces.get(indexB);

								BigDecimal distance = BigDecimal.valueOf(distances.get(indexA, traceA, indexB, traceB));

								//keep track of average over all traces
								countR++;
								sumR = sumR.add(distance);

								//keep track of average over traces with equal attribute
								String valueA = AttributeUtils.valueString(parameters.getAttribute(), traceA);
								String valueB = AttributeUtils.valueString(parameters.getAttribute(), traceB);

								if (valueA.equals(valueB)) {
									sumA = sumA.add(distance);
									countA++;
								}

							}
						}

						if (countA > 0) {
							n.incrementAndGet();
							if (sumA.doubleValue() / countA < sumR.doubleValue() / countR) {
								e.incrementAndGet();
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