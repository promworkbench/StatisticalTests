package org.processmining.statisticaltests.helperclasses;

import java.util.BitSet;
import java.util.SplittableRandom;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemantics;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSemanticsImpl;

public class StochasticLabelledPetriNetSample {
	private static class Scratch {
		StochasticLabelledPetriNetSemantics semantics;
		SplittableRandom random;
		XFactory factory;
	}

	public static XLog sample(StochasticNet sNet, Marking initialMarking, int numberOfTraces, SplittableRandom random,
			ProMCanceller canceller) throws ModelHasZeroWeightsException {
		StochasticLabelledPetriNet net = StochasticNet2StochasticLabelledPetriNet.convert(sNet, initialMarking);
		return sample(net, numberOfTraces, random, canceller);
	}

	public static XLog sample(StochasticLabelledPetriNet net, int numberOfTraces, SplittableRandom random,
			ProMCanceller canceller) throws ModelHasZeroWeightsException {
		Scratch s = new Scratch();
		s.factory = new XFactoryNaiveImpl();
		s.random = random;

		s.semantics = new StochasticLabelledPetriNetSemanticsImpl(net);
		byte[] markingB = s.semantics.getState();

		XLog log = s.factory.createLog();
		for (int i = 0; i < numberOfTraces; i++) {
			s.semantics.setState(markingB);

			log.add(sampleTrace(s, canceller));

			if (canceller.isCancelled()) {
				return null;
			}
		}
		return log;
	}

	public static XTrace sampleTrace(Scratch s, ProMCanceller canceller) throws ModelHasZeroWeightsException {
		XTrace result = s.factory.createTrace();

		while (!s.semantics.isFinalState()) {

			int chosenTransition = chooseTransition(s);

			assert chosenTransition >= 0;

			if (!s.semantics.isTransitionSilent(chosenTransition)) {
				XEvent event = s.factory.createEvent();
				event.getAttributes().put(XConceptExtension.KEY_NAME, new XAttributeLiteralImpl(
						XConceptExtension.KEY_NAME, s.semantics.getTransitionLabel(chosenTransition)));
				result.add(event);
			}

			s.semantics.executeTransition(chosenTransition);

			if (canceller.isCancelled()) {
				return null;
			}
		}

		return result;
	}

	public static int chooseTransition(Scratch s) throws ModelHasZeroWeightsException {

		BitSet enabledTransitions = s.semantics.getEnabledTransitions();
		double sumWeight = s.semantics.getTotalWeightOfEnabledTransitions();

		if (sumWeight == 0) {
			throw new ModelHasZeroWeightsException();
		}

		double chosenWeight = s.random.nextDouble(sumWeight);
		int chosenTransition = -1;
		for (int enabledTransition = enabledTransitions.nextSetBit(
				0); enabledTransition >= 0; enabledTransition = enabledTransitions.nextSetBit(enabledTransition + 1)) {
			chosenTransition = enabledTransition;
			double transitionWeight = s.semantics.getTransitionWeight(enabledTransition);
			chosenWeight -= transitionWeight;
			if (chosenWeight <= 0) {
				return chosenTransition;
			}
		}
		return chosenTransition;
	}
}