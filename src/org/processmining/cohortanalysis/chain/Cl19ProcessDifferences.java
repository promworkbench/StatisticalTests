package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.CohortsState;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesParetoImpl;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesPareto;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;

public class Cl19ProcessDifferences
		extends CohortsChainLink<Triple<IvMModel, IvMLogInfo, IvMLogInfo>, ProcessDifferencesPareto> {

	protected Triple<IvMModel, IvMLogInfo, IvMLogInfo> generateInput(CohortsState state) {
		return Triple.of(state.getModel(), state.getCohortLogInfo(), state.getAntiCohortLogInfo());
	}

	protected ProcessDifferencesPareto executeLink(Triple<IvMModel, IvMLogInfo, IvMLogInfo> input,
			IvMCanceller canceller) throws Exception {
		return new ProcessDifferencesParetoImpl(input.getA(), input.getB(), input.getC());
	}

	protected void processResult(ProcessDifferencesPareto result, CohortsState state) {
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