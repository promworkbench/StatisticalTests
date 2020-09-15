package org.processmining.lousycohortanalysis.visualisation;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class LousyCohortsPlugin {

	@Plugin(name = "Lousy cohorts", level = PluginLevel.PeerReviewed, returnLabels = {
			"Lousy cohorts launcher" }, returnTypes = { LousyCohortsLauncher.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Discovery,
							PluginCategory.Analytics, PluginCategory.ConformanceChecking }, help = ".")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public LousyCohortsLauncher mineGuiProcessTree(PluginContext context, XLog log) {
		return LousyCohortsLauncher.fromLog(log);
	}

	@Plugin(name = "Lousy cohorts visualisation", returnLabels = { "Visualisation" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Lousy cohort launcher",
					"canceller" }, userAccessible = true, level = PluginLevel.Regular)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Lousy cohorts visualisation", requiredParameterLabels = { 0, 1 })
	public JComponent fancy(PluginContext context, LousyCohortsLauncher launcher, ProMCanceller canceller) {
		return LousyCohortsController.controller(context, launcher.getLog(), canceller);
	}
}