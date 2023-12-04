package svn27emscpartialordersbounds;

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
		return "IMfMS";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new MeanScaledActivityPairRHEstimator();
	}

}