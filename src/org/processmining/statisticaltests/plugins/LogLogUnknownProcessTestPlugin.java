package org.processmining.statisticaltests.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTest;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTestParameters;

@Plugin(name = "Log vs. log - unknown process test", returnLabels = { "Statistical significance" }, returnTypes = {
		HTMLToString.class }, parameterLabels = { "Log A", "Log B" }, userAccessible = true, categories = {
				PluginCategory.Analytics }, help = "Perform a statistical test as to whether the logs were derived from the same process.")
public class LogLogUnknownProcessTestPlugin {
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "test", requiredParameterLabels = { 0, 1 })
	public HTMLToString test(final UIPluginContext context, XLog logA, XLog logB, Progress progress) throws Exception {

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		LogLogUnknownProcessTestDialog dialog = new LogLogUnknownProcessTestDialog(logA, logB);
		InteractionResult result = context.showWizard("Log vs. log - unknown process test", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		LogLogUnknownProcessTestParameters parameters = dialog.getParameters();

		double p = new LogLogUnknownProcessTest().test(Pair.of(logA, logB), parameters, canceller, progress);

		if (canceller.isCancelled()) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		String outcome = new LogLogUnknownProcessTest().rejectHypothesisForSingleTest(parameters, p)
				? "reject null-hypothesis that the logs were derived from the same process"
				: "do not reject null-hypothesis that the logs were derived from the same process";

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
