package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.CohortsState;
import org.processmining.cohortanalysis.visualisation.ProcessDifferences;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesImpl;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesPareto;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesParetoImpl;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;

public class Cl19ProcessDifferences extends
		CohortsChainLink<Triple<IvMModel, IvMLogInfo, IvMLogInfo>, Pair<ProcessDifferences, ProcessDifferencesPareto>> {

	protected Triple<IvMModel, IvMLogInfo, IvMLogInfo> generateInput(CohortsState state) {
		return Triple.of(state.getModel(), state.getCohortLogInfo(), state.getAntiCohortLogInfo());
	}

	protected Pair<ProcessDifferences, ProcessDifferencesPareto> executeLink(
			Triple<IvMModel, IvMLogInfo, IvMLogInfo> input, IvMCanceller canceller) throws Exception {
		return Pair.of(new ProcessDifferencesImpl(input.getA(), input.getB(), input.getC()),
				new ProcessDifferencesParetoImpl(input.getA(), input.getB(), input.getC()));
	}

	protected void processResult(Pair<ProcessDifferences, ProcessDifferencesPareto> result, CohortsState state) {
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