package org.processmining.statisticaltests.helperclasses;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetEditable;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetImpl;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * This is not a lossless operation: not all options of stochasticNets are
 * preserved by this translation to StochasticLabelledPetriNets. Basically, it
 * only takes the weights and ignores everything else. Use with caution!
 * 
 * @author sander
 *
 */
public class StochasticNet2StochasticLabelledPetriNet {

	public static StochasticLabelledPetriNet convert(StochasticNet net, Marking initialMarking) {
		StochasticLabelledPetriNetEditable result = new StochasticLabelledPetriNetImpl();

		TObjectIntMap<Transition> transition2index = new TObjectIntHashMap<>(10, 0.5f, -1);
		TObjectIntMap<Place> place2index = new TObjectIntHashMap<>(10, 0.5f, -1);

		for (Transition transition : net.getTransitions()) {
			if (transition instanceof TimedTransition) {
				double weight = ((TimedTransition) transition).getWeight();

				int index;
				if (transition.isInvisible()) {
					index = result.addTransition(weight);
				} else {
					index = result.addTransition(transition.getLabel(), weight);
				}
				transition2index.put(transition, index);
			} else {
				throw new RuntimeException("each transition must have a weight");
			}
		}

		for (Place place : net.getPlaces()) {
			place2index.put(place, result.addPlace());
		}

		for (Place place : initialMarking) {
			int cardinality = initialMarking.occurrences(place);
			result.addPlaceToInitialMarking(place2index.get(place), cardinality);
		}

		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getEdges()) {
			if (edge.getSource() instanceof Place) {
				Place place = (Place) edge.getSource();
				Transition transition = (Transition) edge.getTarget();
				result.addPlaceTransitionArc(place2index.get(place), transition2index.get(transition));
			} else {
				Transition transition = (Transition) edge.getSource();
				Place place = (Place) edge.getTarget();
				result.addTransitionPlaceArc(transition2index.get(transition), place2index.get(place));
			}
		}

		return result;
	}
}
