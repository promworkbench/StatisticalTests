package org.processmining.statisticaltests;

import org.processmining.framework.plugin.ProMCanceller;

public interface StatisticalTest<I, P extends StatisticalTestParameters> {
	/**
	 * No side effects allowed.
	 * 
	 * @param p
	 * @param alpha
	 * @return true: reject null-hypothesis; false: do not reject
	 *         null-hypothesis
	 */
	public boolean rejectHypothesisForSingleTest(double p, double alpha);

	/**
	 * Perform the test. No side effects allowed.
	 * 
	 * @param input
	 * @param parameters
	 * @param canceller
	 * @return
	 */
	public double test(I input, P parameters, ProMCanceller canceller);
}
