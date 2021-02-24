package org.processmining.correlation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class Associations {

	private final List<Attribute> attributes;
	private final double[] correlations;

	public Associations(Collection<Attribute> attributes) {
		this.attributes = new ArrayList<>(attributes);
		correlations = new double[attributes.size()];
		Arrays.fill(correlations, -Double.MAX_VALUE);
	}

	public void setCorrelation(Attribute attribute, double correlation) {
		correlations[attributes.indexOf(attribute)] = correlation;
	}

	public int getNumberOfAttributes() {
		return attributes.size();
	}

	public Attribute getAttribute(int att) {
		return attributes.get(att);
	}

	public double getCorrelation(int att) {
		return correlations[att];
	}
}