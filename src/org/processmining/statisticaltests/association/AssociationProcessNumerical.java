package org.processmining.statisticaltests.association;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.Levenshtein;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;

public class AssociationProcessNumerical {

	public static double[][] compute(AssociationParameters parameters, XLog log, ProMCanceller canceller)
			throws InterruptedException {
		//select traces that have the attribute
		if (parameters.isDebug()) {
			System.out.println(" select traces");
		}
		List<XTrace> traces = new ArrayList<>();
		final double maxAttributeValue;
		final double minAttributeValue;
		{
			double max = -Double.MAX_VALUE;
			double min = Double.MAX_VALUE;
			for (XTrace trace : log) {
				double value = AttributeUtils.valueDouble(parameters.getAttribute(), trace);
				if (value != -Double.MAX_VALUE && !(parameters.getAttribute().isTime() && value < 0)) {
					max = Math.max(max, value);
					min = Math.min(min, value);
					traces.add(trace);
				}
			}
			maxAttributeValue = max;
			minAttributeValue = min;
		}

		if (minAttributeValue == maxAttributeValue) {
			return null;
		}

		//perform the sampling
		if (parameters.isDebug()) {
			System.out.println(" start sampling threads");
		}
		double[][] result = new double[2][parameters.getNumberOfSamples()];

		AtomicInteger nextSampleNumber = new AtomicInteger(0);

		Thread[] threads = new Thread[7];

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

						int sampleA = random.nextInt(traces.size());
						int sampleB = random.nextInt(traces.size());

						double[] result2 = performSampleMeasure(parameters, canceller, traces, sampleA, sampleB,
								minAttributeValue, maxAttributeValue);

						if (canceller.isCancelled()) {
							return;
						}

						result[0][sampleNumber] = result2[0];
						result[1][sampleNumber] = result2[1];

						if (parameters.isDebug() && sampleNumber % 100000 == 0) {
							System.out.println(" sample " + sampleNumber + ", \\varphi=" + result[0][sampleNumber]
									+ ", \\delta=" + result[1][sampleNumber]);
						}

						sampleNumber = nextSampleNumber.getAndIncrement();
					}
				}

				private double[] performSampleMeasure(AssociationParameters parameters, ProMCanceller canceller,
						List<XTrace> traces, int sampleA, int sampleB, double minAttributeValue,
						double maxAttributeValue) {
					double valueA = (AttributeUtils.valueDouble(parameters.getAttribute(), traces.get(sampleA))
							- minAttributeValue) / (maxAttributeValue - minAttributeValue);
					double valueB = (AttributeUtils.valueDouble(parameters.getAttribute(), traces.get(sampleB))
							- minAttributeValue) / (maxAttributeValue - minAttributeValue);

					double valueDelta = Math.abs(valueA - valueB);

					String[] traceA = StatisticalTestUtils.getTraceString(traces.get(sampleA), parameters.getClassifier());
					String[] traceB = StatisticalTestUtils.getTraceString(traces.get(sampleB), parameters.getClassifier());

					double processDelta = Levenshtein.getNormalisedDistance(traceA, traceB);

					return new double[] { valueDelta, processDelta };
				}
			}, "log-numerical correlation thread " + thread);
			threads[thread].start();
		}

		//join
		for (Thread thread : threads) {
			thread.join();
		}

		return result;
	}

	
}