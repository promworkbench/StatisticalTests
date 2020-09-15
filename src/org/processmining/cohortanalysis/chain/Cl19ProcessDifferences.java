package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.CohortsState;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesImpl;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;

public class Cl19ProcessDifferences
		extends CohortsChainLink<Triple<IvMModel, IvMLogInfo, IvMLogInfo>, ProcessDifferencesImpl> {

	protected Triple<IvMModel, IvMLogInfo, IvMLogInfo> generateInput(CohortsState state) {
		return Triple.of(state.getModel(), state.getCohortLogInfo(), state.getAntiCohortLogInfo());
	}

	protected ProcessDifferencesImpl executeLink(Triple<IvMModel, IvMLogInfo, IvMLogInfo> input, IvMCanceller canceller)
			throws Exception {
		return new ProcessDifferencesImpl(input.getA(), input.getB(), input.getC());
	}

	protected void processResult(ProcessDifferencesImpl result, CohortsState state) {
		state.setProcessDifferences(result);
	}

	protected void invalidateResult(CohortsState state) {
		state.setProcessDifferences(null);
	}

	public String getName() {
		return "differences";
	}

	public String getStatusBusyMessage() {
		return "Computing differences..";
	}

}