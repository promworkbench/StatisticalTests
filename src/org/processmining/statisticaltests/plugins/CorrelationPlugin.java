package org.processmining.statisticaltests.plugins;

import java.math.BigDecimal;
import java.util.Collection;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.correlation.Associations;
import org.processmining.correlation.AssociationsParameters;
import org.processmining.correlation.CorrelationParametersAbstract;
import org.processmining.correlation.CorrelationProcessNumerical;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.Correlation;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;

public class CorrelationPlugin {
	@Plugin(name = "Compute association/correlation between the process and trace attributes", level = PluginLevel.Regular, returnLabels = {
			"Association result" }, returnTypes = { Associations.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Analytics,
							PluginCategory.ConformanceChecking }, help = "Compute the association and correlation of process behaviour and trace attributes.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public Associations mineGuiProcessTree(final UIPluginContext context, XLog log) throws InterruptedException {
		CorrelationDialog dialog = new CorrelationDialog(log);
		InteractionResult result = context.showWizard("Process-Attribute Association & Correlation", true, true,
				dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		AssociationsParameters parameters = dialog.getParameters();
		return compute(log, parameters, new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		});
	}

	public static Associations compute(XLog log, AssociationsParameters parameters, ProMCanceller canceller)
			throws InterruptedException {
		//gather attributes
		Collection<Attribute> attributes = new AttributesInfoImpl(log).getTraceAttributes();
		Associations result = new Associations(attributes);

		for (Attribute attribute : attributes) {
			if (attribute.isDuration() || attribute.isNumeric() || attribute.isTime()) {
				//numerical
				result.setCorrelation(attribute, computeNumericCorrelation(log, attribute, parameters, canceller));
			}
		}

		return result;
	}

	public static double computeNumericCorrelation(XLog log, Attribute attribute, AssociationsParameters parameters,
			ProMCanceller canceller) throws InterruptedException {
		CorrelationParametersAbstract parametersc = new CorrelationParametersAbstract(100000,
				parameters.getClassifier(), attribute, System.currentTimeMillis(), false) {
		};
		double[][] result = CorrelationProcessNumerical.compute(parametersc, log, canceller);

		if (result == null) {
			return Double.NaN;
		}

		double[] x = result[0];
		double[] y = result[1];
		BigDecimal meanY = Correlation.mean(y);
		double standardDeviationYd = Correlation.standardDeviation(y, meanY);
		double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

		return correlation;
	}
}