package org.processmining.statisticaltests.association;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.Levenshtein;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;

import gnu.trove.set.hash.THashSet;

public class AssociationProcessCategorical {

	public static double[] compute(AssociationParametersCategorical parameters, XLog log, ProMCanceller canceller)
			throws InterruptedException {
		//select traces that have the attribute
		if (parameters.isDebug()) {
			System.out.println(" select traces");
		}
		final List<XTrace> traces = new ArrayList<>();
		{
			Set<String> values = new THashSet<>();

			for (XTrace trace : log) {
				String value = AttributeUtils.valueString(parameters.getAttribute(), trace);
				if (value != null) {
					values.add(value);
					traces.add(trace);
				}
			}

			if (values.size() < 2) {
				return null;
			}
		}

		double[] result = new double[parameters.getNumberOfSamples()];

		//perform the sampling
		if (parameters.isDebug()) {
			System.out.println(" start sampling threads");
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

						int[] sample = getSample(traces, parameters.getSampleSize(), random);

						result[sampleNumber] = processSample(traces, sample, parameters.getClassifier(),
								parameters.getAttribute());

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

		return result;
	}

	public static int[] getSample(List<XTrace> traces, int sampleSize, Random random) {
		int[] sample = new int[sampleSize];
		for (int i = 0; i < sampleSize; i++) {
			sample[i] = random.nextInt(traces.size());
		}
		return sample;
	}

	public static double processSample(List<XTrace> traces, int[] sample, XEventClassifier classifier,
			Attribute attribute) {
		BigDecimal sumA = BigDecimal.ZERO;
		int countA = 0;
		BigDecimal sumR = BigDecimal.ZERO;
		for (int i = 0; i < sample.length; i++) {
			String[] traceI = AssociationProcessNumerical.getTraceString(traces.get(sample[i]), classifier);
			String valueI = AttributeUtils.valueString(attribute, traces.get(sample[i]));
			for (int j = i + 1; j < sample.length; j++) {
				String[] traceJ = AssociationProcessNumerical.getTraceString(traces.get(sample[j]), classifier);
				String valueJ = AttributeUtils.valueString(attribute, traces.get(sample[j]));
				BigDecimal x = BigDecimal.valueOf(Levenshtein.getNormalisedDistance(traceI, traceJ));

				if (valueI.equals(valueJ)) {
					sumA = sumA.add(x);
					countA++;
				}
				sumR = sumR.add(x);
			}
		}

		if (countA == 0) {
			return 1;
		}
		BigDecimal a = sumA.divide(BigDecimal.valueOf(countA), 10, RoundingMode.HALF_UP);
		BigDecimal r = sumR.divide(BigDecimal.valueOf(sample.length), 10, RoundingMode.HALF_UP);

		return a.divide(r, 10, RoundingMode.HALF_UP).doubleValue();
	}
}
