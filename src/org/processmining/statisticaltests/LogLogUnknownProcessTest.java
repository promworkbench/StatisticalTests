package org.processmining.statisticaltests;

import java.util.Arrays;
import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.log.StochasticLanguageLog;
import org.processmining.framework.plugin.ProMCanceller;

public class LogLogUnknownProcessTest {
	public static double p(XLog logA, XLog logB, Parameters parameters, ProMCanceller canceller)
			throws InterruptedException {
		Random random = new Random(parameters.getSeed());

		double[] sampleDistances = new double[parameters.getNumberOfReSamples()];

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(logA, parameters.getClassifierA());
		activityKey.feed(logB, parameters.getClassifierB());

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		//set up objects for Earth Movers' conformance
		StochasticLanguageLog languageA = XLog2StochasticLanguage.convert(logA, parameters.getClassifierA(),
				activityKey, canceller);
		StochasticLanguageLog languageB = XLog2StochasticLanguage.convert(logB, parameters.getClassifierB(),
				activityKey, canceller);

		EMSCParametersLogLogAbstract emscParameters = new EMSCParametersLogLogDefault();
		emscParameters.setComputeStochasticTraceAlignments(false);
		DistanceMatrix distanceMatrixAA = EMSCParametersDefault.defaultDistanceMatrix.clone();
		DistanceMatrix distanceMatrixAB = EMSCParametersDefault.defaultDistanceMatrix.clone();
		distanceMatrixAA.init(languageA, languageA, canceller);
		distanceMatrixAB.init(languageA, languageB, canceller);

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		//create sampler method
		double[] massKeyA = LogLogTest.getMassKey(languageA);
		AliasMethod aliasMethodA = new AliasMethod(massKeyA, random);

		for (int i = 0; i < sampleDistances.length; i++) {
			double[] sampleA = LogLogTest.sample(aliasMethodA, parameters.getSampleSize());

			sampleDistances[i] = LogLogTest.getSimilarity(languageA, LogLogTest.applySample(languageA, sampleA), distanceMatrixAA,
					emscParameters, canceller);
		}

		double distanceAB = LogLogTest.getSimilarity(languageA, languageB, distanceMatrixAB, emscParameters, canceller);
		
		Arrays.sort(sampleDistances);
		
		int pos = Arrays.binarySearch(sampleDistances, distanceAB);
		
		if (pos >= 0) {
			return pos / (sampleDistances.length * 1.0);
		} else {
			return (~pos) / (sampleDistances.length * 1.0);
		}
	}
}
