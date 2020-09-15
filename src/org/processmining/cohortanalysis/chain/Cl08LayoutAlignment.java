package org.processmining.cohortanalysis.chain;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.cohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.graphviz.visualisation.DotPanelUserSettings;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationData;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationDataImplEmpty;
import org.processmining.plugins.inductiveVisualMiner.alignment.LogMovePosition;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.mode.ModeRelativePaths;
import org.processmining.plugins.inductiveVisualMiner.traceview.TraceViewEventColourMap;
import org.processmining.plugins.inductiveVisualMiner.visualisation.DfmVisualisation;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisation;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationParameters;

import com.kitfox.svg.SVGDiagram;

public class Cl08LayoutAlignment extends
		LousyCohortsChainLink<Quadruple<IvMModel, IvMLogInfo, ProcessTreeVisualisationParameters, DotPanelUserSettings>, Triple<Dot, SVGDiagram, ProcessTreeVisualisationInfo>> {

	protected Quadruple<IvMModel, IvMLogInfo, ProcessTreeVisualisationParameters, DotPanelUserSettings> generateInput(
			LousyCohortsState state) {
		ProcessTreeVisualisationParameters visualisationParameters = new ModeRelativePaths()
				.getFinalVisualisationParameters(null);
		return Quadruple.of(state.getModel(), state.getCohortLogInfo(), visualisationParameters,
				state.getGraphUserSettings());
	}

	protected Triple<Dot, SVGDiagram, ProcessTreeVisualisationInfo> executeLink(
			Quadruple<IvMModel, IvMLogInfo, ProcessTreeVisualisationParameters, DotPanelUserSettings> input,
			IvMCanceller canceller) throws UnknownTreeNodeException {
		IvMModel model = input.getA();

		//compute dot
		AlignedLogVisualisationData data = new AlignedLogVisualisationDataImplDummy();
		Triple<Dot, ProcessTreeVisualisationInfo, TraceViewEventColourMap> p;
		if (model.isTree()) {
			ProcessTreeVisualisation visualiser = new ProcessTreeVisualisation();
			p = visualiser.fancy(model, data, input.getC());
		} else {
			DfmVisualisation visualiser = new DfmVisualisation();
			p = visualiser.fancy(model, data, input.getC());
		}

		//keep the user settings of the dot panel
		input.getD().applyToDot(p.getA());

		//compute svg from dot
		SVGDiagram diagramCohort = DotPanel.dot2svg(p.getA());

		return Triple.of(p.getA(), diagramCohort, p.getB());
	}

	protected void processResult(Triple<Dot, SVGDiagram, ProcessTreeVisualisationInfo> result,
			LousyCohortsState state) {
		state.setLayoutCohort(result.getA(), result.getB(), result.getC());
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setLayoutCohort(null, null, null);
	}

	public String getName() {
		return "layout alignment";
	}

	public String getStatusBusyMessage() {
		return "Layouting aligned model..";
	}

	public static class AlignedLogVisualisationDataImplDummy implements AlignedLogVisualisationData {

		public Pair<Long, Long> getExtremeCardinalities() {
			return Pair.of(0l, 0l);
		}

		public Triple<String, Long, Long> getNodeLabel(int unode, boolean includeModelMoves) {
			return Triple.of("-.---", 0l, 0l);
		}

		public Pair<String, Long> getModelMoveEdgeLabel(int unode) {
			return Pair.of("-.---", 0l);
		}

		public Pair<String, MultiSet<XEventClass>> getLogMoveEdgeLabel(LogMovePosition logMovePosition) {
			return Pair.of("-.---", new MultiSet<XEventClass>());
		}

		public Pair<String, Long> getEdgeLabel(int unode, boolean includeModelMoves) {
			return Pair.of("-.---", 0l);
		}

		public Pair<String, Long> getEdgeLabel(int from, int to, boolean includeModelMoves) {
			return Pair.of("-.---", 0l);
		}

		public void setTime(long time) {

		}

		public AlignedLogVisualisationDataImplEmpty clone() throws CloneNotSupportedException {
			AlignedLogVisualisationDataImplEmpty c = (AlignedLogVisualisationDataImplEmpty) super.clone();

			return c;
		}
	}
}