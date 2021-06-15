package org.processmining.statisticaltests.test;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang.SystemUtils;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.statisticaltests.helperclasses.StatisticalTestUtils;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

public class TestTest {
	public static File folder = SystemUtils.IS_OS_LINUX
			? new File("/home/sander/Documents/svn/41 - stochastic statistics/experiments/")
			: new File("C:\\Users\\leemans2\\Documents\\svn\\41 - stochastic statistics\\experiments\\");

	public static void main(String[] args) throws Exception {
		//		testBPIC15();
		//		multipleTests("Road fines with trace attributes.xes.gz");
		//		multipleTests("bpic12-a.xes");
	}

	public static enum Type {
		linear {
			int step(int current, int step) {
				return current + step;
			}
		},
		exponential {
			int step(int current, int step) {
				return current * step;
			}
		};

		abstract int step(int current, int step);
	}

	//	public static void testBPIC15() throws Exception {
	//		for (int i = 1; i <= 5; i++) {
	//			File logI = new File(new File(folder, "logs"), "BPIC15_" + i + ".xes");
	//			for (int j = i + 1; j <= 5; j++) {
	//				File logJ = new File(new File(folder, "logs"), "BPIC15_" + j + ".xes");
	//
	//				File outputCsv = new File(new File(folder, "06 - log log test"),
	//						"BPIC15_" + i + "-BPIC15_" + j + "samsen.csv");
	//
	//				multipleTests(logI, logJ, outputCsv, 10, 100, Type.linear);
	//			}
	//		}
	//	}

	//	public static void multipleTests(String logName) throws Exception {
	//		File inputLogA = new File(new File(folder, "logs"), logName);
	//
	//		String[] logsB = new String[] { "TE", "MS", "TS", "LE", "LL" };
	//
	//		for (String logB : logsB) {
	//			System.out.println(logB);
	//			File outputCsv = new File(new File(folder, "06 - log log test"), logName + "-" + logB + "-samsen.csv");
	//
	//			multipleTests(inputLogA, new File(new File(folder, "logs"), logName + "-" + logB + ".xes.gz"), outputCsv,
	//					10, 1000000000, Type.exponential);
	//		}
	//	}

	//	private static void multipleTests(File inputLogA, File inputLogB, File outputCsv, int step, int maxSampleSize,
	//			Type type) throws Exception {
	//		outputCsv.getParentFile().mkdirs();
	//		int startSampleSize = step;
	//		BufferedWriter output;
	//		if (!outputCsv.exists()) {
	//			outputCsv.createNewFile();
	//			startSampleSize = step;
	//			output = new BufferedWriter(new FileWriter(outputCsv, false));
	//			output.write("sampleSize,numberOfSamples,p,time\n");
	//		} else {
	//			//count the number of lines in the file
	//			BufferedReader reader = new BufferedReader(new FileReader(outputCsv));
	//			startSampleSize = 1;
	//			while (reader.readLine() != null) {
	//				startSampleSize = type.step(startSampleSize, step);
	//			}
	//			reader.close();
	//
	//			output = new BufferedWriter(new FileWriter(outputCsv, true));
	//		}
	//
	//		PluginContext context = new FakeContext();
	//		XLog logA = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLogA);
	//		XLog logB = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLogB);
	//
	//		ProMCanceller canceller = new ProMCanceller() {
	//			public boolean isCancelled() {
	//				return false;
	//			}
	//		};
	//
	//		LogLogUnknownProcessTestParametersAbstract parameters = new LogLogUnknownProcessTestParametersDefault();
	//		parameters.setDebug(true);
	//
	//		for (int sampleSize = startSampleSize; sampleSize <= maxSampleSize; sampleSize = type.step(sampleSize, step)) {
	//			System.out.println("sample size " + sampleSize);
	//			parameters.setSampleSize(sampleSize);
	//
	//			long startTime = System.currentTimeMillis();
	//			double p = new LogLogUnknownProcessTest().test(Pair.of(logA, logB), parameters, canceller);
	//			long time = System.currentTimeMillis() - startTime;
	//
	//			output.write(
	//					parameters.getSampleSize() + "," + parameters.getNumberOfSamples() + "," + p + "," + time + "\n");
	//			output.flush();
	//		}
	//		output.close();
	//	}

