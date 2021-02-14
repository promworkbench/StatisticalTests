package org.processmining.correlation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.log.StochasticLanguageLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;
import org.processmining.statisticaltests.LogLogTest;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

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

		//construct the full stochastic language
		if (parameters.isDebug()) {
			System.out.println(" make stochastic language");
		}
		StochasticLanguageLog languageFull;
		{
			Activity2IndexKey activityKey = new Activity2IndexKey();
			activityKey.feed(log, parameters.getClassifier());
			languageFull = XLog2StochasticLanguage.convert(log, parameters.getClassifier(), activityKey, canceller);
		}

		//create a map traces => stochastic language trace index
		if (parameters.isDebug()) {
			System.out.println(" create trace map");
		}
		int[] traceIndex2stochasticLanguageTraceIndex = traceIndex2stochasticLanguageTraceIndex(
				parameters.getClassifier(), traces, languageFull);

		//set up stochastic language comparison
		EMSCParametersLogLogAbstract emscParameters = new EMSCParametersLogLogDefault();
		DistanceMatrix distanceMatrix = EMSCParametersDefault.defaultDistanceMatrix.clone();
		{
			emscParameters.setComputeStochasticTraceAlignments(false);
			distanceMatrix.init(languageFull, languageFull, canceller);
		}

		if (canceller.isCancelled()) {
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

						int[] sampleA = sample(traces, parameters.getSampleSize(), random);
						int[] sampleB = sample(traces, parameters.getSampleSize(), random);

						double[] result2 = performSampleMeasure(parameters, canceller, traces, languageFull,
								traceIndex2stochasticLanguageTraceIndex, emscParameters, distanceMatrix, sampleA,
								sampleB, maxAttributeValue);

						if (canceller.isCancelled()) {
							return;
						}

						result[0][sampleNumber] = result2[0];
						result[1][sampleNumber] = result2[1];

						if (parameters.isDebug() && sampleNumber % 100 == 0) {
							System.out.println(" sample " + sampleNumber + ", \\varphi=" + result[0][sampleNumber]
									+ ", \\delta=" + result[1][sampleNumber]);
						}

						sampleNumber = nextSampleNumber.getAndIncrement();
					}
				}

				public double[] performSampleMeasure(CorrelationParameters parameters, ProMCanceller canceller,
						List<XTrace> traces, StochasticLanguageLog languageFull,
						int[] traceIndex2stochasticLanguageTraceIndex, EMSCParametersLogLogAbstract emscParameters,
						DistanceMatrix distanceMatrix, int[] sampleA, int[] sampleB, double maxAttributeValue) {
					//distance in numeric attribute
					double attributeDistance = average(traces, parameters.getSampleSize(), sampleA, sampleB,
							parameters.getAttribute(), canceller, maxAttributeValue);

					if (canceller.isCancelled()) {
						return null;
					}

					//distance in process
					double[] sampleAx = sample2stochasticLanguage(sampleA, parameters.getSampleSize(),
							languageFull.size(), traceIndex2stochasticLanguageTraceIndex);
					double[] sampleBx = sample2stochasticLanguage(sampleB, parameters.getSampleSize(),
							languageFull.size(), traceIndex2stochasticLanguageTraceIndex);
					double processDistance = 1 - LogLogTest.getSimilarity(
							LogLogTest.applySample(languageFull, sampleAx),
							LogLogTest.applySample(languageFull, sampleBx), distanceMatrix, emscParameters, canceller);

					//					System.out.println(StochasticLanguageLog2String
					//							.toString(LogLogTest.applySample(languageFull, sampleAx), false));
					//					System.out.println(StochasticLanguageLog2String
					//							.toString(LogLogTest.applySample(languageFull, sampleBx), false));

					double[] result2 = new double[] { attributeDistance, processDistance };
					return result2;
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

	private static int[] sample(List<XTrace> traces, int sampleSize, Random random) {
		int[] result = new int[traces.size()];
		for (int i = 0; i < sampleSize; i++) {
			result[random.nextInt(traces.size())]++;
		}
		return result;
	}

	public static int[] traceIndex2stochasticLanguageTraceIndex(final XEventClassifier classifier, List<XTrace> traces,
			StochasticLanguageLog languageFull) {
		int[] traceIndex2stochasticLanguageTraceIndex = new int[traces.size()];

		//first, create a map trace string => stochastic language trace index
		TObjectIntMap<String[]> traceString2stochasticLanguageTraceIndex = new TObjectIntCustomHashMap<String[]>(
				new HashingStrategy<String[]>() {
					private static final long serialVersionUID = 1549874324564L;

					public int computeHashCode(String[] traceString) {
						return Arrays.hashCode(traceString);
					}

					public boolean equals(String[] traceString1, String[] traceString2) {
						return Arrays.equals(traceString1, traceString2);
					}
				});
		{
			int i = 0;
			for (StochasticTraceIterator it = languageFull.iterator(); it.hasNext();) {
				traceString2stochasticLanguageTraceIndex.put(it.next(), i);
				i++;
			}
		}

		//second, make a map traces => stochastic language trace index
		for (int traceIndex = 0; traceIndex < traces.size(); traceIndex++) {
			XTrace trace = traces.get(traceIndex);

			//create trace string
			String[] traceString = new String[trace.size()];
			{
				int i = 0;
				for (XEvent event : trace) {
					traceString[i] = classifier.getClassIdentity(event);
					i++;
				}
			}

			int stochasticLanguageTraceIndex = traceString2stochasticLanguageTraceIndex.get(traceString);
			traceIndex2stochasticLanguageTraceIndex[traceIndex] = stochasticLanguageTraceIndex;
		}
		return traceIndex2stochasticLanguageTraceIndex;
	}

	public static double[] sample2stochasticLanguage(int[] sample, int numberOfTraces, int massKeySize,
			int[] traceIndex2stochasticLanguageTraceIndex) {
		int[] result = new int[massKeySize];
		Arrays.fill(result, 0);
		for (int traceIndex = 0; traceIndex < sample.length; traceIndex++) {
			if (sample[traceIndex] > 0) {
				int stochasticLanguageTraceIndex = traceIndex2stochasticLanguageTraceIndex[traceIndex];
				//				System.out.println("language trace index " + stochasticLanguageTraceIndex);
				result[stochasticLanguageTraceIndex] += sample[traceIndex];
			}
		}

		return LogLogTest.normalise(result, numberOfTraces);
	}

	public static double average(List<XTrace> traces, int sampleSize, int[] sampleA, int[] sampleB, Attribute attribute,
			ProMCanceller canceller, double maxAttributeValue) {
		double[] valuesA = gatherValuesOfSample(traces, sampleSize, sampleA, attribute, maxAttributeValue);
		double[] valuesB = gatherValuesOfSample(traces, sampleSize, sampleB, attribute, maxAttributeValue);

		return NumericalEarthMoversDistance.compute(sampleSize, valuesA, valuesB, canceller);
	}

	public static double[] gatherValuesOfSample(List<XTrace> traces, int sampleSize, int[] sample, Attribute attribute,
			double maxAttributeValue) {
		double[] result = new double[sampleSize];

		int j = 0;

		for (int i = 0; i < sample.length; i++) {
			if (sample[i] > 0) {
				XTrace trace = traces.get(i);

				for (int x = j; x < j + sample[i]; x++) {
					result[x] = AttributeUtils.valueDouble(attribute, trace) / maxAttributeValue;
				}

				j += sample[i];
			}
		}

		return result;
	}
}