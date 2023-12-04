package caise2020isextension;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.pm.spn_estimator.ActivityPairLHEstimator;

public class AlgorithmWeightLHE extends AlgorithmWeightEstimator {

	public String getName() {
		return "IMf+Adam LHE";
	}

	public String getAbbreviation() {
		return "IMAdLHE";
	}
	
	public String getLatexName() {
		return "LHEIMf";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new ActivityPairLHEstimator();
	}

}