	public static void createLogs(String logName) throws Exception {
		File inputLogA = new File(folder, logName);
		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLogA);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		Activity2IndexKey activityKey = new Activity2IndexKey();
		XEventClassifier classifier = new XEventNameClassifier();
		StochasticLanguage<TotalOrder> language = XLog2StochasticLanguage.convert(log, classifier, activityKey,
				canceller);

		//LL copy
		File LL = new File(folder, logName + "-LL.xes.gz");
		if (!LL.exists()) {
			XLogWriterIncremental writer = new XLogWriterIncremental(LL);
			for (XTrace trace : log) {
				writer.writeTrace(trace);
			}
			writer.close();
		}

		//TE copy (average probabilities of lowest two traces) 
		File TE = new File(folder, logName + "-TE.xes.gz");
		if (!TE.exists()) {

			//find least-occurring trace
			double probMinTrace = Double.MAX_VALUE;
			String[] traceMinTrace = null;
			for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
				String[] trace = language.getActivityKey().toTraceString(it.next());
				if (it.getProbability() < probMinTrace) {
					probMinTrace = it.getProbability();
					traceMinTrace = trace;
				}
			}

			System.out.println("min trace " + probMinTrace + " (" + (probMinTrace * log.size()) + ")" + " "
					+ Arrays.toString(traceMinTrace));

