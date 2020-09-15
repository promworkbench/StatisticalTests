package org.processmining.cohortanalysis.visualisation;

import org.deckfour.xes.model.XLog;

public class LousyCohortsLauncher {
	private XLog log;

	private LousyCohortsLauncher() {

	}

	public static LousyCohortsLauncher fromLog(XLog log) {
		LousyCohortsLauncher result = new LousyCohortsLauncher();
		result.log = log;
		return result;
	}

	public XLog getLog() {
		return log;
	}
}