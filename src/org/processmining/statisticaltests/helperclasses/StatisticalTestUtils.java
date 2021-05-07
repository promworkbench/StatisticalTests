package org.processmining.statisticaltests.helperclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;

import gnu.trove.set.hash.THashSet;

public class StatisticalTestUtils {

	/**
	 * 
	 * @param attribute
	 * @param log
	 * @param debug
	 * @return a list of traces that have the attribute, or null if (1) all
	 *         values are the same, or (2) all values are unique
	 */
	public static List<XTrace> filterTracesCategorical(Attribute attribute, XLog log, boolean debug) {
		assert attribute.isLiteral();

		final List<XTrace> traces = new ArrayList<>();
		{
			Set<String> values = new THashSet<>();

			for (XTrace trace : log) {
				String value = AttributeUtils.valueString(attribute, trace);
				if (value != null) {
					values.add(value);
					traces.add(trace);
				}
			}

			if (values.size() < 2) {
				if (debug) {
					System.out.println("  attribute rejected as it does not have 2 values");
				}
				return null;
			}

			if (values.size() == traces.size()) {
				if (debug) {
					System.out.println("  attribute rejected as each value of the attribute is unique");
				}
				return null;
			}

		}
		return traces;
	}

	/**
	 * 
	 * @param traces
	 * @param sampleSize
	 * @param random
	 * @return an array of trace indices in the list
	 */
	public static int[] getSample(List<XTrace> traces, int sampleSize, Random random) {
		int[] sample = new int[sampleSize];
		for (int i = 0; i < sampleSize; i++) {
			sample[i] = random.nextInt(traces.size());
		}
		return sample;
	}
}
