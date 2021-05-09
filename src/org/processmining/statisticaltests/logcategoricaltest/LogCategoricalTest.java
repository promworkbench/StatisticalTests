package org.processmining.statisticaltests.logcategoricaltest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;
import org.processmining.statisticaltests.StatisticalTest;
import org.processmining.statisticaltests.helperclasses.ConcurrentSamples;
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

	public double test(XLog log, final LogCategoricalTestParameters parameters, final ProMCanceller canceller)
			throws InterruptedException {
		AtomicInteger e = new AtomicInteger(0);
		AtomicInteger n = new AtomicInteger(0);

		final List<XTrace> traces = StatisticalTestUtils.filterTracesCategorical(parameters.getAttribute(), log, true);

		if (traces == null) {
			return Double.NaN;
		}

		new ConcurrentSamples<Pair<Random, DistanceCache>>(parameters.getThreads(), parameters.getNumberOfSamples(),
				canceller) {

			protected Pair<Random, DistanceCache> createThreadConstants(int threadNumber) {
				Random random = new Random(parameters.getSeed() + threadNumber);
				DistanceCache distances = new DistanceCache(parameters.getClassifier());

				return Pair.of(random, distances);
			}

			protected void performSample(Pair<Random, DistanceCache> input, int sampleNumber) {
				Random random = input.getA();
				DistanceCache distances = input.getB();

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
		};

		double p = 1 - e.get() / (1.0 * n.get());

		return p;
	}

}