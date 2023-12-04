package svn27emscpartialorders;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.pm.spn_estimator.ActivityPairRHEstimator;

public class AlgorithmWeightRHE extends AlgorithmWeightEstimator {

	public String getName() {
		return "IMf+Adam RHE";
	}

	public String getAbbreviation() {
		return "IMAdRHE";
	}
	
	public String getLatexName() {
		return "IMfRH";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new ActivityPairRHEstimator();
	}

}