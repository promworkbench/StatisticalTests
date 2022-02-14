package org.processmining.statisticaltests.test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;

public class CorrelationLog {
	static File folder = new File("/home/sander/Documents/svn/41 - stochastic statistics/experiments/logs");

	public static void main(String[] args) throws IOException {
		File logFile = new File(folder, "testLog2.xes.gz");
		XLogWriterIncremental writer = new XLogWriterIncremental(logFile);
		log2(writer);
		writer.close();
	}

	private static void log1(XLogWriterIncremental writer) {
		Random random = new Random(0);

		XAttributeMapImpl traceMap = new XAttributeMapImpl();

		for (int i = 0; i < 100000; i++) {
			double value = random.nextDouble();

			writer.startTrace();
			traceMap.put("concept:name", new XAttributeLiteralImpl("concept:name", i + ""));
			traceMap.put("value", new XAttributeContinuousImpl("value", value));
			writer.writeAttributes(traceMap);

			if (random.nextDouble() < value) {
				writer.writeEvent("a", "complete");
			} else {
				writer.writeEvent("b", "complete");
			}

			writer.endTrace();
		}
	}

	private static void log2(XLogWriterIncremental writer) {
		Random random = new Random(0);

		XAttributeMapImpl traceMap = new XAttributeMapImpl();

		for (int i = 0; i < 100000; i++) {
			int value = random.nextInt(100);

			writer.startTrace();
			traceMap.put("concept:name", new XAttributeLiteralImpl("concept:name", i + ""));
			traceMap.put("value", new XAttributeDiscreteImpl("value", value));
			writer.writeAttributes(traceMap);

			for (int j = 0; j < 100; j++) {
				if (j < value) {
					writer.writeEvent("a", "complete");
				} else {
					writer.writeEvent("b", "complete");
				}
			}

			writer.endTrace();
		}
	}
}
