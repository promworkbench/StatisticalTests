package org.processmining.lousycohortanalysis.visualisation;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.feature.Features2String;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.lousycohortanalysis.chain.Cl01GatherAttributes;
import org.processmining.lousycohortanalysis.chain.Cl05Mine;
import org.processmining.lousycohortanalysis.chain.Cl07Align;
import org.processmining.lousycohortanalysis.chain.Cl08LayoutAlignment;
import org.processmining.lousycohortanalysis.chain.Cl08LayoutAlignmentAnti;
import org.processmining.lousycohortanalysis.chain.Cl17DataAnalysisCohort;
import org.processmining.lousycohortanalysis.chain.Cl18ApplyCohort;
import org.processmining.lousycohortanalysis.chain.Cl19Done;
import org.processmining.lousycohortanalysis.chain.Cl19ProcessDifferences;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.inductiveVisualMiner.InductiveVisualMinerSelectionColourer;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationData;
import org.processmining.plugins.inductiveVisualMiner.chain.Chain;
import org.processmining.plugins.inductiveVisualMiner.mode.ModeRelativePaths;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationParameters;

/**
 * Constructs and visualises a directly follows graph.
 * 
 * @author sander
 *
 */
public class LousyCohortsController {

	public static LousyCohortsPanel controller(PluginContext context, XLog log, ProMCanceller globalCanceller) {

		LousyCohortsState state = new LousyCohortsState(log);
		LousyCohortsPanel panel = new LousyCohortsPanel();

		state.setGraphUserSettings(panel.getCohortGraph().getUserSettings());
		state.getGraphUserSettings().setDirection(GraphDirection.leftRight);

		//chain
		Chain<LousyCohortsState> chain = new Chain<>(state, globalCanceller, context.getExecutor());
		{

			chain.setOnChange(new Runnable() {
				public void run() {

				}
			});

			Cl01GatherAttributes gatherAttributes = new Cl01GatherAttributes();

			Cl05Mine mine = new Cl05Mine();
			{
				chain.addConnection(gatherAttributes, mine);
			}

			Cl07Align alignment = new Cl07Align();
			{
				chain.addConnection(mine, alignment);
			}

			Cl08LayoutAlignment layoutAlignment = new Cl08LayoutAlignment();
			{
				layoutAlignment.setOnComplete(new Runnable() {
					public void run() {
						panel.getCohortGraph().changeDot(state.getDotCohort(), state.getSVGDiagramCohort(), true);
					}
				});
				chain.addConnection(alignment, layoutAlignment);
			}

			Cl08LayoutAlignmentAnti layoutAlignmentAnti = new Cl08LayoutAlignmentAnti();
			{
				layoutAlignmentAnti.setOnComplete(new Runnable() {
					public void run() {
						panel.getAntiCohortGraph().changeDot(state.getDotAntiCohort(), state.getSVGDiagramAntiCohort(),
								true);
					}
				});
				chain.addConnection(alignment, layoutAlignmentAnti);
			}

			Cl17DataAnalysisCohort cohortAnalysis = new Cl17DataAnalysisCohort();
			{
				cohortAnalysis.setOnComplete(new Runnable() {
					public void run() {
						panel.setCohorts(state.getCohorts());
						panel.getCohortsList().getSelectionModel().setSelectionInterval(0, 0);
						state.setSelectedCohort(state.getCohorts()
								.get(panel.getCohortsList().getSelectionModel().getAnchorSelectionIndex()));
						panel.getCohortsList().repaint();
					}
				});
				cohortAnalysis.setOnInvalidate(new Runnable() {
					public void run() {
						panel.setCohorts(null);
						panel.getCohortsList().repaint();
					}
				});
				chain.addConnection(gatherAttributes, cohortAnalysis);
			}

			Cl18ApplyCohort applyCohort = new Cl18ApplyCohort();
			{
				applyCohort.setOnComplete(new Runnable() {
					public void run() {
						ProcessTreeVisualisationParameters visualisationParameters = new ModeRelativePaths()
								.getFinalVisualisationParameters(null);
						{
							AlignedLogVisualisationData cohortVisualisationData = new ModeRelativePaths()
									.getVisualisationData(state.getModel(), state.getCohortLog(),
											state.getCohortLogInfo(), null, null);
							InductiveVisualMinerSelectionColourer.colourHighlighting(panel.getCohortGraph().getSVG(),
									state.getVisualisationInfoCohort(), state.getModel(), cohortVisualisationData,
									visualisationParameters);
							panel.getCohortGraph().repaint();
						}

						{
							AlignedLogVisualisationData antiCohortVisualisationData = new ModeRelativePaths()
									.getVisualisationData(state.getModel(), state.getAntiCohortLog(),
											state.getAntiCohortLogInfo(), null, null);
							InductiveVisualMinerSelectionColourer.colourHighlighting(
									panel.getAntiCohortGraph().getSVG(), state.getVisualisationInfoAntiCohort(),
									state.getModel(), antiCohortVisualisationData, visualisationParameters);
							panel.getAntiCohortGraph().repaint();
						}
					}
				});
				chain.addConnection(layoutAlignment, applyCohort);
				chain.addConnection(layoutAlignmentAnti, applyCohort);
				chain.addConnection(cohortAnalysis, applyCohort);
			}

			Cl19ProcessDifferences differences = new Cl19ProcessDifferences();
			{
				differences.setOnComplete(new Runnable() {
					public void run() {
						panel.setProcessDifferences(state.getProcessDifferences());
						panel.getProcessDifferences().repaint();
					}
				});
				differences.setOnInvalidate(new Runnable() {
					public void run() {
						panel.setProcessDifferences(null);
						panel.getProcessDifferences().repaint();
					}
				});
				chain.addConnection(applyCohort, differences);
			}

			Cl19Done done = new Cl19Done();
			{
				chain.addConnection(differences, done);
			}

			//start the chain
			chain.execute(Cl01GatherAttributes.class);
		}

		//respond to cohort selection
		panel.getCohortsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					//update selection
					state.setSelectedCohort(state.getCohorts()
							.get(panel.getCohortsList().getSelectionModel().getAnchorSelectionIndex()));
					panel.getCohortLabel().setText(
							"<html>Cohort (" + Features2String.toString(state.getSelectedCohort().getFeatures()) + ")</html>");
					chain.execute(Cl18ApplyCohort.class);
					panel.repaint();
				}
			}
		});

		return panel;
	}
}