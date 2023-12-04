package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMfd;


public class MinerIMiD01 extends MinerOption {

	public MinerIMiD01() {
		super(null, new DfgMiningParametersIMfd());
		dfgParameters.setNoiseThreshold(0.01f);
	}

	public String toString() {
		return "IMi-d 01%";
	}

	public String toLatex() {
		return "IMi-d&0.01";
	}
}
