package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMfd;


public class MinerIMiD05 extends MinerOption {

	public MinerIMiD05() {
		super(null, new DfgMiningParametersIMfd());
		dfgParameters.setNoiseThreshold(0.05f);
	}

	public String toString() {
		return "IMi-d 05%";
	}

	public String toLatex() {
		return "IMi-d&0.05";
	}
}
