package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;


public class MinerIMi80 extends MinerOption {

	public MinerIMi80() {
		super(new MiningParametersIMi(), null);
		parameters.setNoiseThreshold(0.8f);
	}

	public String toString() {
		return "IMi 80%";
	}

	public String toLatex() {
		return "IMi&0.80";
	}
}
