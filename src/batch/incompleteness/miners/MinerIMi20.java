package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;


public class MinerIMi20 extends MinerOption {

	public MinerIMi20() {
		super(new MiningParametersIMi(), null);
		parameters.setNoiseThreshold(0.2f);
	}

	public String toString() {
		return "IMi 20%";
	}
	
	public String toLatex() {
		return "IMi&0.20";
	}
}
