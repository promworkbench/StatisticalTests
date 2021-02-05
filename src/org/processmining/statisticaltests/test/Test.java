package org.processmining.statisticaltests.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.statisticaltests.LogLogTest;
import org.processmining.statisticaltests.LogLogUnknownProcessTest;
import org.processmining.statisticaltests.ParametersAbstract;
import org.processmining.statisticaltests.ParametersDefault;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

public class Test {
	static File folder = new File("/home/sander/Documents/svn/41 - stochastic statistics/experiments/logs");
	static File outputFolder = new File(
			"/home/sander/Documents/svn/41 - stochastic statistics/experiments/06 - log log test");

	static int maxSampleSize = 100;
	static int numberOfSamples = 1000;

	public static void main(String... args) throws Exception {
		Bpic15();
	}

	public static void Bpic15() throws IOException, InterruptedException, Exception {
		for (int i = 1; i <= 5; i++) {
			for (int j = i; j <= 5; j++) {
				File inputLog1 = new File(folder, "BPIC15_" + i + ".xes");
				File inputLog2 = new File(folder, "BPIC15_" + j + ".xes");
				File outputCsv = new File(outputFolder, "BPIC15-" + i + "-BPIC15-" + j + ".csv");

				loglog2IncreasingSampleSize(inputLog1, inputLog2, outputCsv, 10);
			}
		}
	}

