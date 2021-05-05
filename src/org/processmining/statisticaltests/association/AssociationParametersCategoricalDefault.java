package org.processmining.statisticaltests.association;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class AssociationParametersCategoricalDefault extends AssociationParametersCategoricalAbstract {

	public final static int defaultNumberOfSamples = 500;
	public final static int defaultSampleSize = 1000;

	public AssociationParametersCategoricalDefault(Attribute attribute) {
		super(defaultNumberOfSamples, AssociationParametersDefault.defaultClassifier, attribute,
				System.currentTimeMillis(), AssociationParametersDefault.defaultDebug, defaultSampleSize);
	}
}