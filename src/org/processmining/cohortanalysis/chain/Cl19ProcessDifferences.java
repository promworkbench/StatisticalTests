package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.LousyCohortsState;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesImpl;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;

public class Cl19ProcessDifferences
		extends LousyCohortsChainLink<Triple<IvMModel, IvMLogInfo, IvMLogInfo>, ProcessDifferencesImpl> {

	protected Triple<IvMModel, IvMLogInfo, IvMLogInfo> generateInput(LousyCohortsState state) {
		return Triple.of(state.getModel(), state.getCohortLogInfo(), state.getAntiCohortLogInfo());
	}

	protected ProcessDifferencesImpl executeLink(Triple<IvMModel, IvMLogInfo, IvMLogInfo> input, IvMCanceller canceller)
			throws Exception {
		return new ProcessDifferencesImpl(input.getA(), input.getB(), input.getC());
	}

	protected void processResult(ProcessDifferencesImpl result, LousyCohortsState state) {
		state.setProcessDifferences(result);
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setProcessDifferences(null);
	}

	public String getName() {
		return "differences";
	}

	public String getStatusBusyMessage() {
		return "Computing differences..";
	}

}