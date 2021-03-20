package org.processmining.statisticaltests.test;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

public class TestBPIC15 {
	public static void main(String[] args) throws Exception {
		File folder = TestTest.folder;
		PluginContext context = new FakeContext();
		File outputFile = new File(new File(folder, "logs"), "BPIC15_merged.xes.gz");
		outputFile.getParentFile().mkdirs();
		LogWriterIncremental writer = new XLogWriterIncremental(outputFile);
		for (int i = 1; i <= 5; i++) {
			File logFile = new File(new File(folder, "logs"), "BPIC15_" + i + ".xes");

			XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

			for (XTrace trace : log) {
				trace.getAttributes().put("fromLog", new XAttributeLiteralImpl("fromLog", "log" + i));
				writer.writeTrace(trace);
			}
		}

		writer.close();
	}
}
