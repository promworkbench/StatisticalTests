package org.processmining.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.Levenshtein;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;

public class CorrelationProcessNumerical {

	public static double[][] compute(CorrelationParameters parameters, XLog log, ProMCanceller canceller)
			throws InterruptedException {
		//select traces that have the attribute
		if (parameters.isDebug()) {
			System.out.println(" select traces");
		}
		List<XTrace> traces = new ArrayList<>();
		final double maxAttributeValue;
		{
			double max = -Double.MAX_VALUE;
			for (XTrace trace : log) {
				double value = AttributeUtils.valueDouble(parameters.getAttribute(), trace);
				if (value != -Double.MAX_VALUE) {
					max = Math.max(max, value);
					traces.add(trace);
				}
			}
			maxAttributeValue = max;
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
								maxAttributeValue);

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

				private double[] performSampleMeasure(CorrelationParameters parameters, ProMCanceller canceller,
						List<XTrace> traces, int sampleA, int sampleB, double maxAttributeValue) {
					double valueA = AttributeUtils.valueDouble(parameters.getAttribute(), traces.get(sampleA))
							/ maxAttributeValue;
					double valueB = AttributeUtils.valueDouble(parameters.getAttribute(), traces.get(sampleB))
							/ maxAttributeValue;

					double valueDelta = Math.abs(valueA - valueB);

					String[] traceA = getTraceString(traces.get(sampleA), parameters.getClassifier());
					String[] traceB = getTraceString(traces.get(sampleB), parameters.getClassifier());

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

	private static String[] getTraceString(XTrace xTrace, XEventClassifier classifier) {
		String[] result = new String[xTrace.size()];
		for (int i = 0; i < xTrace.size(); i++) {
			XEvent event = xTrace.get(i);
			result[i] = classifier.getClassIdentity(event);
		}
		return result;
	}
}