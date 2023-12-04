package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMfd;


public class MinerIMiD80 extends MinerOption {

	public MinerIMiD80() {
		super(null, new DfgMiningParametersIMfd());
		dfgParameters.setNoiseThreshold(0.8f);
	}

	public String toString() {
		return "IMi-d 80%";
	}

	public String toLatex() {
		return "IMi-d&0.80";
	}
}
