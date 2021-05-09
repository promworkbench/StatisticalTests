package org.processmining.statisticaltests.plugins;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.CategoricalComparisonResult;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTestParameters;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTest;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTestParameters;

public class LogCategoricalTestPlugin {
	@Plugin(name = "TODO Log vs. categorical attribute test", level = PluginLevel.Regular, returnLabels = {
			"Compare sub-logs result" }, returnTypes = { CategoricalComparisonResult.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Analytics,
							PluginCategory.ConformanceChecking }, help = "Compare processes of a categorical attribute using statistal tests. Alpha will be adjusted for multiple tests using the Benjamini-Hochberg method.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public CategoricalComparisonResult compare(final UIPluginContext context, XLog log) throws InterruptedException {
		LogCategoricalTestDialog dialog = new LogCategoricalTestDialog(log);
		InteractionResult result = context.showWizard("Log vs. categorical attribute test", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		LogCategoricalTestParameters parameters = dialog.getParameters();

		return null;
	}

	public static CategoricalComparisonResult computeOneVsOther(XLog log, Attribute attribute,
			LogLogUnknownProcessTestParameters parameters, ProMCanceller canceller, Progress progress)
			throws InterruptedException {
		XFactory factory = new XFactoryNaiveImpl();

		if (progress != null) {
			progress.setMinimum(0);
			progress.setMaximum(attribute.getStringValues().size());
		}

		List<Triple<Double, String, String>> result = new ArrayList<>();

		for (String value : attribute.getStringValues()) {
			//create two logs
			XLog logA = factory.createLog();
			XLog logB = factory.createLog();

			for (XTrace trace : log) {
				if (attribute.getLiteral(trace) != null && attribute.getLiteral(trace).equals(value)) {
					logA.add(trace);
				} else {
					logB.add(trace);
				}
			}

			if (logA.isEmpty() || logB.isEmpty()) {
				result.add(Triple.of(Double.NaN, value, "[others]"));
			} else {
				double p = new LogLogUnknownProcessTest().test(Pair.of(logA, logB), parameters, canceller);

				if (canceller.isCancelled()) {
					return null;
				}

				result.add(Triple.of(p, value, "[others]"));
			}

			if (canceller.isCancelled()) {
				return null;
			}

			if (progress != null) {
				progress.setValue(progress.getValue() + 1);
			}
		}

		if (canceller.isCancelled()) {
			return null;
		}

		//apply Benjamini-Hochberg
		List<Quadruple<Double, Boolean, String, String>> r = StatisticalTestUtils.benjaminiHochberg(result,
				parameters.getAlpha());

		if (canceller.isCancelled()) {
			return null;
		}

		return new CategoricalComparisonResult(attribute, r, parameters.getAlpha());
	}

}