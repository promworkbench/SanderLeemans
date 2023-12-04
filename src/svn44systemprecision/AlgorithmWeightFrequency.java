package svn44systemprecision;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.pm.spn_estimator.FrequencyEstimator;

public class AlgorithmWeightFrequency extends AlgorithmWeightEstimator {

	public String getName() {
		return "IMf+Adam 1";
	}

	public String getAbbreviation() {
		return "IMAd1";
	}

	public String getLatexName() {
		return "FEIMf";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new FrequencyEstimator();
	}

}