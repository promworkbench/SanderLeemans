package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMin;

public class MinerIMin extends MinerOption {

	public MinerIMin() {
		super(new MiningParametersIMin(), null);
	}

	public String toString() {
		return "IMin";
	}

	public String toLatex() {
		return toString() + "&";
	}
}
