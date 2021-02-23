package org.processmining.correlation;

import org.deckfour.xes.classification.XEventClassifier;

public class AssociationsParametersAbstract implements AssociationsParameters {

	public AssociationsParametersAbstract(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	private XEventClassifier classifier;

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

}
