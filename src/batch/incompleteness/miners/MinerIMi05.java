package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;


public class MinerIMi05 extends MinerOption {

	public MinerIMi05() {
		super(new MiningParametersIMi(), null);
		parameters.setNoiseThreshold(0.05f);
	}

	public String toString() {
		return "IMi 05%";
	}

	public String toLatex() {
		return "IMi&0.05";
	}
}
