package org.processmining.statisticaltests.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quintuple;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.statisticaltests.StatisticalTest;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTest;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTestParameters;

@Plugin(name = "Model vs. model - log test", returnLabels = { "Statistical significance" }, returnTypes = {
		HTMLToString.class }, parameterLabels = { "Model A", "Model B", "Log" }, userAccessible = true, categories = {
				PluginCategory.Analytics }, help = "Perform a statistical test as to whether the models represent the log equally well.")
public class ModelModelLogTestPlugin {
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "test", requiredParameterLabels = { 0, 1, 2 })
	public HTMLToString test(final UIPluginContext context, StochasticNet netA, StochasticNet netB, XLog log)
			throws Exception {

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

		Marking markingA = EarthMoversStochasticConformancePlugin.getInitialMarking(netA);
		Marking markingB = EarthMoversStochasticConformancePlugin.getInitialMarking(netB);

		HTMLToString hresult = test(dialog.getParameters(), netA, markingA, netB, markingB, log, canceller);

		if (hresult == null) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		return hresult;
	}

	public static HTMLToString test(ModelModelLogTestParameters parameters, StochasticNet netA, Marking markingA,
			StochasticNet netB, Marking markingB, XLog log, ProMCanceller canceller) throws InterruptedException {
		StatisticalTest<Quintuple<StochasticNet, Marking, StochasticNet, Marking, XLog>, ModelModelLogTestParameters> test = new ModelModelLogTest();

		double p = test.test(Quintuple.of(netA, markingA, netB, markingB, log), parameters, canceller);

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
