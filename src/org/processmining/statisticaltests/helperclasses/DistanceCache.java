package org.processmining.statisticaltests.helperclasses;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.Levenshtein;

import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.hash.TLongDoubleHashMap;

public class DistanceCache {

	private final TLongDoubleMap pair2distance;
	private final XEventClassifier classifier;

	public DistanceCache(XEventClassifier classifier) {
		pair2distance = new TLongDoubleHashMap(10, 0.5f, Long.MIN_VALUE, 0);
		this.classifier = classifier;
	}

	public double get(int indexA, XTrace traceA, int indexB, XTrace traceB) {
		long pack = pack(indexA, indexB);
		if (pair2distance.containsKey(pack)) {
			return pair2distance.get(pack);
		}

		String[] traceAs = StatisticalTestUtils.getTraceString(traceA, classifier);
		String[] traceBs = StatisticalTestUtils.getTraceString(traceB, classifier);

		double distance = Levenshtein.getNormalisedDistance(traceAs, traceBs);

		pair2distance.put(pack, distance);

		return distance;
	}

	private long pack(int indexA, int indexB) {
		return (((long) indexA) << 32) | (indexB & 0xffffffffL);
	}
}
