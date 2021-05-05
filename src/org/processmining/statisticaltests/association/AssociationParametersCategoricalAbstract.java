package org.processmining.statisticaltests.association;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class AssociationParametersCategoricalAbstract extends AssociationParametersAbstract
		implements AssociationParametersCategorical {

	public AssociationParametersCategoricalAbstract(int numberOfSamples, XEventClassifier classifier,
			Attribute attribute, long seed, boolean debug) {
		super(numberOfSamples, classifier, attribute, seed, debug, AssociationParametersDefault.defaultThreads);
	}

}