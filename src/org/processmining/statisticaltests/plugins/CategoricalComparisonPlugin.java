package org.processmining.statisticaltests.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.CategoricalComparisonParameters;
import org.processmining.statisticaltests.CategoricalComparisonResult;
import org.processmining.statisticaltests.LogLogUnknownProcessTest;
import org.processmining.statisticaltests.ParametersDefault;

public class CategoricalComparisonPlugin {
	@Plugin(name = "Compare sub-logs defined by categorical attribute", level = PluginLevel.Regular, returnLabels = {
			"Association result" }, returnTypes = { CategoricalComparisonResult.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Analytics,
							PluginCategory.ConformanceChecking }, help = "Compare processes of categorical attribute using statistal tests.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public CategoricalComparisonResult compare(final UIPluginContext context, XLog log) throws InterruptedException {
		CategoricalComparisonDialog dialog = new CategoricalComparisonDialog(log);
		InteractionResult result = context.showWizard("Categorical attribute-process comparison", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		CategoricalComparisonParameters parameters = dialog.getParameters();
		return compute(log, parameters, new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		}, context.getProgress());
	}

	public static CategoricalComparisonResult compute(XLog log, CategoricalComparisonParameters parameters,
			ProMCanceller canceller, Progress progress) throws InterruptedException {
		Attribute attribute = parameters.getAttribute();
		XFactory factory = new XFactoryNaiveImpl();

		if (progress != null) {
			progress.setMinimum(0);
			progress.setMaximum(attribute.getStringValues().size());
		}

		ParametersDefault pParameters = new ParametersDefault();

		List<Pair<Double, String>> result = new ArrayList<>();

		for (String value : attribute.getStringValues()) {
			//create two logs
			XLog logA = factory.createLog();
			XLog logB = factory.createLog();

			for (XTrace trace : log) {
				if (attribute.getLiteral(trace).equals(value)) {
					logA.add(trace);
				} else {
					logB.add(trace);
				}
			}

			double p = LogLogUnknownProcessTest.p(logA, logB, pParameters, canceller);
			result.add(Pair.of(p, value));

			if (progress != null) {
				progress.setValue(progress.getValue() + 1);
			}
		}

		//apply Benjamini-Hochberg
		List<Triple<Double, Boolean, String>> r = benjaminiHochberg(result, parameters.getAlpha());

		return new CategoricalComparisonResult(attribute, r, parameters.getAlpha());
	}

	/**
	 * 
	 * @param <X>
	 * @param <X>
	 * @param values
	 * @return which hypotheses are rejected
	 */
	public static <X> List<Triple<Double, Boolean, X>> benjaminiHochberg(List<Pair<Double, X>> values, double alpha) {
		Collections.sort(values, new Comparator<Pair<Double, X>>() {
			public int compare(Pair<Double, X> o1, Pair<Double, X> o2) {
				return o1.getA().compareTo(o2.getA());
			}
		});
		Collections.reverse(values);

		boolean[] rejected = new boolean[values.size()];

		int r = 0;
		int x = values.size();

		while (r < values.size() && 1 - values.get(r).getA() < alpha * (r + 1.0) / x) {
			rejected[r] = true;
			r = r + 1;
		}

		//post-processing: reject all hypotheses up to the last one
		boolean rejecting = false;
		for (int i = rejected.length - 1; i >= 0; i--) {
			if (rejecting || rejected[i]) {
				rejecting = true;
				rejected[i] = true;
			}
		}

		List<Triple<Double, Boolean, X>> result = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			result.add(Triple.of(values.get(i).getA(), rejected[i], values.get(i).getB()));
		}
		return result;
	}
}