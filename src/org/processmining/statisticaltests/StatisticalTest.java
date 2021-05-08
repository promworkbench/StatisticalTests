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
	 * @return the p-value of the test (confirm with
	 *         rejectHypothesisForSingleTest whether hypothesis is rejected),
	 *         Double.NaN if the test failed.
	 * @throws InterruptedException
	 */
	public double test(I input, P parameters, ProMCanceller canceller) throws InterruptedException;
}
