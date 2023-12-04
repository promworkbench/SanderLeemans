package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMfd;


public class MinerIMiD20 extends MinerOption {

	public MinerIMiD20() {
		super(null, new DfgMiningParametersIMfd());
		dfgParameters.setNoiseThreshold(0.2f);
	}

	public String toString() {
		return "IMi-d 20%";
	}
	
	public String toLatex() {
		return "IMi-d&0.20";
	}
}
