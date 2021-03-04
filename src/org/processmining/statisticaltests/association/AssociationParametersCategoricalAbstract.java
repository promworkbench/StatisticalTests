package org.processmining.statisticaltests.association;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class AssociationParametersCategoricalAbstract extends AssociationParametersAbstract
		implements AssociationParametersCategorical {

	private int sampleSize;

	public AssociationParametersCategoricalAbstract(int numberOfSamples, XEventClassifier classifier,
			Attribute attribute, long seed, boolean debug, int sampleSize) {
		super(numberOfSamples, classifier, attribute, seed, debug, AssociationParametersDefault.defaultThreads);
		this.sampleSize = sampleSize;
	}

	@Override
	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

}