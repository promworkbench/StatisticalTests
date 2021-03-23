package org.processmining.statisticaltests.test;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.Correlation;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl.Type;
import org.processmining.statisticaltests.association.AssociationParametersAbstract;
import org.processmining.statisticaltests.association.AssociationParametersDefault;
import org.processmining.statisticaltests.association.AssociationProcessNumerical;
import org.processmining.statisticaltests.association.CorrelationPlot;
import org.processmining.statisticaltests.association.CorrelationPlotLegend;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class CorrelationTest {
	static File folder = new File("/home/sander/Documents/svn/41 - stochastic statistics/experiments/logs");

	public static void main(String... args) throws Exception {
		//bpic15mergedStartDateSingle();
		//bpic15mergedStartDatePlot();
		//bpic15mergedStartDate();

		//bpic15EndDateSingle(1);
		//bpic15EndDatePlot(1);

		//		testLogSingle(1);
		//		testLogPlot();
		//		bpic11AgePlot();
		//		bpic12aAmountRequestedPlot();
		//		roadFinesAmountRequestedPlot();
		//roadFinesAmountRequested();
		//bpic11Age();
		bpic17AmountRequested();
		//bpic12aAmountRequested();
	}

	public static void legend() throws IOException {
		File outputFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/legend.png");
		BufferedImage image = new CorrelationPlotLegend().create("sample density", "1", "highest");
		ImageIO.write(image, "png", outputFile);
	}

	public static void testLog() throws Exception {
		File inputLog = new File(folder, "correlation-test-log.xes.gz");
		Attribute attribute = new AttributeImpl("value", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/testLog-value-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 100, 100, attribute);
	}

	public static void testLogSingle(int testLog) throws Exception {
		File inputLog = new File(folder, "testLog" + testLog + ".xes.gz");
		Attribute attribute = new AttributeImpl("value", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/testLog" + testLog
						+ ".csv");

		correlation(inputLog, outputCsv, attribute, 999500, true);
	}

	public static void testLogPlot() throws Exception {
		for (int testLog = 1; testLog <= 2; testLog++) {
			File outputCsv = new File(
					"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/testLog"
							+ testLog + ".csv");
			File outputImageFile = new File(
					"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/testLog"
							+ testLog + ".png");

			createCorrelationPlot(outputCsv, outputImageFile);
		}
	}

	public static void bpic20DDAmountSingle() throws Exception {
		File inputLog = new File(folder, "bpic20-DomesticDeclarations.xes.gz");
		Attribute attribute = new AttributeImpl("Amount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic20-DomesticDeclarations-Amount.csv");

		correlation(inputLog, outputCsv, attribute, 1000000, true);
	}

	public static void bpic20DDAmountPlot() throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic20-DomesticDeclarations-Amount.csv");
		File outputImageFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic20-DomesticDeclarations-Amount.png");

		createCorrelationPlot(outputCsv, outputImageFile);
	}

	public static void bpic15EndDate(int municipality) throws Exception {
		File inputLog = new File(folder, "BPIC15_" + municipality + ".xes");
		Attribute attribute = new AttributeImpl("endDate", Type.time);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/BPIC15_"
						+ municipality + "-endDate-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 10000, 1000000, attribute);
	}

	public static void bpic15EndDateSingle(int municipality) throws Exception {
		File inputLog = new File(folder, "BPIC15_" + municipality + ".xes");
		Attribute attribute = new AttributeImpl("endDate", Type.time);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_"
						+ municipality + "-endDate.csv");

		correlation(inputLog, outputCsv, attribute, 1000000, true);
	}

	public static void bpic15EndDatePlot(int municipality) throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_"
						+ municipality + "-endDate.csv");
		File outputImageFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_"
						+ municipality + "-endDate.png");

		createCorrelationPlot(outputCsv, outputImageFile);
	}

	public static void bpic15mergedStartDate() throws Exception {
		File inputLog = new File(folder, "BPIC15_merged.xes.gz");
		Attribute attribute = new AttributeImpl("startDate", Type.time);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/BPIC15_merged-startDate-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 10000, 2000000, attribute);
	}

	public static void bpic15mergedStartDateSingle() throws Exception {
		File inputLog = new File(folder, "BPIC15_merged.xes.gz");
		Attribute attribute = new AttributeImpl("startDate", Type.time);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_merged-startDate.csv");

		correlation(inputLog, outputCsv, attribute, 1000000, true);
	}

	public static void bpic15mergedStartDatePlot() throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_merged-startDate.csv");
		File outputImageFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_merged-startDate.png");

		createCorrelationPlot(outputCsv, outputImageFile);
	}

	public static void bpic15SumLeges(int municipality) throws Exception {
		File inputLog = new File(folder, "BPIC15_" + municipality + ".xes");
		Attribute attribute = new AttributeImpl("SUMleges", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/BPIC15_"
						+ municipality + "-SUMleges-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 10000, 1000000, attribute);
	}

	public static void bpic15SumLegesSingle(int municipality) throws Exception {
		File inputLog = new File(folder, "BPIC15_" + municipality + ".xes");
		Attribute attribute = new AttributeImpl("SUMleges", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_"
						+ municipality + "-SUMLeges.csv");

		correlation(inputLog, outputCsv, attribute, 1000000, true);
	}

	public static void bpic15SumLegesPlot(int municipality) throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_"
						+ municipality + "-SUMLeges.csv");
		File outputImageFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/BPIC15_"
						+ municipality + "-SUMLeges.png");

		createCorrelationPlot(outputCsv, outputImageFile);
	}

	public static void bpic11Age() throws Exception {
		File inputLog = new File(folder, "bpic11.xes.gz");
		Attribute attribute = new AttributeImpl("Age:1", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/bpic11-Age1-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 10000, 2000000, attribute);
	}

	public static void bpic11AgeSingle() throws Exception {
		File inputLog = new File(folder, "bpic11.xes.gz");
		Attribute attribute = new AttributeImpl("Age:1", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic11-Age1.csv");

		correlation(inputLog, outputCsv, attribute, 998000, true);
	}

	public static void bpic11AgePlot() throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic11-Age1.csv");
		File image = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic11-Age1.png");

		createCorrelationPlot(outputCsv, image);
	}

	public static void bpic12aAmountRequested() throws Exception {
		File inputLog = new File(folder, "bpic12-a.xes");
		Attribute attribute = new AttributeImpl("AMOUNT_REQ", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/bpic12a-AMOUNT_REQ-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 10000, 2000000, attribute);
	}

	public static void bpic12aAmountRequestedSingle() throws Exception {
		File inputLog = new File(folder, "bpic12-a.xes");
		Attribute attribute = new AttributeImpl("AMOUNT_REQ", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12a-AMOUNT_REQ.csv");

		correlation(inputLog, outputCsv, attribute, 700000, true);
	}

	public static void bpic12aAmountRequestedPlot() throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12a-AMOUNT_REQ.csv");
		File outputImageFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12a-AMOUNT_REQ.png");

		createCorrelationPlot(outputCsv, outputImageFile);
	}

	public static void bpic12AmountRequestedSingle() throws Exception {
		File inputLog = new File(folder, "bpic12.xes.gz");
		Attribute attribute = new AttributeImpl("AMOUNT_REQ", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12-AMOUNT_REQ.csv");

		correlation(inputLog, outputCsv, attribute, 1000000, true);
	}

	public static void bpic12AmountRequestedPlot() throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12-AMOUNT_REQ.csv");
		File outputImageFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12-AMOUNT_REQ.png");

		createCorrelationPlot(outputCsv, outputImageFile);
	}

	public static void bpic17AmountRequestedSingle() throws Exception {
		File inputLog = new File(folder, "BPI Challenge 2017.xes.gz");
		Attribute attribute = new AttributeImpl("RequestedAmount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic17-RequestedAmount.csv");

		correlation(inputLog, outputCsv, attribute, 1000000, true);
	}

	public static void bpic17AmountRequested() throws Exception {
		File inputLog = new File(folder, "BPI Challenge 2017.xes.gz");
		Attribute attribute = new AttributeImpl("RequestedAmount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/bpic17-RequestedAmount-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 10000, 2000000, attribute);
	}

	public static void bpic17AmountRequestedPlot() throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic17-RequestedAmount.csv");
		File outputImageFile = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic17-RequestedAmount.png");

		createCorrelationPlot(outputCsv, outputImageFile);
	}

	public static void roadFinesAmountRequested() throws Exception {
		File inputLog = new File(folder, "Road fines with trace attributes.xes.gz");
		Attribute attribute = new AttributeImpl("amount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/roadFines-amount-samsen.csv");

		multipleCorrelation(inputLog, outputCsv, 10000, 2000000, attribute);
	}

	public static void roadFinesAmountRequestedSingle() throws Exception {
		File inputLog = new File(folder, "Road fines with trace attributes.xes.gz");
		Attribute attribute = new AttributeImpl("amount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/roadFines-amount.csv");

		correlation(inputLog, outputCsv, attribute, 980000, true);
	}

	public static void roadFinesAmountRequestedPlot() throws Exception {
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/roadFines-amount.csv");
		File image = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/roadFines-amount.png");

		createCorrelationPlot(outputCsv, image);
	}

	public static void createCorrelationPlot(File fileCsv, File outputImageFile) throws IOException {
		CorrelationPlot plot = new CorrelationPlot();

		//read the correlation sample file
		TDoubleList deltaValues = new TDoubleArrayList();
		TDoubleList deltaTraces = new TDoubleArrayList();
		BufferedReader reader = new BufferedReader(new FileReader(fileCsv));
		reader.readLine();
		String line = reader.readLine();
		while (line != null) {
			String[] arr = line.split(",");
			deltaValues.add(Double.valueOf(arr[0]));
			deltaTraces.add(Double.valueOf(arr[1]));

			line = reader.readLine();
		}
		reader.close();

		System.out.println("number of samples " + deltaValues.size());

		outputImageFile.mkdirs();
		
		BufferedImage image = plot.create("Δ value", deltaValues.toArray(), "Δ trace", deltaTraces.toArray());
		ImageIO.write(image, "png", outputImageFile);
	}

	private static void correlation(File inputLog, File outputCsv, Attribute attribute, int numberOfSamples,
			boolean append) throws Exception {
		outputCsv.getParentFile().mkdirs();
		BufferedWriter output;
		if (!append || !outputCsv.exists()) {
			outputCsv.createNewFile();
			output = new BufferedWriter(new FileWriter(outputCsv, false));
			output.write("valueDelta,processDelta\n");
		} else {
			output = new BufferedWriter(new FileWriter(outputCsv, append));
		}

		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		AssociationParametersAbstract parameters = new AssociationParametersDefault(attribute);
		parameters.setNumberOfSamples(numberOfSamples);
		parameters.setDebug(true);

		double[][] result = AssociationProcessNumerical.compute(parameters, log, canceller);

		for (int i = 0; i < result[0].length; i++) {
			output.write(result[0][i] + "," + result[1][i] + "\n");
		}
		output.flush();
		output.close();

		double[] x = result[0];
		double[] y = result[1];
		BigDecimal meanY = Correlation.mean(y);
		double standardDeviationYd = Correlation.standardDeviation(y, meanY);
		double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

		System.out.println("correlation " + correlation);
	}

	private static void multipleCorrelation(File inputLog, File outputCsv, int step, int maxNumberOfSamples,
			Attribute attribute) throws Exception {
		outputCsv.getParentFile().mkdirs();
		int startNumberOfSamples;
		BufferedWriter output;
		if (!outputCsv.exists()) {
			outputCsv.createNewFile();
			startNumberOfSamples = step;
			output = new BufferedWriter(new FileWriter(outputCsv, false));
			output.write("numberOfSamples,correlation,time\n");
		} else {

			//count the number of lines in the file
			BufferedReader reader = new BufferedReader(new FileReader(outputCsv));
			startNumberOfSamples = 0;
			while (reader.readLine() != null) {
				startNumberOfSamples += step;
			}
			reader.close();

			output = new BufferedWriter(new FileWriter(outputCsv, true));
		}

		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		AssociationParametersAbstract parameters = new AssociationParametersAbstract(0, new XEventNameClassifier(),
				attribute, System.currentTimeMillis(), true, 7) {
		};

		for (int numberOfSamples = startNumberOfSamples; numberOfSamples <= maxNumberOfSamples; numberOfSamples += step) {
			parameters.setNumberOfSamples(numberOfSamples);

			long startTime = System.currentTimeMillis();
			double[][] result = AssociationProcessNumerical.compute(parameters, log, canceller);
			long time = System.currentTimeMillis() - startTime;

			double[] x = result[0];
			double[] y = result[1];

			BigDecimal meanY = Correlation.mean(y);
			double standardDeviationYd = Correlation.standardDeviation(y, meanY);
			double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

			output.write(numberOfSamples + "," + correlation + "," + time + "\n");
			output.flush();
		}
		output.close();
	}
}
