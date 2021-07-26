package org.processmining.cohortanalysis.visualisation;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.chain.Cl01GatherAttributes;
import org.processmining.cohortanalysis.chain.Cl05Mine;
import org.processmining.cohortanalysis.chain.CohortsConfiguration;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.inductiveVisualMiner.chain.Cl02SortEvents;
import org.processmining.plugins.inductiveVisualMiner.chain.Cl03MakeLog;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainImplNonBlocking;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.DfgMiner;

/**
 * Constructs and visualises a directly follows graph.
 * 
 * @author sander
 *
 */
public class CohortsController {

	private final CohortsPanel panel;
	private final DataChainImplNonBlocking<CohortsConfiguration, CohortsPanel> chain;
	private final Cl02SortEvents sortEvents;

	public CohortsController(PluginContext context, XLog log, ProMCanceller canceller) {
		DataState state = new DataState();
		CohortsConfiguration configuration = new CohortsConfiguration();
		panel = new CohortsPanel();

		chain = new DataChainImplNonBlocking<>(state,

				canceller, context.getExecutor(), configuration, getPanel());

		initGui(canceller, configuration);

		//set up the controller view
		chain.setOnChange(new Runnable() {
			public void run() {
				getPanel().getControllerView().pushCompleteChainLinks(chain);
			}
		});

		//state.setGraphUserSettings(panel.getCohortGraph().getUserSettings());
		//state.getGraphUserSettings().setDirection(GraphDirection.leftRight);

		//chain
		chain.register(new Cl01GatherAttributes());

		{
			sortEvents = new Cl02SortEvents();
			chain.register(sortEvents);
			sortEvents.setOnIllogicalTimeStamps(new Function<Object, Boolean>() {
				public Boolean call(Object input) throws Exception {
					String[] options = new String[] { "Continue with neither animation nor performance",
							"Reorder events" };
					int n = JOptionPane.showOptionDialog(panel,
							"The event log contains illogical time stamps,\n i.e. some time stamps contradict the order of events.\n\nInductive visual Miner can reorder the events and discover a new model.\nWould you like to do that?", //message
							"Illogical Time Stamps", //title
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, //do not use a custom Icon
							options, //the titles of buttons
							options[0]); //default button title
					if (n == 1) {
						//the user requested to reorder the events
						return true;
					}
					return false;
				}
			});
		}

		chain.register(new Cl03MakeLog());
		chain.register(new Cl05Mine());

		//		chain.register(new Cl07Align());
		//
		//		Cl08LayoutAlignment layoutAlignment = new Cl08LayoutAlignment();
		//		{
		//			layoutAlignment.setOnComplete(new Runnable() {
		//				public void run() {
		//					panel.getCohortGraph().changeDot(state.getDotCohort(), state.getSVGDiagramCohort(), true);
		//				}
		//			});
		//			chain.addConnection(alignment, layoutAlignment);
		//		}

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

		//start the chain
		chain.setFixedObject(IvMObject.selected_noise_threshold, 0.8);
		chain.setFixedObject(IvMObject.selected_miner, new DfgMiner());
		chain.setFixedObject(IvMObject.input_log, log);
	}

	protected void initGui(final ProMCanceller canceller, CohortsConfiguration configuration) {
		//listen to ctrl c to show the controller view
		{
			panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), "showControllerView"); // - key
			panel.getActionMap().put("showControllerView", new AbstractAction() {
				private static final long serialVersionUID = 1727407514105090094L;

				public void actionPerformed(ActionEvent arg0) {
					panel.getControllerView().setVisible(true);
					chain.getOnChange().run();
				}

			});
		}
	}

	public CohortsPanel getPanel() {
		return panel;
	}
}