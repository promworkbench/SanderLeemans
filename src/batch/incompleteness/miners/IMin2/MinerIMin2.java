package batch.incompleteness.miners.IMin2;

import batch.incompleteness.miners.MinerOption;

public class MinerIMin2 extends MinerOption {

	public MinerIMin2() {
		super(new MiningParametersIMin2(), null);
	}

	public String toString() {
		return "IMin2";
	}

	public String toLatex() {
		return toString() + "&";
	}
	
}
