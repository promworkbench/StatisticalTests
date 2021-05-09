package org.processmining.statisticaltests;

import java.util.List;

import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class CategoricalComparisonResult {

	private final List<Quadruple<Double, Boolean, String, String>> r;
	private final Attribute attribute;
	private final double alpha;

	public CategoricalComparisonResult(Attribute attribute, List<Quadruple<Double, Boolean, String, String>> r,
			double alpha) {
		this.r = r;
		this.attribute = attribute;
		this.alpha = alpha;
	}

	public List<Quadruple<Double, Boolean, String, String>> get() {
		return r;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public double getAlpha() {
		return alpha;
	}
}