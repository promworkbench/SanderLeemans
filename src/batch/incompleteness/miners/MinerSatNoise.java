package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMin;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMc.probabilities.ProbabilitiesNoise;

public class MinerSatNoise extends MinerOption {
	public MinerSatNoise() {
		super(new MiningParametersIMin(), null);
		parameters.setSatProbabilities(new ProbabilitiesNoise());
		parameters.setIncompleteThreshold(0);
	}

	public String toString() {
		return parameters.getSatProbabilities().toString();
	}

	public String toLatex() {
		return toString() + "&";
	}
}
