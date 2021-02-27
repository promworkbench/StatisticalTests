package org.processmining.statisticaltests.association;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class AssociationParametersCategoricalDefault extends AssociationParametersCategoricalAbstract {

	public final static int defaultNumberOfSamples = 100;
	public final static int defaultSampleSize = 100;

	public AssociationParametersCategoricalDefault(Attribute attribute) {
		super(defaultNumberOfSamples, CorrelationParametersDefault.defaultClassifier, attribute,
				System.currentTimeMillis(), CorrelationParametersDefault.defaultDebug, defaultSampleSize);
	}
}