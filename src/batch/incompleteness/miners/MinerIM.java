package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;


public class MinerIM extends MinerOption {

	public MinerIM() {
		super(new MiningParametersIM(), null);
	}

	public String toString() {
		return "IM";
	}

	public String toLatex() {
		return toString() + "&";
	}
}
