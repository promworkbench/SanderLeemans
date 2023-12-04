package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;


public class MinerIMi01 extends MinerOption {

	public MinerIMi01() {
		super(new MiningParametersIMi(), null);
		parameters.setNoiseThreshold(0.01f);
	}

	public String toString() {
		return "IMi 01%";
	}

	public String toLatex() {
		return "IMi&0.01";
	}
}
