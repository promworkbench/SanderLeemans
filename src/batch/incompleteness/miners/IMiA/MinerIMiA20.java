package batch.incompleteness.miners.IMiA;

import batch.incompleteness.miners.MinerOption;

public class MinerIMiA20 extends MinerOption {

	public MinerIMiA20() {
		super(new MiningParametersIMiA(), null);
		parameters.setNoiseThreshold(20);
	}

	public String toString() {
		return "IMiA 20";
	}

	public String toLatex() {
		return toString() + "&";
	}
}
