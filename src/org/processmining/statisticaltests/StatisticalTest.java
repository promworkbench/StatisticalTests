package org.processmining.statisticaltests;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.Progress;

public interface StatisticalTest<I, P extends StatisticalTestParameters> {
	/**
	 * No side effects allowed.
	 * 
	 * @param parameters
	 * @param p
	 * @return true: reject null-hypothesis; false: do not reject
	 *         null-hypothesis
	 */
	public boolean rejectHypothesisForSingleTest(P parameters, double p);

	/**
	 * Perform the test. No side effects allowed.
	 * 
	 * @param input
	 * @param parameters
	 * @param canceller
	 *            may not be null
	 * @param progress
	 *            may be null
	 * @return the p-value of the test (confirm with
	 *         rejectHypothesisForSingleTest whether hypothesis is rejected),
	 *         Double.NaN if the test failed.
	 * @throws InterruptedException
	 */
	public double test(I input, P parameters, ProMCanceller canceller, Progress progress) throws InterruptedException;
}
