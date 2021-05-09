package org.processmining.statisticaltests.logcategoricaltest;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTestParametersDefault;

public class LogCategoricalPairwiseTestParametersDefault extends LogLogUnknownProcessTestParametersDefault
		implements LogCategoricalPairwiseTestParameters {

	private Attribute attribute;

	public LogCategoricalPairwiseTestParametersDefault(Attribute attribute) {
		this.attribute = attribute;
	}

	@Override
	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

}