			double probSMinTrace = Double.MAX_VALUE;
			String[] traceSMinTrace = null;
			for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
				String[] trace = language.getActivityKey().toTraceString(it.next());
				if (it.getProbability() < probSMinTrace && it.getProbability() > probMinTrace) {
					probSMinTrace = it.getProbability();
					traceSMinTrace = trace;
				}
			}

			System.out.println("min trace " + probSMinTrace + " (" + (probSMinTrace * log.size()) + ")" + " "
					+ Arrays.toString(traceSMinTrace));

			//copy other traces
			XLogWriterIncremental writer = new XLogWriterIncremental(TE);
			for (XTrace trace : log) {
				if (!Arrays.equals(StatisticalTestUtils.getTraceString(trace, classifier), traceMinTrace)
						&& !Arrays.equals(StatisticalTestUtils.getTraceString(trace, classifier), traceSMinTrace)) {
					writer.writeTrace(trace);
				}
			}

			int count = (int) Math.floor((probMinTrace * log.size() + probSMinTrace * log.size()) / 2);
			int countS = (int) Math.ceil((probMinTrace * log.size() + probSMinTrace * log.size()) / 2);
			for (int i = 0; i < count; i++) {
				writer.startTrace();
				for (String activity : traceMinTrace) {
					writer.writeEvent(activity, "complete");
				}
				writer.endTrace();
			}
			for (int i = 0; i < countS; i++) {
				writer.startTrace();
				for (String activity : traceSMinTrace) {
					writer.writeEvent(activity, "complete");
				}
				writer.endTrace();
			}

			writer.close();
		}

		//TS copy (swap probabilities)
		File TS = new File(folder, logName + "-TS.xes.gz");
		if (!TS.exists()) {

			//find least-occurring trace
			double probMinTrace = Double.MAX_VALUE;
			String[] traceMinTrace = null;
			for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
				String[] trace = language.getActivityKey().toTraceString(it.next());
				if (it.getProbability() < probMinTrace) {
					probMinTrace = it.getProbability();
					traceMinTrace = trace;
				}
			}

			System.out.println("min trace " + probMinTrace + " (" + (probMinTrace * log.size()) + ")" + " "
					+ Arrays.toString(traceMinTrace));

			double probSMinTrace = Double.MAX_VALUE;
			String[] traceSMinTrace = null;
			for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
				String[] trace = language.getActivityKey().toTraceString(it.next());
				if (it.getProbability() < probSMinTrace && it.getProbability() > probMinTrace) {
					probSMinTrace = it.getProbability();
					traceSMinTrace = trace;
				}
			}

			System.out.println("min trace " + probSMinTrace + " (" + (probSMinTrace * log.size()) + ")" + " "
					+ Arrays.toString(traceSMinTrace));

			//copy other traces
			XLogWriterIncremental writer = new XLogWriterIncremental(TS);
			for (XTrace trace : log) {
				if (!Arrays.equals(StatisticalTestUtils.getTraceString(trace, classifier), traceMinTrace)
						&& !Arrays.equals(StatisticalTestUtils.getTraceString(trace, classifier), traceSMinTrace)) {
					writer.writeTrace(trace);
				}
			}

			int count = (int) (probSMinTrace * log.size());
			int countS = (int) (probMinTrace * log.size());
			for (int i = 0; i < count; i++) {
				writer.startTrace();
				for (String activity : traceMinTrace) {
					writer.writeEvent(activity, "complete");
				}
				writer.endTrace();
			}
			for (int i = 0; i < countS; i++) {
				writer.startTrace();
				for (String activity : traceSMinTrace) {
					writer.writeEvent(activity, "complete");
				}
				writer.endTrace();
			}

			writer.close();
		}

		//LE (traces have equal probabilities)
		File LE = new File(folder, logName + "-LE.xes.gz");
		if (!LE.exists()) {
			XLogWriterIncremental writer = new XLogWriterIncremental(LE);
			for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
				String[] trace = language.getActivityKey().toTraceString(it.next());
				writer.startTrace();
				for (String activity : trace) {
					writer.writeEvent(activity, "complete");
				}
				writer.endTrace();
			}
			writer.close();
		}

		//MS copy (swap probabilities)
		File MS = new File(folder, logName + "-MS.xes.gz");
		if (!MS.exists()) {

			//find most-occurring trace
			double probMaxTrace = -Double.MAX_VALUE;
			String[] traceMaxTrace = null;
			for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
				String[] trace = language.getActivityKey().toTraceString(it.next());
				if (it.getProbability() > probMaxTrace) {
					probMaxTrace = it.getProbability();
					traceMaxTrace = trace;
				}
			}

			System.out.println("max trace " + probMaxTrace + " (" + (probMaxTrace * log.size()) + ")" + " "
					+ Arrays.toString(traceMaxTrace));

			double probSMaxTrace = -Double.MAX_VALUE;
			String[] traceSMaxTrace = null;
			for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
				String[] trace = language.getActivityKey().toTraceString(it.next());
				if (it.getProbability() > probSMaxTrace && it.getProbability() < probMaxTrace) {
					probSMaxTrace = it.getProbability();
					traceSMaxTrace = trace;
				}
			}

			System.out.println("max trace " + probSMaxTrace + " (" + (probSMaxTrace * log.size()) + ")" + " "
					+ Arrays.toString(traceSMaxTrace));

			//copy other traces
			XLogWriterIncremental writer = new XLogWriterIncremental(MS);
			for (XTrace trace : log) {
				if (!Arrays.equals(StatisticalTestUtils.getTraceString(trace, classifier), traceMaxTrace)
						&& !Arrays.equals(StatisticalTestUtils.getTraceString(trace, classifier), traceSMaxTrace)) {
					writer.writeTrace(trace);
				}
			}

			int count = (int) (probSMaxTrace * log.size());
			int countS = (int) (probMaxTrace * log.size());
			for (int i = 0; i < count; i++) {
				writer.startTrace();
				for (String activity : traceMaxTrace) {
					writer.writeEvent(activity, "complete");
				}
				writer.endTrace();
			}
			for (int i = 0; i < countS; i++) {
				writer.startTrace();
				for (String activity : traceSMaxTrace) {
					writer.writeEvent(activity, "complete");
				}
				writer.endTrace();
			}

			writer.close();
		}
	}
}