	public static void incRfRf() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "Road fines with trace attributes.xes.gz");
		File inputLog2 = new File(folder, "Road fines with trace attributes.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/RF-RF.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incRfRfe() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "Road fines with trace attributes.xes.gz");
		File inputLog2 = new File(folder, "Road fines with trace attributes-equalWeights.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/RF-RFe.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incRfRfdlot() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "Road fines with trace attributes.xes.gz");
		File inputLog2 = new File(folder, "Road fines with trace attributes-dlot.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/RF-RFdlot.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incRfRfmoto() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "Road fines with trace attributes.xes.gz");
		File inputLog2 = new File(folder, "Road fines with trace attributes-moto.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/RF-RFmoto.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic11Bpic11() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "bpic11.xes.gz");
		File inputLog2 = new File(folder, "bpic11.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic11-bpic11.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic11Bpic11e() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "bpic11.xes.gz");
		File inputLog2 = new File(folder, "bpic11-equalWeights.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic11-bpic11e.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic11Bpic11dlot() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "bpic11.xes.gz");
		File inputLog2 = new File(folder, "bpic11-dlot.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic11-bpic11dlot.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic11Bpic11moto() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File(folder, "bpic11.xes.gz");
		File inputLog2 = new File(folder, "bpic11-moto.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic11-bpic11moto.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic12aBpic12a() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File("/home/sander/Desktop/bpic12-a.xes");
		File inputLog2 = new File("/home/sander/Desktop/bpic12-a.xes");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic12a-bpic12a.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic12aBpic12ae() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File("/home/sander/Desktop/bpic12-a.xes");
		File inputLog2 = new File("/home/sander/Desktop/bpic12-a-equalWeights.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic12a-bpic12ae.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic12aBpic12dlot() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File("/home/sander/Desktop/bpic12-a.xes");
		File inputLog2 = new File("/home/sander/Desktop/bpic12-a-dlot.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic12a-bpic12adlot.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void incBpic12aBpic12amoto() throws IOException, InterruptedException, Exception {
		File inputLog1 = new File("/home/sander/Desktop/bpic12-a.xes");
		File inputLog2 = new File("/home/sander/Desktop/bpic12-a-moto.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/03 - loglog increasing sample size/bpic12a-bpic12amoto.csv");

		loglogIncreasingSampleSize(inputLog1, inputLog2, outputCsv);
	}

	public static void bpic12aBpic12ae() throws IOException, InterruptedException, Exception {
		int repetitions = 100;
		File inputLog1 = new File("/home/sander/Desktop/bpic12-a.xes");
		File inputLog2 = new File("/home/sander/Desktop/bpic12-a-equalWeights.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/02 - loglog symmetrical/output-logs-bpic12a-bpic12ldot.csv");

		loglog(repetitions, inputLog1, inputLog2, outputCsv);
	}

	public static void bpic12aBpic12dlot() throws IOException, InterruptedException, Exception {
		int repetitions = 100;
		File inputLog1 = new File("/home/sander/Desktop/bpic12-a.xes");
		File inputLog2 = new File("/home/sander/Desktop/bpic12-a-dlot.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/02 - loglog symmetrical/output-logs-bpic12a-bpic12ae.csv");

		loglog(repetitions, inputLog1, inputLog2, outputCsv);
	}

	public static void bpic12aBpic11() throws IOException, InterruptedException, Exception {
		int repetitions = 100;
		File inputLog1 = new File("/home/sander/Desktop/bpic12-a.xes");
		File inputLog2 = new File("/home/sander/Desktop/bpic11.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/02 - loglog symmetrical/output-logs-bpic12a-Bpic11.csv");

		loglog(repetitions, inputLog1, inputLog2, outputCsv);
	}

	public static void bpic11() throws IOException, InterruptedException, Exception {
		int repetitions = 100;
		File inputLog = new File("/home/sander/Desktop/bpic11.xes.gz");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/02 - loglog symmetrical/output-logWithItself-bpic11.csv");

		extracted(repetitions, inputLog, outputCsv);
	}

	public static void bpic12a() throws IOException, InterruptedException, Exception {
		int repetitions = 63;
		File inputLog = new File("/home/sander/Desktop/bpic12-a.xes");
		File outputCsv = new File(
				"/home/sander/Documents/svn/41 - stochastic statistics/experiments/02 - loglog symmetrical/output-logWithItself.csv");

		extracted(repetitions, inputLog, outputCsv);
	}

	public static void extracted(int repetitions, File inputLog, File outputCsv)
			throws IOException, Exception, InterruptedException {
		outputCsv.getParentFile().mkdirs();
		if (!outputCsv.exists()) {
			outputCsv.createNewFile();
		}
		FileWriter fw = new FileWriter(outputCsv, true);
		BufferedWriter output = new BufferedWriter(fw);
		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		for (int i = 0; i < repetitions; i++) {
			ParametersAbstract parameters = new ParametersDefault(log.size());
			double p = LogLogTest.p(log, log, parameters, canceller);
			output.write(p + "\n");
			System.out.println(i + " " + p);
		}

		output.close();
	}

	//	public static void loglog(int repetitions, File inputLog1, File inputLog2, File outputCsv)
	//			throws IOException, Exception, InterruptedException {
	//		outputCsv.getParentFile().mkdirs();
	//		if (!outputCsv.exists()) {
	//			outputCsv.createNewFile();
	//		}
	//		FileWriter fw = new FileWriter(outputCsv, true);
	//		BufferedWriter output = new BufferedWriter(fw);
	//		PluginContext context = new FakeContext();
	//		XLog log1 = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog1);
	//		XLog log2 = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog2);
	//
	//		ProMCanceller canceller = new ProMCanceller() {
	//			public boolean isCancelled() {
	//				return false;
	//			}
	//		};
	//
	//		for (int i = 0; i < repetitions; i++) {
	//			ParametersAbstract parameters = new ParametersDefault(Math.max(log1.size(), log2.size()));
	//			double p = LogLogTest.p(log1, log2, parameters, canceller);
	//			output.write(p + "\n");
	//			output.flush();
	//			System.out.println(i + " " + p);
	//		}
	//
	//		output.close();
	//	}

	public static void loglog2IncreasingSampleSize(File inputLogA, File inputLogB, File outputCsv, int step)
			throws IOException, Exception, InterruptedException {
		outputCsv.getParentFile().mkdirs();
		int startSampleSize;
		BufferedWriter output;
		if (!outputCsv.exists()) {
			outputCsv.createNewFile();
			startSampleSize = step;
			output = new BufferedWriter(new FileWriter(outputCsv, false));
			output.write("sampleSize,p,time\n");
			output.flush();
		} else {

			//count the number of lines in the file
			BufferedReader reader = new BufferedReader(new FileReader(outputCsv));
			reader.readLine(); //read the header
			startSampleSize = step;
			while (reader.readLine() != null) {
				startSampleSize += step;
			}
			reader.close();

			output = new BufferedWriter(new FileWriter(outputCsv, true));
		}

		PluginContext context = new FakeContext();
		XLog logA = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLogA);
		XLog logB = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLogB);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		for (int sampleSize = startSampleSize; sampleSize <= maxSampleSize; sampleSize += step) {
			System.out.println("sample size " + sampleSize);

			ParametersAbstract parameters = new ParametersAbstract(sampleSize, numberOfSamples, sampleSize,
					MiningParameters.getDefaultClassifier(), MiningParameters.getDefaultClassifier()) {
			};

			parameters.setSampleSize(sampleSize);

			long startTime = System.currentTimeMillis();
			double p = LogLogUnknownProcessTest.p(logA, logB, parameters, canceller);
			long time = System.currentTimeMillis() - startTime;

			output.write(sampleSize + "," + p + "," + time + "\n");
			output.flush();
		}
		output.close();
	}

	//	public static void loglogIncreasingSampleSize(File inputLog1, File inputLog2, File outputCsv)
	//			throws IOException, Exception, InterruptedException {
	//		outputCsv.getParentFile().mkdirs();
	//		if (!outputCsv.exists()) {
	//			outputCsv.createNewFile();
	//		}
	//		FileWriter fw = new FileWriter(outputCsv, false);
	//		BufferedWriter output = new BufferedWriter(fw);
	//		PluginContext context = new FakeContext();
	//		XLog log1 = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog1);
	//		XLog log2 = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog2);
	//
	//		ProMCanceller canceller = new ProMCanceller() {
	//			public boolean isCancelled() {
	//				return false;
	//			}
	//		};
	//
	//		output.write("sampleSize,p,computationTime\n");
	//		for (int i = 10; i < Math.max(log1.size(), log2.size()); i += 10) {
	//			ParametersAbstract parameters = new ParametersDefault(i);
	//			long start = System.currentTimeMillis();
	//			double p = LogLogTest.p(log1, log2, parameters, canceller);
	//			long end = System.currentTimeMillis();
	//
	//			DecimalFormat df = new DecimalFormat("#");
	//			df.setMaximumFractionDigits(4);
	//			output.write(i + "," + df.format(p) + "," + (end - start) + "\n");
	//			output.flush();
	//			System.out.println(i + "," + p);
	//		}
	//
	//		output.close();
	//	}
}
