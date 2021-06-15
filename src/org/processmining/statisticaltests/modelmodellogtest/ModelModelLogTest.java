package org.processmining.statisticaltests.modelmodellogtest;

import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quintuple;
import org.processmining.statisticaltests.StatisticalTest;
import org.processmining.statisticaltests.helperclasses.ConcurrentSamples;
import org.processmining.statisticaltests.helperclasses.ModelHasZeroWeightsException;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;
import org.processmining.statisticaltests.helperclasses.StochasticPetriNetSample;

public class ModelModelLogTest implements
		StatisticalTest<Quintuple<StochasticNet, Marking, StochasticNet, Marking, XLog>, ModelModelLogTestParameters> {

	public boolean rejectHypothesisForSingleTest(ModelModelLogTestParameters parameters, double p) {
		return p >= 1 - 0.5 * parameters.getAlpha() || p >= 1 - 0.5 * parameters.getAlpha();
	}

	public double test(Quintuple<StochasticNet, Marking, StochasticNet, Marking, XLog> input,
			final ModelModelLogTestParameters parameters, ProMCanceller canceller, Progress progress)
			throws InterruptedException {
		final StochasticNet netA = input.getA();
		final Marking markingA = input.getB();
		final StochasticNet netB = input.getC();
		final Marking markingB = input.getD();
		final XLog log = input.getE();

		final Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(log, parameters.getClassifier());
		StochasticPetriNetSample.feed(activityKey, netA);
		StochasticPetriNetSample.feed(activityKey, netB);

		final StochasticLanguage<TotalOrder> languageL = XLog2StochasticLanguage.convert(log, parameters.getClassifier(),
				activityKey, canceller);

		final EMSCParametersLogLogAbstract emscParameters = new EMSCParametersLogLogDefault();
		emscParameters.setComputeStochasticTraceAlignments(false);

		//result variable
		final AtomicInteger accepts = new AtomicInteger(0);

		new ConcurrentSamples<SplittableRandom>(parameters.getThreads(), parameters.getNumberOfSamples(), canceller,
				progress) {
			protected SplittableRandom createThreadConstants(int threadNumber) {
				return new SplittableRandom(parameters.getSeed() + threadNumber);
			}

			protected boolean performSample(SplittableRandom random, int sampleNumber, ProMCanceller canceller) {

				if (parameters.isDebug()) {
					System.out.println(" sample number " + sampleNumber);
				}

				//take samples
				XLog sampleA;
				try {
					sampleA = StochasticPetriNetSample.sample(netA, markingA, activityKey, parameters.getSampleSize(),
							random, canceller);
				} catch (ModelHasZeroWeightsException e1) {
					e1.printStackTrace();
					return false;
				}

				if (canceller.isCancelled()) {
					return false;
				}

				XLog sampleB;
				try {
					sampleB = StochasticPetriNetSample.sample(netB, markingB, activityKey, parameters.getSampleSize(),
							random, canceller);
				} catch (ModelHasZeroWeightsException e1) {
					e1.printStackTrace();
					return false;
				}

				if (canceller.isCancelled()) {
					return false;
				}

				//transform samples
				StochasticLanguage<TotalOrder> languageA = XLog2StochasticLanguage.convert(sampleA,
						new XEventNameClassifier(), activityKey, canceller);

				if (canceller.isCancelled()) {
					return false;
				}

				StochasticLanguage<TotalOrder> languageB = XLog2StochasticLanguage.convert(sampleB,
						new XEventNameClassifier(), activityKey, canceller);

				if (canceller.isCancelled()) {
					return false;
				}

				final DistanceMatrix<TotalOrder, TotalOrder> distanceMatrixAL = EMSCParametersDefault.defaultDistanceMatrix
						.clone();
				final DistanceMatrix<TotalOrder, TotalOrder> distanceMatrixBL = EMSCParametersDefault.defaultDistanceMatrix
						.clone();

				if (canceller.isCancelled()) {
					return false;
				}

				try {
					distanceMatrixAL.init(languageA, languageL, canceller);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}

				if (canceller.isCancelled()) {
					return false;
				}

				try {
					distanceMatrixBL.init(languageB, languageL, canceller);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}

				if (canceller.isCancelled()) {
					return false;
				}

				//compute distances
				double emscAL = StatisticalTestUtils.getSimilarity(languageA, languageL, distanceMatrixAL,
						emscParameters, canceller);

				if (canceller.isCancelled()) {
					return false;
				}

				double emscBL = StatisticalTestUtils.getSimilarity(languageB, languageL, distanceMatrixBL,
						emscParameters, canceller);

				if (canceller.isCancelled()) {
					return false;
				}

				if (emscAL <= emscBL) {
					accepts.incrementAndGet();
				}

				return true;
			}

		};

		double p = accepts.get() / (parameters.getNumberOfSamples() * 1.0);

		return p;
	}
}