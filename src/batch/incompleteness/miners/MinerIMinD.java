package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMcd;

public class MinerIMinD extends MinerOption {

	public MinerIMinD() {
		super(null, new DfgMiningParametersIMcd());
	}

	public String toString() {
		return "IMin-d";
	}

	public String toLatex() {
		return toString() + "&";
	}
}
