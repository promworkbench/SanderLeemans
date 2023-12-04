package org.processmining.cohortanalysis.visualisation;

import org.deckfour.xes.model.XLog;

public class CohortsLauncher {
	private XLog log;

	private CohortsLauncher() {

	}

	public static CohortsLauncher fromLog(XLog log) {
		CohortsLauncher result = new CohortsLauncher();
		result.log = log;
		return result;
	}

	public XLog getLog() {
		return log;
	}
}