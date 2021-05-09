package org.processmining.statisticaltests.loglogunknownprocesstest;

import java.util.SplittableRandom;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.log.StochasticLanguageLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.statisticaltests.StatisticalTest;
import org.processmining.statisticaltests.helperclasses.AliasMethod;
import org.processmining.statisticaltests.helperclasses.ConcurrentSamples;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;

import com.google.common.util.concurrent.AtomicDouble;

public class LogLogUnknownProcessTest implements StatisticalTest<Pair<XLog, XLog>, LogLogUnknownProcessTestParameters> {

	public boolean rejectHypothesisForSingleTest(double p, double alpha) {
		return p >= 1 - alpha;
	}

	public double test(Pair<XLog, XLog> input, LogLogUnknownProcessTestParameters parameters, ProMCanceller canceller)
			throws InterruptedException {
		XLog logA = input.getA();
		XLog logB = input.getB();

		int sampleSize = logA.size();

		double[] sampleDistances = new double[parameters.getNumberOfSamples()];
		AtomicDouble distanceAB = new AtomicDouble();

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(logA, parameters.getClassifierA());
		activityKey.feed(logB, parameters.getClassifierB());

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		if (parameters.isDebug()) {
			System.out.println("create stochastic languages");
		}

		//set up objects for Earth Movers' conformance
		StochasticLanguageLog languageA = XLog2StochasticLanguage.convert(logA, parameters.getClassifierA(),
				activityKey, canceller);
		StochasticLanguageLog languageB = XLog2StochasticLanguage.convert(logB, parameters.getClassifierB(),
				activityKey, canceller);

		return p(parameters, canceller, sampleDistances, distanceAB, languageA, languageB, sampleSize);
	}

	public static double p(LogLogUnknownProcessTestParameters parameters, ProMCanceller canceller,
			double[] sampleDistances, AtomicDouble distanceAB, StochasticLanguageLog languageA,
			StochasticLanguageLog languageB, int sampleSize) throws InterruptedException {
		if (parameters.isDebug()) {
			System.out.println("create distance matrices");
		}

		final EMSCParametersLogLogAbstract emscParameters = new EMSCParametersLogLogDefault();
		emscParameters.setComputeStochasticTraceAlignments(false);
		final DistanceMatrix<int[], int[]> distanceMatrixAA = EMSCParametersDefault.defaultDistanceMatrix.clone();
		final DistanceMatrix<int[], int[]> distanceMatrixAB = EMSCParametersDefault.defaultDistanceMatrix.clone();
		distanceMatrixAA.init(languageA, languageA, canceller);
		distanceMatrixAB.init(languageA, languageB, canceller);

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		if (parameters.isDebug()) {
			System.out.println("start sampling threads");
		}

		new ConcurrentSamples<AliasMethod>(parameters.getThreads(), parameters.getNumberOfSamples(), -1, canceller) {

			protected AliasMethod createThreadConstants(int threadNumber) {
				SplittableRandom random = new SplittableRandom(parameters.getSeed() + threadNumber);
				double[] massKeyA = StatisticalTestUtils.getMassKeyNormal(languageA);
				AliasMethod aliasMethodA = new AliasMethod(massKeyA, random);

				return aliasMethodA;
			}

			protected void performSample(AliasMethod aliasMethodA, int sampleNumber) {
				if (sampleNumber < 0) {
					//full log-log comparison
					double emsc = StatisticalTestUtils.getSimilarity(languageA, languageB, distanceMatrixAB,
							emscParameters, canceller);
					if (canceller.isCancelled() || Double.isNaN(emsc)) {
						return;
					}
					distanceAB.set(1 - emsc);
					if (parameters.isDebug()) {
						System.out.println(" sample reference " + distanceAB);
					}
				} else {

					//sample
					double[] sampleA = StatisticalTestUtils.sample(aliasMethodA, sampleSize);
					StochasticLanguageLog languageX = StatisticalTestUtils.applySample(languageA, sampleA);
					double emsc = StatisticalTestUtils.getSimilarity(languageA, languageX, distanceMatrixAA,
							emscParameters, canceller);

					if (canceller.isCancelled() || Double.isNaN(emsc)) {
						return;
					}

					sampleDistances[sampleNumber] = 1 - emsc;
				}
			}

		};

		if (parameters.isDebug()) {
			System.out.println("find leq distances");
		}

		//find leq distances
		int leq = 0;
		for (double distance : sampleDistances) {
			if (distance <= distanceAB.get()) {
				leq++;
			}
		}

		if (parameters.isDebug()) {
			System.out.println("distance leq AB distance " + leq + " (of " + sampleDistances.length + ")");
		}

		double p = leq / (sampleDistances.length * 1.0);

		if (parameters.isDebug()) {
			System.out.println("p " + p);
		}

		return p;
	}

}
