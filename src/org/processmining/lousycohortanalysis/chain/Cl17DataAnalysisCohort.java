package org.processmining.lousycohortanalysis.chain;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.lousycohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.cohorts.CohortAnalysis;
import org.processmining.plugins.inductiveVisualMiner.performance.XEventPerformanceClassifier;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfo;

public class Cl17DataAnalysisCohort
		extends LousyCohortsChainLink<Triple<AttributesInfo, XLog, XEventPerformanceClassifier>, Cohorts> {

	public String getName() {
		return "cohort analysis";
	}

	protected Triple<AttributesInfo, XLog, XEventPerformanceClassifier> generateInput(LousyCohortsState state) {
		return Triple.of(state.getAttributesInfo(), state.getLog(), state.getPerformanceClassifier());
	}

	protected Cohorts executeLink(Triple<AttributesInfo, XLog, XEventPerformanceClassifier> input,
			IvMCanceller canceller) throws Exception {
		return CohortAnalysis.compute(input.getA(), input.getB(), input.getC(), canceller);
	}

	protected void processResult(Cohorts result, LousyCohortsState state) {
		state.setCohorts(result);
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setCohorts(null);
	}

	public String getStatusBusyMessage() {
		return "Performing cohort analysis";
	}

}