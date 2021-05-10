package org.processmining.statisticaltests.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.statisticaltests.CategoricalComparisonResult;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalPairwiseTest;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalPairwiseTestParameters;

public class LogCategoricalPairwiseTestPlugin {
	@Plugin(name = "Log vs. categorical attribute test", level = PluginLevel.Regular, returnLabels = {
			"Compare sub-logs result" }, returnTypes = { CategoricalComparisonResult.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Analytics,
							PluginCategory.ConformanceChecking }, help = "Compare processes of a categorical attribute using statistal tests, for all pairs of values of the categorical attribute. Alpha will be adjusted for multiple tests using the Benjamini-Hochberg method.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public CategoricalComparisonResult compare(final UIPluginContext context, XLog log) throws InterruptedException {
		LogCategoricalPairwiseTestDialog dialog = new LogCategoricalPairwiseTestDialog(log);
		InteractionResult result = context.showWizard("Log vs. categorical attribute test", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		LogCategoricalPairwiseTestParameters parameters = dialog.getParameters();

		return LogCategoricalPairwiseTest.computePairWise(log, parameters, new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		}, context.getProgress());
	}

}
