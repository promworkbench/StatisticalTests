package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanelUserSettings;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.mode.ModeRelativePaths;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationParameters;

import com.kitfox.svg.SVGDiagram;

public class Cl08LayoutAlignmentAnti extends Cl08LayoutAlignment {
	@Override
	protected Quadruple<IvMModel, IvMLogInfo, ProcessTreeVisualisationParameters, DotPanelUserSettings> generateInput(
			LousyCohortsState state) {
		ProcessTreeVisualisationParameters visualisationParameters = new ModeRelativePaths()
				.getFinalVisualisationParameters(null);
		return Quadruple.of(state.getModel(), state.getAntiCohortLogInfo(), visualisationParameters,
				state.getGraphUserSettings());
	}

	@Override
	protected void processResult(Triple<Dot, SVGDiagram, ProcessTreeVisualisationInfo> result,
			LousyCohortsState state) {
		state.setLayoutAntiCohort(result.getA(), result.getB(), result.getC());
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setLayoutAntiCohort(null, null, null);
	}
}
