package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersEKS;

public class MinerEKS extends MinerOption {

	public MinerEKS() {
		super(new MiningParametersEKS(), null);
	}

	public String toString() {
		return "EKS";
	}

	public String toLatex() {
		return toString() + "&";
	}
}
