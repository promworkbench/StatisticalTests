package org.processmining.statisticaltests.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.correlation.CorrelationParametersAbstract;
import org.processmining.correlation.CorrelationProcessNumerical;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.Correlation;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl.Type;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

public class CorrelationTest {
	static File folder = new File("/home/sander/Documents/svn/41 - stochastic statistics/experiments/logs");

	static int maxSampleSize = 150000;
	static int numberOfSamples = 10000;

	public static void main(String... args) throws Exception {
		testLogSingle();
	}

	public static void testLog() throws Exception {
		File inputLog = new File(folder, "correlation-test-log.xes.gz");
		Attribute attribute = new AttributeImpl("value", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/testLog-value-samsen-1000.csv");

		multipleCorrelation(inputLog, outputCsv, 1000, attribute);
	}

	public static void testLogSingle() throws Exception {
		int testLog = 1;
		File inputLog = new File(folder, "testLog" + testLog + ".xes.gz");
		Attribute attribute = new AttributeImpl("value", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/testLog" + testLog
						+ "-2.csv");

		correlation(inputLog, outputCsv, attribute, 500, 2);
	}

	public static void bpic11Age() throws Exception {
		File inputLog = new File(folder, "bpic11.xes.gz");
		Attribute attribute = new AttributeImpl("Age:1", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/bpic11-Age1-samsen-100.csv");

		multipleCorrelation(inputLog, outputCsv, 100, attribute);
	}

	public static void bpic11AgeSingle() throws Exception {
		File inputLog = new File(folder, "bpic11.xes.gz");
		Attribute attribute = new AttributeImpl("Age:1", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic11-Age1.csv");

		correlation(inputLog, outputCsv, attribute, 500, 1000);
	}

	public static void bpic12aAmountRequested() throws Exception {
		File inputLog = new File(folder, "bpic12-a.xes");
		Attribute attribute = new AttributeImpl("AMOUNT_REQ", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/bpic12a-AMOUNT_REQ-samsen-1000.csv");

		multipleCorrelation(inputLog, outputCsv, 1000, attribute);
	}

	public static void bpic12aAmountRequestedSingle() throws Exception {
		File inputLog = new File(folder, "bpic12-a.xes");
		Attribute attribute = new AttributeImpl("AMOUNT_REQ", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12a-AMOUNT_REQ.csv");

		correlation(inputLog, outputCsv, attribute, 500, 1000);
	}

	public static void roadFinesAmountRequested() throws Exception {
		File inputLog = new File(folder, "Road fines with trace attributes.xes.gz");
		Attribute attribute = new AttributeImpl("amount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/05 - correlation sampleSize sensitivity/roadFines-amount-samsen-100.csv");

		multipleCorrelation(inputLog, outputCsv, 100, attribute);
	}

	public static void roadFinesAmountRequestedSingle() throws Exception {
		File inputLog = new File(folder, "Road fines with trace attributes.xes.gz");
		Attribute attribute = new AttributeImpl("amount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/roadFines-amount.csv");

		correlation(inputLog, outputCsv, attribute, 500, 1000);
	}

	private static void correlation(File inputLog, File outputCsv, Attribute attribute, int numberOfSamples,
			int sampleSize) throws Exception {
		outputCsv.getParentFile().mkdirs();
		outputCsv.createNewFile();
		BufferedWriter output = new BufferedWriter(new FileWriter(outputCsv, false));
		output.write("valueDelta,processDelta\n");

		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		CorrelationParametersAbstract parameters = new CorrelationParametersAbstract(numberOfSamples, sampleSize,
				new XEventNameClassifier(), attribute, System.currentTimeMillis()) {
		};

		double[][] result = CorrelationProcessNumerical.compute(parameters, log, canceller);

		for (int i = 0; i < result[0].length; i++) {
			output.write(result[0][i] + "," + result[1][i] + "\n");
		}
		output.flush();
		output.close();
	}

	private static void multipleCorrelation(File inputLog, File outputCsv, int step, Attribute attribute)
			throws Exception {
		outputCsv.getParentFile().mkdirs();
		int startSampleSize;
		BufferedWriter output;
		if (!outputCsv.exists()) {
			outputCsv.createNewFile();
			startSampleSize = step;
			output = new BufferedWriter(new FileWriter(outputCsv, false));
			output.write("sampleSize,correlation,time\n");
		} else {

			//count the number of lines in the file
			BufferedReader reader = new BufferedReader(new FileReader(outputCsv));
			startSampleSize = 0;
			while (reader.readLine() != null) {
				startSampleSize += step;
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

		CorrelationParametersAbstract parameters = new CorrelationParametersAbstract(numberOfSamples, 10,
				new XEventNameClassifier(), attribute, System.currentTimeMillis()) {
		};

		for (int sampleSize = startSampleSize; sampleSize <= maxSampleSize; sampleSize += step) {
			System.out.println("sample size " + sampleSize);

			parameters.setSampleSize(sampleSize);

			long startTime = System.currentTimeMillis();
			double[][] result = CorrelationProcessNumerical.compute(parameters, log, canceller);
			long time = System.currentTimeMillis() - startTime;

			double[] x = result[0];
			double[] y = result[1];

			BigDecimal meanY = Correlation.mean(y);
			double standardDeviationYd = Correlation.standardDeviation(y, meanY);
			double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

			output.write(sampleSize + "," + correlation + "," + time + "\n");
			output.flush();
		}
		output.close();
	}
}
