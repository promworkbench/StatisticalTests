package org.processmining.statisticaltests.association;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.Levenshtein;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;

public class AssociationProcessCategorical {

	/**
	 * 
	 * @param parameters
	 * @param log
	 * @param canceller
	 * @return the sample data, or null if it does not exist
	 * @throws InterruptedException
	 */
	public static double[][] compute(Attribute attribute, AssociationsParameters parameters, XLog log,
			ProMCanceller canceller) throws InterruptedException {
		//select traces that have the attribute
		if (parameters.isDebug()) {
			System.out.println(" select traces for attribute " + attribute);
		}
		final List<XTrace> traces = StatisticalTestUtils.filterTracesCategorical(attribute, log, parameters.isDebug());
		if (traces == null) {
			return null;
		}

		//setup result
		final double[] as = new double[parameters.getNumberOfSamples()];
		final double[] rs = new double[parameters.getNumberOfSamples()];

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
								Math.min(parameters.getSampleSize(), traces.size()), random);

						Pair<BigDecimal, BigDecimal> result = processSample(traces, sample, parameters.getClassifier(),
								attribute);
						if (result == null) {
							as[sampleNumber] = Double.NaN;
							rs[sampleNumber] = Double.NaN;
						} else {
							as[sampleNumber] = result.getA().doubleValue();
							rs[sampleNumber] = result.getB().doubleValue();
						}

						if (parameters.isDebug() && sampleNumber % 100 == 0) {
							System.out.println(" sample " + sampleNumber);
						}

						sampleNumber = nextSampleNumber.getAndIncrement();
					}
				}
			}, "log-numerical correlation thread " + thread);
			threads[thread].start();
		}

		//join
		for (Thread thread : threads) {
			thread.join();
		}

		return new double[][] { as, rs };
	}

	/**
	 * 
	 * @param traces
	 * @param sample
	 * @param classifier
	 * @param attribute
	 * @return (average trace distance with knowledge, average trace distance
	 *         without knowledge)
	 */
	public static Pair<BigDecimal, BigDecimal> processSample(List<XTrace> traces, int[] sample,
			XEventClassifier classifier, Attribute attribute) {
		BigDecimal sumA = BigDecimal.ZERO;
		int countA = 0;
		BigDecimal sumR = BigDecimal.ZERO;
		int countR = 0;
		for (int i = 0; i < sample.length; i++) {
			String[] traceI = StatisticalTestUtils.getTraceString(traces.get(sample[i]), classifier);
			String valueI = AttributeUtils.valueString(attribute, traces.get(sample[i]));
			for (int j = i + 1; j < sample.length; j++) {
				String[] traceJ = StatisticalTestUtils.getTraceString(traces.get(sample[j]), classifier);
				String valueJ = AttributeUtils.valueString(attribute, traces.get(sample[j]));
				BigDecimal x = BigDecimal.valueOf(Levenshtein.getNormalisedDistance(traceI, traceJ));

				if (valueI.equals(valueJ)) {
					sumA = sumA.add(x);
					countA++;
				}
				sumR = sumR.add(x);
				countR++;
			}
		}

		if (countA == 0) {
			return null;
		}

		BigDecimal a = sumA.divide(BigDecimal.valueOf(countA), 10, RoundingMode.HALF_UP);
		BigDecimal r = sumR.divide(BigDecimal.valueOf(countR), 10, RoundingMode.HALF_UP);

		return Pair.of(a, r);
	}
}
