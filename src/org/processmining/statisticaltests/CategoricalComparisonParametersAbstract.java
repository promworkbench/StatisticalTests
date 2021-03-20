package org.processmining.statisticaltests;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class CategoricalComparisonParametersAbstract implements CategoricalComparisonParameters {

	private XEventClassifier classifier;
	private Attribute attribute;
	private double alpha;

	public CategoricalComparisonParametersAbstract(XEventClassifier classifier, Attribute attribute, double alpha) {
		this.classifier = classifier;
		this.attribute = attribute;
		this.alpha = alpha;
	}

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

}