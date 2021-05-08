package org.processmining.statisticaltests;

import java.math.BigDecimal;
import java.util.SplittableRandom;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.distancematrix.DistanceMatrix;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.ReallocationMatrix;
import org.processmining.earthmoversstochasticconformancechecking.reallocationmatrix.epsa.ComputeReallocationMatrix2;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.log.StochasticLanguageLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.statisticaltests.helperclasses.AliasMethod;

@Plugin(name = "Significance test: log-log differs", returnLabels = { "Statistical significance" }, returnTypes = {
		HTMLToString.class }, parameterLabels = { "Log A", "Log B" }, userAccessible = true, categories = {
				PluginCategory.Analytics }, help = "Perform a statistical significance test as to whether the logs are derived from the same process.")
public class LogLogTest {

	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Filter log on life cycle, default", requiredParameterLabels = { 0, 1 })
	public HTMLToString test(final PluginContext context, XLog logA, XLog logB) throws Exception {
		final ParametersAbstract parameters = new ParametersDefault();

		parameters.setNumberOfSamples(10000);
		parameters.setSampleSize(Math.max(logA.size(), logB.size()) / 20);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};
		double p = p(logA, logB, parameters, canceller);

		final StringBuilder result = new StringBuilder();
		result.append("<table>");
		result.append("<tr><td>Sample size</td><td>" + parameters.getSampleSize() + "</td></tr>");
		result.append("<tr><td>Number of samples</td><td>" + parameters.getNumberOfSamples() + "</td></tr>");
		result.append("<tr><td> </td><td></td></tr>");
		result.append("<tr><td>p value</td><td>" + p + "</td></tr>");
		result.append("<tr><td colspan=2>0.5 => equal, 0 or 1 => unequal</td></tr>");
		result.append("</table>");

		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return result.toString();
			}
		};
	}

	public static double p(XLog logA, XLog logB, Parameters parameters, ProMCanceller canceller)
			throws InterruptedException {
		SplittableRandom random = new SplittableRandom(parameters.getSeed());

		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(logA, parameters.getClassifierA());
		activityKey.feed(logB, parameters.getClassifierB());

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		//set up objects for Earth Movers' conformance
		StochasticLanguageLog languageA = XLog2StochasticLanguage.convert(logA, parameters.getClassifierA(),
				activityKey, canceller);
		//System.out.println("language A");
		//System.out.println(StochasticLanguageLog2String.toString(languageA, false));
		StochasticLanguageLog languageB = XLog2StochasticLanguage.convert(logB, parameters.getClassifierB(),
				activityKey, canceller);
		//System.out.println("");
		//System.out.println("language B");
		//System.out.println(StochasticLanguageLog2String.toString(languageB, false));
		EMSCParametersLogLogAbstract emscParameters = new EMSCParametersLogLogDefault();
		emscParameters.setComputeStochasticTraceAlignments(false);
		@SuppressWarnings("unchecked")
		DistanceMatrix<int[], int[]> distanceMatrixAA = EMSCParametersDefault.defaultDistanceMatrix.clone();
		@SuppressWarnings("unchecked")
		DistanceMatrix<int[], int[]> distanceMatrixAB = EMSCParametersDefault.defaultDistanceMatrix.clone();
		distanceMatrixAA.init(languageA, languageA, canceller);
		distanceMatrixAB.init(languageA, languageB, canceller);

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		double[] massKeyA = getMassKeyNormal(languageA);
		double[] massKeyB = getMassKeyNormal(languageB);

		//create sampler methods
		AliasMethod aliasMethodA = new AliasMethod(massKeyA, random);
		AliasMethod aliasMethodB = new AliasMethod(massKeyB, random);

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		int winsA = 0;
		int winsB = 0;

		for (int sample = 0; sample < parameters.getNumberOfSamples(); sample++) {
			//			double[] sampleA = sample(massKeyA, parameters.getSampleSize(), random);
			//			double[] sampleB = sample(massKeyB, parameters.getSampleSize(), random);
			double[] sampleA = sample(aliasMethodA, parameters.getSampleSize());
			double[] sampleB = sample(aliasMethodB, parameters.getSampleSize());

			double distanceA = getSimilarity(languageA, applySample(languageA, sampleA), distanceMatrixAA,
					emscParameters, canceller);
			double distanceB = getSimilarity(languageA, applySample(languageB, sampleB), distanceMatrixAB,
					emscParameters, canceller);
			//			System.out.println("distanceAa " + distanceA + ", distanbeAb " + distanceB);
			if (distanceA < distanceB) {
				winsA++;
			} else if (distanceA > distanceB) {
				winsB++;
			} else {
				winsA++;
				winsB++;
			}

			if (canceller.isCancelled()) {
				return -Double.MAX_VALUE;
			}
		}

		double p = winsA / (winsA + winsB * 1.0);
		return p;
	}

	/**
	 * Cumulative mass key
	 * 
	 * @param language
	 * @return
	 */
	public static double[] getMassKeyCumulative(StochasticLanguageLog language) {
		double[] result = new double[language.size()];
		BigDecimal cumulative = BigDecimal.ZERO;
		StochasticTraceIterator<int[]> it = language.iterator();
		for (int i = 1; i < result.length; i++) {
			it.next();
			cumulative = cumulative.add(new BigDecimal(it.getProbability()));
			result[i] = cumulative.doubleValue();
		}
		return result;
	}

	public static double[] getMassKeyNormal(StochasticLanguageLog language) {
		double[] result = new double[language.size()];
		StochasticTraceIterator<int[]> it = language.iterator();
		for (int i = 0; i < result.length; i++) {
			it.next();
			result[i] = it.getProbability();
		}
		return result;
	}

	public static double getSimilarity(StochasticLanguage<?> languageA, StochasticLanguage<?> languageB,
			DistanceMatrix<?, ?> distanceMatrix, EMSCParameters parameters, ProMCanceller canceller) {
		Pair<ReallocationMatrix, Double> p = ComputeReallocationMatrix2.computeWithDistanceMatrixInitialised(languageA,
				languageB, distanceMatrix, parameters, canceller);
		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		return p.getB();
	}

	public static StochasticLanguageLog applySample(StochasticLanguageLog language, double[] sample) {
		return new StochasticLanguageWrapper(language, sample);
	}

	public static double[] sample(AliasMethod aliasMethod, int sampleSize) {
		double[] result = new double[aliasMethod.getProbabilitiesSize()];
		for (int i = 0; i < sampleSize; i++) {
			result[aliasMethod.next()]++;
		}
		double ss = sampleSize;
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i] / ss;
		}
		return result;
	}

	public static double[] normalise(int[] sample, int sampleSize) {
		double[] result = new double[sample.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = sample[i] / (sampleSize * 1.0);
		}
		return result;
	}
}