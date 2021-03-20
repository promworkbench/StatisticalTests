package org.processmining.statisticaltests;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;

public class CategoricalComparisonParametersDefault extends CategoricalComparisonParametersAbstract {

	public static final double defaultAlpha = 0.05;

	public CategoricalComparisonParametersDefault(Attribute attribute) {
		super(MiningParameters.defaultClassifier, attribute, defaultAlpha);
	}

}
