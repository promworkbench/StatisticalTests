package org.processmining.correlation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
		List<XTrace> traces = new ArrayList<>();
		for (XTrace trace : log) {
			double value = AttributeUtils.valueDouble(parameters.getAttribute(), trace);
			if (value != -Double.MAX_VALUE) {
				traces.add(trace);
			}
		}

		//construct the full stochastic language
		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(log, parameters.getClassifier());
		StochasticLanguageLog languageFull = XLog2StochasticLanguage.convert(log, parameters.getClassifier(),
				activityKey, canceller);

		//sort the traces on the attribute value
		Collections.sort(traces, new Comparator<XTrace>() {
			public int compare(XTrace t1, XTrace t2) {
				return Double.compare(AttributeUtils.valueDouble(parameters.getAttribute(), t1),
						AttributeUtils.valueDouble(parameters.getAttribute(), t2));
			}
		});

		//create a map traces => stochastic language trace index
		int[] traceIndex2stochasticLanguageTraceIndex = traceIndex2stochasticLanguageTraceIndex(
				parameters.getClassifier(), traces, languageFull);

		//set up stochastic language comparison
		EMSCParametersLogLogAbstract emscParameters = new EMSCParametersLogLogDefault();
		emscParameters.setComputeStochasticTraceAlignments(false);
		DistanceMatrix distanceMatrix = EMSCParametersDefault.defaultDistanceMatrix.clone();
		distanceMatrix.init(languageFull, languageFull, canceller);

		if (canceller.isCancelled()) {
			return null;
		}

		//perform the sampling
		double[][] result = new double[2][parameters.getNumberOfSamples()];
		Random random = parameters.getRandom();

		for (int sampleNumber = 0; sampleNumber < parameters.getNumberOfSamples(); sampleNumber++) {

			int a = random.nextInt(traces.size() - parameters.getSampleSize());
			int b = random.nextInt(traces.size() - parameters.getSampleSize());

			if (a == b) {
				//optimisation: if a == b, then their distance is 0
				result[0][sampleNumber] = 0;
				result[1][sampleNumber] = 0;
			} else {
				//distance in numeric attribute
				List<XTrace> tracesA = traces.subList(a, a + parameters.getSampleSize());
				List<XTrace> tracesB = traces.subList(b, b + parameters.getSampleSize());
				result[0][sampleNumber] = Math
						.abs(average(tracesA, parameters.getAttribute()) - average(tracesB, parameters.getAttribute()));

				//distance in process
				double[] sampleA = sample(traces, a, parameters.getSampleSize(), languageFull.size(),
						traceIndex2stochasticLanguageTraceIndex);
				double[] sampleB = sample(traces, b, parameters.getSampleSize(), languageFull.size(),
						traceIndex2stochasticLanguageTraceIndex);
				result[1][sampleNumber] = 1 - LogLogTest.getSimilarity(LogLogTest.applySample(languageFull, sampleA),
						LogLogTest.applySample(languageFull, sampleB), distanceMatrix, emscParameters, canceller);
			}

			if (canceller.isCancelled()) {
				return null;
			}
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

	public static double[] sample(List<XTrace> traces, int startIndex, int numberOfTraces, int massKeySize,
			int[] traceIndex2stochasticLanguageTraceIndex) {
		int[] result = new int[massKeySize];
		Arrays.fill(result, 0);
		for (int traceIndex = startIndex; traceIndex < startIndex + numberOfTraces; traceIndex++) {
			int stochasticLanguageTraceIndex = traceIndex2stochasticLanguageTraceIndex[traceIndex];
			result[stochasticLanguageTraceIndex]++;
		}

		return LogLogTest.normalise(result, numberOfTraces);
	}

	public static double average(List<XTrace> traces, Attribute attribute) {
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal count = BigDecimal.valueOf(traces.size());
		for (XTrace trace : traces) {
			sum = sum.add(BigDecimal.valueOf(AttributeUtils.valueDouble(attribute, trace)));
		}
		return sum.divide(count, 10, RoundingMode.HALF_UP).doubleValue();
	}
}
