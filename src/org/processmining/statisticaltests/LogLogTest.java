package org.processmining.statisticaltests;

import org.processmining.statisticaltests.helperclasses.AliasMethod;

public class LogLogTest {

	

	public static double[] sample(AliasMethod aliasMethod, int sampleSize) {
		double[] result = new double[aliasMethod.getProbabilitiesSize()];
		for (int i = 0; i < sampleSize; i++) {
			result[aliasMethod.next()]++;
		}
		double ss = sampleSize;
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i] / ss;
		}
		return result;
	}

}