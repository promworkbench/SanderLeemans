package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMin;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMc.probabilities.ProbabilitiesUnit;


public class MinerSatUnit extends MinerOption {

	public MinerSatUnit() {
		super(new MiningParametersIMin(), null);
		parameters.setSatProbabilities(new ProbabilitiesUnit());
		parameters.setIncompleteThreshold(0);
	}
	
	public String toString() {
		return parameters.getSatProbabilities().toString();
	}

	public String toLatex() {
		return toString() + "&";
	}
}
