package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;

public class Cl19Done extends LousyCohortsChainLink<Object, Object> {

	protected Object generateInput(LousyCohortsState state) {
		return null;
	}

	protected Object executeLink(Object input, IvMCanceller canceller) throws Exception {
		Thread.sleep(5000);
		return null;
	}

	protected void processResult(Object result, LousyCohortsState state) {

	}

	protected void invalidateResult(LousyCohortsState state) {

	}

	public String getName() {
		return "done";
	}

	public String getStatusBusyMessage() {
		return "done";
	}
}
