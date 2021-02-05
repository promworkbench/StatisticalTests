package org.processmining.statisticaltests;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.log.StochasticLanguageLog;
import org.processmining.framework.plugin.ProMCanceller;

import com.google.common.util.concurrent.AtomicDouble;

public class LogLogUnknownProcessTest {
	public static double p(XLog logA, XLog logB, Parameters parameters, ProMCanceller canceller)
			throws InterruptedException {
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

		AtomicDouble distanceAB = new AtomicDouble();

		AtomicInteger nextSampleNumber = new AtomicInteger(-1);

		Thread[] threads = new Thread[7];

		for (int thread = 0; thread < threads.length; thread++) {
			final int thread2 = thread;
			threads[thread] = new Thread(new Runnable() {
				public void run() {
					//create sampler method
					Random random = new Random(parameters.getSeed() + thread2);
					double[] massKeyA = LogLogTest.getMassKey(languageA);
					AliasMethod aliasMethodA = new AliasMethod(massKeyA, random);

					int sampleNumber = nextSampleNumber.getAndIncrement();
					while (sampleNumber < sampleDistances.length) {

						if (sampleNumber < 0) {
							//full log-log comparison
							distanceAB.set(1 - LogLogTest.getSimilarity(languageA, languageB, distanceMatrixAB,
									emscParameters, canceller));
							System.out.println(" sample reference " + distanceAB);
						} else {
							double[] sampleA = LogLogTest.sample(aliasMethodA, parameters.getSampleSize());

							sampleDistances[sampleNumber] = 1
									- LogLogTest.getSimilarity(languageA, LogLogTest.applySample(languageA, sampleA),
											distanceMatrixAA, emscParameters, canceller);
							if (sampleNumber % 100 == 0) {
								System.out.println(
										" sample " + sampleNumber + ", distance " + sampleDistances[sampleNumber]);
							}
						}

						sampleNumber = nextSampleNumber.getAndIncrement();
					}
				}
			}, "log-log test thread " + thread);
			threads[thread].start();
		}

		//join
		for (Thread thread : threads) {
			thread.join();
		}

		Arrays.sort(sampleDistances);

		int pos = Arrays.binarySearch(sampleDistances, distanceAB.get());

		if (pos >= 0) {
			return pos / (sampleDistances.length * 1.0);
		} else {
			return (~pos) / (sampleDistances.length * 1.0);
		}
	}
}
