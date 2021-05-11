package org.processmining.statisticaltests.helperclasses;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.Levenshtein;

public class DistanceCache {

	//	private final TLongDoubleMap pair2distance;
	private final XEventClassifier classifier;
//	private long cacheHits = 0;

	public DistanceCache(XEventClassifier classifier, int logSize) {
		//		pair2distance = new TLongDoubleHashMap(logSize * (logSize - 1) / 2, 0.8f, Long.MIN_VALUE, 0);
		this.classifier = classifier;
	}

	public double get(int indexA, XTrace traceA, int indexB, XTrace traceB) {
		//		long pack = pack(indexA, indexB);
		//		if (pair2distance.containsKey(pack)) {
		//			cacheHits++;
		//			if (cacheHits % 10000 == 0) {
		//				System.out.println("  cache hits " + cacheHits + ", size " + pair2distance.size());
		//			}
		//			return pair2distance.get(pack);
		//		}

		String[] traceAs = StatisticalTestUtils.getTraceString(traceA, classifier);
		String[] traceBs = StatisticalTestUtils.getTraceString(traceB, classifier);

		double distance = Levenshtein.getNormalisedDistance(traceAs, traceBs);

		//		synchronized (pair2distance) {
		//			pair2distance.put(pack, distance);
		//		}

		return distance;
	}

	//	private long pack(int indexA, int indexB) {
	//		return (((long) indexA) << 32) | (indexB & 0xffffffffL);
	//	}
}
