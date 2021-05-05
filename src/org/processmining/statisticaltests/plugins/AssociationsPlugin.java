package org.processmining.statisticaltests.plugins;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Collection;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.Correlation;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;
import org.processmining.statisticaltests.association.AssociationParametersAbstract;
import org.processmining.statisticaltests.association.AssociationParametersCategoricalAbstract;
import org.processmining.statisticaltests.association.AssociationParametersCategoricalDefault;
import org.processmining.statisticaltests.association.AssociationParametersDefault;
import org.processmining.statisticaltests.association.AssociationProcessCategorical;
import org.processmining.statisticaltests.association.AssociationProcessNumerical;
import org.processmining.statisticaltests.association.Associations;
import org.processmining.statisticaltests.association.AssociationsParameters;
import org.processmining.statisticaltests.association.CorrelationPlot;

public class AssociationsPlugin {
	@Plugin(name = "Compute association/correlation between the process and trace attributes", level = PluginLevel.Regular, returnLabels = {
			"Association result" }, returnTypes = { Associations.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Analytics,
							PluginCategory.ConformanceChecking }, help = "Compute the association and correlation of process behaviour and trace attributes.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public Associations mineGuiProcessTree(final UIPluginContext context, XLog log) throws InterruptedException {
		AssociationsDialog dialog = new AssociationsDialog(log);
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
		}, context.getProgress());
	}

	/**
	 * 
	 * @param log
	 * @param parameters
	 * @param canceller
	 * @param progress
	 *            give NULL if not present; not required.
	 * @return
	 * @throws InterruptedException
	 */
	public static Associations compute(XLog log, AssociationsParameters parameters, ProMCanceller canceller,
			Progress progress) throws InterruptedException {
		//gather attributes
		Collection<Attribute> attributes = new AttributesInfoImpl(log).getTraceAttributes();
		CorrelationPlot plot = new CorrelationPlot();
		plot.setSizeY2DPlot(150);
		Associations result = new Associations(attributes);

		if (progress != null) {
			progress.setMinimum(0);
			progress.setMaximum(attributes.size());
		}

		for (Attribute attribute : attributes) {

			if (progress != null) {
				progress.setCaption("Computing association of " + attribute);
			}

			if (attribute.isDuration() || attribute.isNumeric() || attribute.isTime()) {
				//numerical
				Pair<Double, BufferedImage> p = computeNumericCorrelation(log, attribute, parameters, plot, canceller);
				if (p != null) {
					result.setAssociation(attribute, p);
				} else {
					result.setAssociation(attribute, Pair.of(Double.NaN, null));
				}
			} else if (attribute.isLiteral()) {
				double correlation = computeCategoricalCorrelation(log, attribute, parameters, canceller);
				result.setAssociation(attribute, Pair.of(correlation, null));
			}

			if (progress != null) {
				progress.setValue(progress.getValue() + 1);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param log
	 * @param attribute
	 * @param parameters
	 * @param plot
	 * @param canceller
	 * @return pair of correlation and correlation plot, or null if the
	 *         correlation does not exist
	 * @throws InterruptedException
	 */
	public static Pair<Double, BufferedImage> computeNumericCorrelation(XLog log, Attribute attribute,
			AssociationsParameters parameters, CorrelationPlot plot, ProMCanceller canceller)
			throws InterruptedException {
		AssociationParametersAbstract parametersc = new AssociationParametersDefault(attribute);
		double[][] result = AssociationProcessNumerical.compute(parametersc, log, canceller);

		if (result == null) {
			return null;
		}

		double[] x = result[0];
		double[] y = result[1];
		BigDecimal meanY = Correlation.mean(y);
		if (meanY == null) {
			return null;
		}

		double standardDeviationYd = Correlation.standardDeviation(y, meanY);
		if (!Correlation.isValid(standardDeviationYd)) {
			return null;
		}

		double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

		BufferedImage image = plot.create("Δ " + attribute.getName(), x, "Δ trace", y);

		return Pair.of(correlation, image);
	}

	/**
	 * 
	 * @param log
	 * @param attribute
	 * @param parameters
	 * @param canceller
	 * @return the correlation, or Double.NaN if it does not exist.
	 * @throws InterruptedException
	 */
	public static double computeCategoricalCorrelation(XLog log, Attribute attribute, AssociationsParameters parameters,
			ProMCanceller canceller) throws InterruptedException {
		AssociationParametersCategoricalAbstract parametersc = new AssociationParametersCategoricalDefault(attribute);

		double[][] result = AssociationProcessCategorical.compute(parametersc, log, canceller);

		if (result == null) {
			return Double.NaN;
		}

		double[] x = result[0];
		double[] y = result[1];
		BigDecimal meanY = Correlation.mean(y);
		if (meanY == null) {
			return Double.NaN;
		}

		double standardDeviationYd = Correlation.standardDeviation(y, meanY);
		if (!Correlation.isValid(standardDeviationYd)) {
			return Double.NaN;
		}

		double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

		return correlation;
	}
}