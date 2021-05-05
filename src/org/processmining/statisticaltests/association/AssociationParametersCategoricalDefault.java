package org.processmining.statisticaltests.association;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class AssociationParametersCategoricalDefault extends AssociationParametersCategoricalAbstract {

	public final static int defaultNumberOfSamples = 100;

	public AssociationParametersCategoricalDefault(Attribute attribute) {
		super(defaultNumberOfSamples, AssociationParametersDefault.defaultClassifier, attribute,
				System.currentTimeMillis(), AssociationParametersDefault.defaultDebug);
	}
}