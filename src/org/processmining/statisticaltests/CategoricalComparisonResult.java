package org.processmining.statisticaltests;

import java.util.List;

import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class CategoricalComparisonResult {

	private final List<Triple<Double, Boolean, String>> r;
	private final Attribute attribute;
	private final double alpha;

	public CategoricalComparisonResult(Attribute attribute, List<Triple<Double, Boolean, String>> r, double alpha) {
		this.r = r;
		this.attribute = attribute;
		this.alpha = alpha;
	}

	public List<Triple<Double, Boolean, String>> get() {
		return r;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public double getAlpha() {
		return alpha;
	}
}