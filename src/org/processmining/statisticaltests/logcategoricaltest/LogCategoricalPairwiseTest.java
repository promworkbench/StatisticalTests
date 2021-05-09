package org.processmining.statisticaltests.logcategoricaltest;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.Progress;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.CategoricalComparisonResult;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTest;

public class LogCategoricalPairwiseTest {
	public static CategoricalComparisonResult computePairWise(XLog log, LogCategoricalPairwiseTestParameters parameters,
			ProMCanceller canceller, Progress progress) throws InterruptedException {
		Attribute attribute = parameters.getAttribute();
		XFactory factory = new XFactoryNaiveImpl();

		if (progress != null) {
			progress.setMinimum(0);
			progress.setMaximum(attribute.getStringValues().size());
		}

		List<Triple<Double, String, String>> result = new ArrayList<>();

		for (String valueA : attribute.getStringValues()) {
			for (String valueB : attribute.getStringValues()) {
				if (valueA.compareTo(valueB) < 0) {
					System.out.println(valueA + " vs. " + valueB);
					//create two logs
					XLog logA = factory.createLog();
					XLog logB = factory.createLog();

					for (XTrace trace : log) {
						if (attribute.getLiteral(trace) != null) {
							if (attribute.getLiteral(trace).equals(valueA)) {
								logA.add(trace);
							} else if (attribute.getLiteral(trace).equals(valueB)) {
								logB.add(trace);
							}
						}
					}

					if (logA.isEmpty() || logB.isEmpty()) {
						result.add(Triple.of(Double.NaN, valueA, valueB));
					} else {
						double p = new LogLogUnknownProcessTest().test(Pair.of(logA, logB), parameters, canceller);

						if (canceller.isCancelled()) {
							return null;
						}

						System.out.println(p);
						result.add(Triple.of(p, valueA, valueB));
					}

					if (canceller.isCancelled()) {
						return null;
					}

					if (progress != null) {
						progress.setValue(progress.getValue() + 1);
					}
				}
			}
		}

		if (canceller.isCancelled()) {
			return null;
		}

		//apply Benjamini-Hochberg
		List<Quadruple<Double, Boolean, String, String>> r = StatisticalTestUtils.benjaminiHochberg(result,
				parameters.getAlpha());

		if (canceller.isCancelled()) {
			return null;
		}

		return new CategoricalComparisonResult(attribute, r, parameters.getAlpha());
	}
}
