package org.processmining.statisticaltests.association;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.statisticaltests.helperclasses.SEventClasses;
import org.processmining.statisticaltests.test.FakeContext;

import nl.tue.astar.AStarException;

public class AssociationConformanceNumerical {
	public static double[][] compute(Attribute attribute, AssociationsParameters parameters, XLog log,
			final AcceptingPetriNet aNet, ProMCanceller canceller) throws InterruptedException {
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
				double value = AttributeUtils.valueDouble(attribute, trace);
				if (value != -Double.MAX_VALUE && !(attribute.isTime() && value < 0)) {
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

		if (traces.size() == 0) {
			return null;
		}

		//perform the sampling
		if (parameters.isDebug()) {
			System.out.println(" start sampling threads");
		}
		double[][] result;
		final int numberOfSamples;
		if (parameters.getNumberOfSamples() >= 0) {
			result = new double[2][parameters.getNumberOfSamples()];
			numberOfSamples = parameters.getNumberOfSamples();
		} else {
			result = new double[2][traces.size()];
			numberOfSamples = traces.size();
		}

		AtomicInteger nextSampleNumber = new AtomicInteger(0);

		Thread[] threads = new Thread[parameters.getThreads()];

		for (int thread = 0; thread < threads.length; thread++) {
			final int thread2 = thread;
			threads[thread] = new Thread(new Runnable() {
				public void run() {

					Random random = new Random(parameters.getSeed() + thread2);

					int sampleNumber = nextSampleNumber.getAndIncrement();
					while (sampleNumber < numberOfSamples) {

						if (canceller.isCancelled()) {
							return;
						}

						int sample;
						if (parameters.getNumberOfSamples() >= 0) {
							sample = random.nextInt(traces.size());
						} else {
							sample = sampleNumber;
						}

						double[] result2 = performSampleMeasure(attribute, parameters, canceller, traces, sample,
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

				private double[] performSampleMeasure(Attribute attribute, AssociationsParameters parameters,
						ProMCanceller canceller, List<XTrace> traces, int sample, double minAttributeValue,
						double maxAttributeValue) {
					double value = (AttributeUtils.valueDouble(attribute, traces.get(sample)) - minAttributeValue)
							/ (maxAttributeValue - minAttributeValue);

					assert !Double.isNaN(value);

					double fitness;
					try {
						XLog localLog = new XLogImpl(new XAttributeMapImpl());
						localLog.add(traces.get(sample));
						fitness = AryaFitness(aNet, localLog).getA();
					} catch (AStarException e) {
						e.printStackTrace();
						fitness = -Double.MAX_VALUE;
					}

					return new double[] { value, fitness };
				}
			}, "conformance-numerical correlation thread " + thread);
			threads[thread].start();
		}

		//join
		for (Thread thread : threads) {
			thread.join();
		}

		return result;
	}

	public static Triple<Double, TransEvClassMapping, PNRepResult> AryaFitness(AcceptingPetriNet aNet, XLog log)
			throws AStarException {
		Triple<SEventClasses, XEventClass, TransEvClassMapping> t = getMapping(aNet, log);
		SEventClasses eventClasses = t.getA();
		XEventClass dummy = t.getB();
		TransEvClassMapping mapping = t.getC();
		FakeContext context = new FakeContext();

		PNLogReplayer replayer = new PNLogReplayer();
		CostBasedCompleteParam replayParameters = new CostBasedCompleteParam(eventClasses.getClasses(), dummy,
				aNet.getNet().getTransitions(), 1, 1);
		replayParameters.setInitialMarking(aNet.getInitialMarking());
		replayParameters.setMaxNumOfStates(Integer.MAX_VALUE);
		IPNReplayAlgorithm algorithm1 = new PetrinetReplayerWithILP();
		Marking[] finalMarkings = new Marking[aNet.getFinalMarkings().size()];
		replayParameters.setFinalMarkings(aNet.getFinalMarkings().toArray(finalMarkings));
		replayParameters.setCreateConn(false);
		replayParameters.setGUIMode(false);

		PNRepResult result = replayer.replayLog(context, aNet.getNet(), log, mapping, algorithm1, replayParameters);
		double fitness = (double) result.getInfo().get("Trace Fitness");

		return Triple.of(fitness, mapping, result);
	}

	public static Triple<SEventClasses, XEventClass, TransEvClassMapping> getMapping(AcceptingPetriNet aNet, XLog log) {
		XLogInfo myLogInfo = XLogInfoImpl.create(log, XLogInfoImpl.NAME_CLASSIFIER);
		SEventClasses eventClasses = new SEventClasses(myLogInfo.getEventClasses());
		XEventClass dummy = new XEventClass("", 1);
		eventClasses.harmonizeIndices();

		TransEvClassMapping mapping;
		{
			mapping = new TransEvClassMapping(eventClasses.getClassifier(), dummy);

			for (Transition t : aNet.getNet().getTransitions()) {
				if (t.isInvisible()) {
					mapping.put(t, dummy);
				} else {
					XEventClass e = eventClasses.getByIdentity(t.getLabel());
					if (e == null) {
						eventClasses.register(t.getLabel());
						e = eventClasses.getByIdentity(t.getLabel());
					}
					mapping.put(t, e);
				}
			}
		}
		return Triple.of(eventClasses, dummy, mapping);
	}
}