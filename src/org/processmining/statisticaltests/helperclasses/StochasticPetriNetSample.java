package org.processmining.statisticaltests.helperclasses;

import java.util.SplittableRandom;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.EfficientStochasticPetriNetSemanticsImpl;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

/**
 * This class assumes that there is no livelock in the model.
 * 
 * @author sander
 *
 */
public class StochasticPetriNetSample {

	private static class Scratch {
		EfficientStochasticPetriNetSemanticsImpl semantics;
		SplittableRandom random;
		XFactory factory;
	}

	public static XLog sample(StochasticNet net, Marking initialMarking, Activity2IndexKey activityKey,
			int numberOfTraces, SplittableRandom random, ProMCanceller canceller) throws ModelHasZeroWeightsException {
		Scratch s = new Scratch();
		s.factory = new XFactoryNaiveImpl();
		s.random = random;

		s.semantics = new EfficientStochasticPetriNetSemanticsImpl(net, initialMarking);
		byte[] markingB = s.semantics.convert(initialMarking);

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

			double chosenWeight = s.random.nextDouble(sumWeight);
			int chosenTransition = -1;
			for (int enabledTransition : enabledTransitions) {
				if (chosenTransition < 0) {
					double transitionWeight = s.semantics.getTransitionWeight(enabledTransition);
					chosenWeight -= transitionWeight;
					if (chosenWeight <= 0) {
						chosenTransition = enabledTransition;
					}
				}
			}

			assert chosenTransition >= 0;

			XEvent event = s.factory.createEvent();
			event.getAttributes().put(XConceptExtension.KEY_NAME,
					new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, s.semantics.getLabel(chosenTransition)));
			result.add(event);

			s.semantics.executeTransition(chosenTransition);

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

	public static void feed(Activity2IndexKey activityKey, StochasticLabelledPetriNet net) {
		for (int transition = 0; transition < net.getNumberOfTransitions(); transition++) {
			if (!net.isTransitionSilent(transition)) {
				activityKey.feed(net.getTransitionLabel(transition));
			}
		}
	}

}
