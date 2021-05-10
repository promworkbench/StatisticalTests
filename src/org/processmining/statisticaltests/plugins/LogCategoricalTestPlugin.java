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
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.statisticaltests.CategoricalComparisonResult;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTest;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTestParameters;

public class LogCategoricalTestPlugin {
	@Plugin(name = "Log vs. categorical attribute test", level = PluginLevel.Regular, returnLabels = {
			"Compare sub-logs result" }, returnTypes = { CategoricalComparisonResult.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Analytics,
							PluginCategory.ConformanceChecking }, help = "Compare processes of a categorical attribute using statistal tests. Alpha will be adjusted for multiple tests using the Benjamini-Hochberg method.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public HTMLToString compare(final UIPluginContext context, XLog log) throws InterruptedException {
		LogCategoricalTestDialog dialog = new LogCategoricalTestDialog(log);
		InteractionResult result = context.showWizard("Log vs. categorical attribute test", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		LogCategoricalTestParameters parameters = dialog.getParameters();
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		double p = new LogCategoricalTest().test(log, parameters, canceller);

		if (canceller.isCancelled()) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		String outcome = new LogCategoricalTest().rejectHypothesisForSingleTest(parameters, p)
				? "reject null-hypothesis that the sub-logs defined by the categorical attribute are derived from identical processes"
				: "do not reject null-hypothesis that the sub-logs defined by the categorical attribute are derived from identical processes";

		final StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tr><td>Number of samples</td><td>" + parameters.getNumberOfSamples() + "</td></tr>");
		sb.append("<tr><td> </td><td></td></tr>");
		sb.append("<tr><td>alpha</td><td>" + parameters.getAlpha() + "</td></tr>");
		sb.append("<tr><td>p value</td><td>" + p + "</td></tr>");
		sb.append("<tr><td>result</td><td>" + outcome + "</td></tr>");
		sb.append("</table>");

		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return sb.toString();
			}
		};
	}

}