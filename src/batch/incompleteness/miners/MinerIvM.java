package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMflc;

public class MinerIvM extends MinerOption {

	public MinerIvM() {
		super(new MiningParametersIMflc(), null);
		getMiningParameters().setNoiseThreshold(0);
	}

	public String toString() {
		return "IvM";
	}

	public String toLatex() {
		return toString() + "&";
	}

}
