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
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.statisticaltests.StatisticalTest;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTest;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTestParameters;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

@Plugin(name = "Model vs. model - log test", returnLabels = { "Statistical significance" }, returnTypes = {
		HTMLToString.class }, parameterLabels = { "Model A", "Model B", "Log" }, userAccessible = true, categories = {
				PluginCategory.Analytics }, help = "Perform a statistical test as to whether the models represent the log equally well.")
public class ModelModelLogTestPlugin {
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "test", requiredParameterLabels = { 0, 1, 2 })
	public HTMLToString test(final UIPluginContext context, StochasticLabelledPetriNet netA,
			StochasticLabelledPetriNet netB, XLog log) throws Exception {

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		ModelModelLogTestDialog dialog = new ModelModelLogTestDialog(log);
		InteractionResult result = context.showWizard("Log vs. log - unknown process test", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		HTMLToString hresult = test(dialog.getParameters(), netA, netB, log, canceller, context.getProgress());

		if (hresult == null) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		return hresult;
	}

	public static HTMLToString test(ModelModelLogTestParameters parameters, StochasticLabelledPetriNet netA,
			StochasticLabelledPetriNet netB, XLog log, ProMCanceller canceller, Progress progress)
			throws InterruptedException {
		StatisticalTest<Triple<StochasticLabelledPetriNet, StochasticLabelledPetriNet, XLog>, ModelModelLogTestParameters> test = new ModelModelLogTest();

		double p = test.test(Triple.of(netA, netB, log), parameters, canceller, progress);

		if (canceller.isCancelled()) {
			return null;
		}

		String outcome = test.rejectHypothesisForSingleTest(parameters, p)
				? "reject null-hypothesis that the models represent the log equally well"
				: "do not reject null-hypothesis that the models represent the log equally well";

		final StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		sb.append("<tr><td>Number of samples</td><td>" + parameters.getNumberOfSamples() + "</td></tr>");
		sb.append("<tr><td>Sample size</td><td>" + parameters.getSampleSize() + "</td></tr>");
		sb.append("<tr><td>alpha</td><td>" + parameters.getAlpha() + "</td></tr>");
		sb.append("<tr><td>p value</td><td>" + p + "</td></tr>");
		sb.append("<tr><td> </td><td></td></tr>");
		sb.append("<tr><td>result</td><td>" + outcome + "</td></tr>");
		sb.append("</table>");

		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return sb.toString();
			}
		};
	}
}
