package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMd;


public class MinerIMd extends MinerOption {

	public MinerIMd() {
		super(null, new DfgMiningParametersIMd());
		dfgParameters.setDebug(false);
	}

	public String toString() {
		return "IM-d";
	}

	public String toLatex() {
		return toString() + "&";
	}
}
