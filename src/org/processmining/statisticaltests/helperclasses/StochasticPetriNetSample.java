package org.processmining.statisticaltests.helperclasses;

import java.util.SplittableRandom;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.EfficientStochasticPetriNetSemanticsImpl;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.TransitionMap;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * This class assumes that there is no livelock in the model.
 * 
 * @author sander
 *
 */
public class StochasticPetriNetSample {

	private static class Scratch {
		StochasticNet net;
		Marking initialMarking;
		EfficientStochasticPetriNetSemanticsImpl semantics;
		TransitionMap transitionMap;
		StochasticTransition2IndexKey transitionKey;
		SplittableRandom random;
		XFactory factory;
	}

	public static XLog sample(StochasticNet net, Marking initialMarking, Activity2IndexKey activityKey,
			int numberOfTraces, SplittableRandom random, ProMCanceller canceller) throws ModelHasZeroWeightsException {
		Scratch s = new Scratch();
		s.factory = new XFactoryNaiveImpl();
		s.net = net;
		s.initialMarking = initialMarking;
		s.random = random;

		s.semantics = new EfficientStochasticPetriNetSemanticsImpl(net, initialMarking);
		byte[] markingB = s.semantics.convert(initialMarking);
		s.transitionMap = new TransitionMap(net, s.semantics);
		s.transitionKey = new StochasticTransition2IndexKey(s.semantics, activityKey);

		XLog log = s.factory.createLog();
		for (int i = 0; i < numberOfTraces; i++) {
			s.semantics.setState(markingB);

			log.add(sampleTrace(s));
		}
		return log;
	}

	public static XTrace sampleTrace(Scratch s) throws ModelHasZeroWeightsException {
		XTrace result = s.factory.createTrace();

		int[] enabledTransitions = s.semantics.getEnabledTransitions();
		while (enabledTransitions.length > 0) {

			double sumWeight = 0;
			for (int enabledTransition : enabledTransitions) {
				sumWeight += s.semantics.getTransitionWeight(enabledTransition);
			}

			if (sumWeight == 0) {
				throw new ModelHasZeroWeightsException();
			}
			
			

			enabledTransitions = s.semantics.getEnabledTransitions();
		}

		return result;
	}

	public static void feed(Activity2IndexKey activityKey, StochasticNet net) {
		for (Transition transition : net.getTransitions()) {
			if (!transition.isInvisible()) {
				activityKey.feed(transition);
			}
		}
	}

}
