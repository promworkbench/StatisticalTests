package org.processmining.statisticaltests.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.correlation.CorrelationProcessNumerical;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl.Type;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

public class CorrelationTest {
	static File folder = new File("/home/sander/Documents/svn/41 - stochastic statistics/experiments/logs");

	public static void main(String... args) throws Exception {
		roadFinesAmountRequested();
	}

	public static void bpic11Age() throws Exception {
		File inputLog = new File(folder, "bpic11.xes.gz");
		Attribute attribute = new AttributeImpl("Age:1", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic11-Age1.csv");

		correlation(inputLog, outputCsv, attribute);
	}

	public static void bpic12aAmountRequested() throws Exception {
		File inputLog = new File(folder, "bpic12-a.xes");
		Attribute attribute = new AttributeImpl("AMOUNT_REQ", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/bpic12a-AMOUNT_REQ.csv");

		correlation(inputLog, outputCsv, attribute);
	}

	public static void roadFinesAmountRequested() throws Exception {
		File inputLog = new File(folder, "Road fines with trace attributes.xes.gz");
		Attribute attribute = new AttributeImpl("amount", Type.numeric);
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/04 - correlation/roadFines-amount.csv");

		correlation(inputLog, outputCsv, attribute);
	}

	private static void correlation(File inputLog, File outputCsv, Attribute attribute) throws Exception {
		outputCsv.getParentFile().mkdirs();
		if (!outputCsv.exists()) {
			outputCsv.createNewFile();
		}
		FileWriter fw = new FileWriter(outputCsv, false);
		BufferedWriter output = new BufferedWriter(fw);
		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		output.write("valueDelta,processDelta\n");
		XEventClassifier classifier = new XEventNameClassifier();

		double[][] result = CorrelationProcessNumerical.compute(log, classifier, attribute, canceller);
		for (double[] row : result) {
			output.write(row[0] + "," + row[1] + "\n");
		}
		output.close();
	}
}
