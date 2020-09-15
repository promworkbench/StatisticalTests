package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IteratorWithPosition;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.highlightingfilter.filters.HighlightingFilterCohort;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogFilteredImpl;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogNotFiltered;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMTrace;

public class Cl18ApplyCohort extends
		LousyCohortsChainLink<Triple<IvMLogNotFiltered, Cohort, IvMModel>, Quadruple<IvMLogFilteredImpl, IvMLogInfo, IvMLogFilteredImpl, IvMLogInfo>> {

	protected Triple<IvMLogNotFiltered, Cohort, IvMModel> generateInput(LousyCohortsState state) {
		return Triple.of(state.getIvMLog(), state.getSelectedCohort(), state.getModel());
	}

	protected Quadruple<IvMLogFilteredImpl, IvMLogInfo, IvMLogFilteredImpl, IvMLogInfo> executeLink(
			Triple<IvMLogNotFiltered, Cohort, IvMModel> input, IvMCanceller canceller)
			throws CloneNotSupportedException {
		IvMLogNotFiltered log = input.getA();
		Cohort cohort = input.getB();
		IvMModel model = input.getC();

		IvMLogFilteredImpl cohortLog = new IvMLogFilteredImpl(log);
		IteratorWithPosition<IvMTrace> cohortIt = cohortLog.iterator();
		while (cohortIt.hasNext()) {
			IvMTrace trace = cohortIt.next();
			if (!HighlightingFilterCohort.inCohort(trace, cohort)) {
				cohortIt.remove();
			}
		}

		IvMLogFilteredImpl antiCohortLog = cohortLog.clone();
		antiCohortLog.invert();

		//create the log infos
		IvMLogInfo cohortLogInfo = new IvMLogInfo(cohortLog, model);
		IvMLogInfo antiCohortLogInfo = new IvMLogInfo(antiCohortLog, model);

		return Quadruple.of(cohortLog, cohortLogInfo, antiCohortLog, antiCohortLogInfo);
	}

	protected void processResult(Quadruple<IvMLogFilteredImpl, IvMLogInfo, IvMLogFilteredImpl, IvMLogInfo> result,
			LousyCohortsState state) {
		state.setCohortLogs(result.getA(), result.getB(), result.getC(), result.getD());
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setCohortLogs(null, null, null, null);
	}

	public String getName() {
		return "cohort filter";
	}

	public String getStatusBusyMessage() {
		return "Filtering cohort..";
	}
}