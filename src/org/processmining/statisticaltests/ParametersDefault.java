package org.processmining.statisticaltests;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

/**
 * Notice that the seed is set once for the parameter object. Thus, running
 * twice with the same parameter object will give the same result. Change the
 * seed to prevent this.
 * 
 * @author sander
 *
 */
public class ParametersDefault extends ParametersAbstract {

	public final static int defaultSampleSize = 100;
	public final static XEventClassifier defaultClassifier = MiningParameters.getDefaultClassifier();

	public ParametersDefault(int numberOfTracesInLogsCombined) {
		super(defaultSampleSize, Math.max(10, (numberOfTracesInLogsCombined) / 20), System.currentTimeMillis(),
				defaultClassifier, defaultClassifier);
	}

}