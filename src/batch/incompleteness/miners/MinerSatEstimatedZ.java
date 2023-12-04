package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMin;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMc.probabilities.ProbabilitiesEstimatedZ;


public class MinerSatEstimatedZ extends MinerOption {

	public MinerSatEstimatedZ() {
		super(new MiningParametersIMin(), null);
		parameters.setSatProbabilities(new ProbabilitiesEstimatedZ());
		parameters.setIncompleteThreshold(0);
	}

	public String toString() {
		return parameters.getSatProbabilities().toString();
	}


	public String toLatex() {
		return toString() + "&";
	}

}
