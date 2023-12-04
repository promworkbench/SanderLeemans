package caise2020isextension;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.pm.spn_estimator.MeanScaledActivityPairRHEstimator;

public class AlgorithmWeightMSAPR extends AlgorithmWeightEstimator {

	public String getName() {
		return "IMf+Adam MeanScaledActivityPairRH";
	}

	public String getAbbreviation() {
		return "IMAdMSAPR";
	}
	
	public String getLatexName() {
		return "MSAPRHIMf";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new MeanScaledActivityPairRHEstimator();
	}

}