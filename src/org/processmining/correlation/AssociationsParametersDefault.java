package org.processmining.correlation;

import org.deckfour.xes.classification.XEventNameClassifier;

public class AssociationsParametersDefault extends AssociationsParametersAbstract {

	public AssociationsParametersDefault() {
		super(new XEventNameClassifier());
	}

}
