package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.CohortsState;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;

public class Cl19Done extends CohortsChainLink<Object, Object> {

	protected Object generateInput(CohortsState state) {
		return null;
	}

	protected Object executeLink(Object input, IvMCanceller canceller) throws Exception {
		Thread.sleep(5000);
		return null;
	}

	protected void processResult(Object result, CohortsState state) {

	}

	protected void invalidateResult(CohortsState state) {

	}

	public String getName() {
		return "done";
	}

	public String getStatusBusyMessage() {
		return "done";
	}
}
