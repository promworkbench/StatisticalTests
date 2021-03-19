package org.processmining.cohortanalysis.visualisation;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;

/**
 * Constructs and visualises a directly follows graph.
 * 
 * @author sander
 *
 */
public class CohortsController {

	public static CohortsPanel controller(PluginContext context, XLog log, ProMCanceller globalCanceller) {

		CohortsPanel panel = new CohortsPanel();
		return panel;

		//state.setGraphUserSettings(panel.getCohortGraph().getUserSettings());
		//state.getGraphUserSettings().setDirection(GraphDirection.leftRight);

//		//chain
//		DataState state = new DataState();
//		InductiveVisualMinerConfiguration configuration;
//		DataChain chain = new DataChainImplNonBlocking(state, globalCanceller, context.getExecutor(), configuration,
//				panel);
//		{
//
//			chain.register(new Cl01GatherAttributes());
//			chain.register(new Cl05Mine());
//			chain.register(new Cl07Align());
//
//			Cl08LayoutAlignment layoutAlignment = new Cl08LayoutAlignment();
//			{
//				layoutAlignment.setOnComplete(new Runnable() {
//					public void run() {
//						panel.getCohortGraph().changeDot(state.getDotCohort(), state.getSVGDiagramCohort(), true);
//					}
//				});
//				chain.addConnection(alignment, layoutAlignment);
//			}
//
//			Cl08LayoutAlignmentAnti layoutAlignmentAnti = new Cl08LayoutAlignmentAnti();
//			{
//				layoutAlignmentAnti.setOnComplete(new Runnable() {
//					public void run() {
//						panel.getAntiCohortGraph().changeDot(state.getDotAntiCohort(), state.getSVGDiagramAntiCohort(),
//								true);
//					}
//				});
//				chain.addConnection(alignment, layoutAlignmentAnti);
//			}
//
//			Cl17DataAnalysisCohort cohortAnalysis = new Cl17DataAnalysisCohort();
//			{
//				cohortAnalysis.setOnComplete(new Runnable() {
//					public void run() {
//						panel.setCohorts(state.getCohorts());
//						panel.getCohortsList().getSelectionModel().setSelectionInterval(0, 0);
//						state.setSelectedCohort(state.getCohorts()
//								.get(panel.getCohortsList().getSelectionModel().getAnchorSelectionIndex()));
//						panel.getCohortsList().repaint();
//					}
//				});
//				cohortAnalysis.setOnInvalidate(new Runnable() {
//					public void run() {
//						panel.setCohorts(null);
//						panel.getCohortsList().repaint();
//					}
//				});
//				chain.addConnection(gatherAttributes, cohortAnalysis);
//			}
//
//			Cl18ApplyCohort applyCohort = new Cl18ApplyCohort();
//			{
//				applyCohort.setOnComplete(new Runnable() {
//					public void run() {
//						ProcessTreeVisualisationParameters visualisationParameters = new ModeRelativePaths()
//								.getVisualisationParametersWithAlignments(null);
//						{
//							AlignedLogVisualisationData cohortVisualisationData = new ModeRelativePaths()
//									.getVisualisationData(state.getModel(), state.getCohortLog(),
//											state.getCohortLogInfo(), null, null);
//							InductiveVisualMinerSelectionColourer.colourHighlighting(panel.getCohortGraph().getSVG(),
//									state.getVisualisationInfoCohort(), state.getModel(), cohortVisualisationData,
//									visualisationParameters);
//							panel.getCohortGraph().repaint();
//						}
//
//						{
//							AlignedLogVisualisationData antiCohortVisualisationData = new ModeRelativePaths()
//									.getVisualisationData(state.getModel(), state.getAntiCohortLog(),
//											state.getAntiCohortLogInfo(), null, null);
//							InductiveVisualMinerSelectionColourer.colourHighlighting(
//									panel.getAntiCohortGraph().getSVG(), state.getVisualisationInfoAntiCohort(),
//									state.getModel(), antiCohortVisualisationData, visualisationParameters);
//							panel.getAntiCohortGraph().repaint();
//						}
//					}
//				});
//				chain.addConnection(layoutAlignment, applyCohort);
//				chain.addConnection(layoutAlignmentAnti, applyCohort);
//				chain.addConnection(cohortAnalysis, applyCohort);
//			}
//
//			Cl19ProcessDifferences differences = new Cl19ProcessDifferences();
//			{
//				differences.setOnComplete(new Runnable() {
//					public void run() {
//						panel.setProcessDifferences(state.getProcessDifferences());
//						panel.getProcessDifferencesPareto().repaint();
//					}
//				});
//				differences.setOnInvalidate(new Runnable() {
//					public void run() {
//						panel.setProcessDifferences(null);
//						panel.getProcessDifferencesPareto().repaint();
//					}
//				});
//				chain.addConnection(applyCohort, differences);
//			}
//		}
//
//		//respond to cohort selection
//		panel.getCohortsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent e) {
//				if (!e.getValueIsAdjusting()) {
//					//update selection
//					state.setSelectedCohort(state.getCohorts()
//							.get(panel.getCohortsList().getSelectionModel().getAnchorSelectionIndex()));
//					panel.getCohortLabel().setText("<html>Cohort ("
//							+ Features2String.toString(state.getSelectedCohort().getFeatures()) + ")</html>");
//					chain.execute(Cl18ApplyCohort.class);
//					panel.repaint();
//				}
//			}
//		});
//
//		//respond to difference selection
//		panel.getProcessDifferences().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent e) {
//				if (!e.getValueIsAdjusting()) {
//					int internalIndex = panel.getProcessDifferences().getSelectionModel().getAnchorSelectionIndex();
//					if (internalIndex >= 0) {
//						int selectedIndex = state.getProcessDifferences().getA().row2index(internalIndex);
//						panel.getProcessDifferencesPareto().setSelectedIndex(selectedIndex);
//					}
//					panel.repaint();
//				}
//			}
//		});
//
//		return panel;
	}
}