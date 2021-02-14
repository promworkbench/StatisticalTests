package org.processmining.correlation;

import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrixAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa.ComputeReallocationMatrix2;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.log.StochasticLanguageLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;

/**
 * This class performs an earth movers distance computation between two arrays
 * of values. The probability of the values is uniform, while the distance
 * between values is Euclidean. The implementation mostly works around the
 * things required for the process-based EMD.
 * 
 * @author sander
 *
 */
public class NumericalEarthMoversDistance {
	public static double compute(final int sampleSize, final double[] valuesA, final double[] valuesB,
			ProMCanceller canceller) {

		DistanceMatrix distanceMatrix = new DistanceMatrix() {
			public void init(StochasticLanguage languageA, StochasticLanguage languageB, ProMCanceller canceller)
					throws InterruptedException {
			}

			public double[] getDistances() {
				return null;
			}

			public double getDistance(int a, int b) {
				return Math.abs(valuesA[a] - valuesB[b]);
			}

			public DistanceMatrixAbstract clone() {
				return null;
			}
		};

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		StochasticLanguageLog languageA = new StochasticLanguageLog() {
			public int size() {
				return sampleSize;
			}

			public StochasticTraceIterator iterator() {
				return new StochasticTraceIterator() {

					int i = 0;

					public boolean hasNext() {
						return i < sampleSize;
					}

					public int[] nextIntegerTrace() {
						i++;
						return null;
					}

					public String[] next() {
						i++;
						return null;
					}

					public double getProbability() {
						return 1.0 / sampleSize;
					}
				};
			}

			public String[] getTraceString(int traceIndex) {
				return null;
			}

			public int[] getTrace(int traceIndex) {
				return null;
			}

			public Activity2IndexKey getActivityKey() {
				return null;
			}
		};
		StochasticLanguageLog languageB = languageA;

		EMSCParameters parameters = new EMSCParametersLogLogDefault();

		Pair<ReallocationMatrix, Double> p = ComputeReallocationMatrix2.computeWithDistanceMatrixInitialised(languageA,
				languageB, distanceMatrix, parameters, canceller);

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		return 1 - p.getB();
	}
}
