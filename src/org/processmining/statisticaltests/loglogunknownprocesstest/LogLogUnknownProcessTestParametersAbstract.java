package org.processmining.statisticaltests.loglogunknownprocesstest;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.statisticaltests.StatisticalTestParametersAbstract;

public class LogLogUnknownProcessTestParametersAbstract extends StatisticalTestParametersAbstract
		implements LogLogUnknownProcessTestParameters {

	private int numberOfSamples;
	private XEventClassifier classifierA;
	private XEventClassifier classifierB;

	public LogLogUnknownProcessTestParametersAbstract(boolean debug, int threads, long seed, double alpha, int numberOfSamples,
			XEventClassifier classifierA, XEventClassifier classifierB) {
		super(debug, threads, seed, alpha);
		this.numberOfSamples = numberOfSamples;
		this.classifierA = classifierA;
		this.classifierB = classifierB;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public XEventClassifier getClassifierA() {
		return classifierA;
	}

	public void setClassifierA(XEventClassifier classifierA) {
		this.classifierA = classifierA;
	}

	public XEventClassifier getClassifierB() {
		return classifierB;
	}

	public void setClassifierB(XEventClassifier classifierB) {
		this.classifierB = classifierB;
	}

}
