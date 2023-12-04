package svn41statistics.processprocesslogtest;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.pm.spn_estimator.FrequencyEstimator;

public class AlgorithmWeightFrequency50 extends AlgorithmWeightEstimatorDFM {

	public AlgorithmWeightFrequency50() {
		super(0.5);
	}

	public String getName() {
		return "DFM-50-frequency estimator";
	}

	public String getAbbreviation() {
		return "DFM50fe";
	}

	public String getLatexName() {
		return "DFM50fe";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new FrequencyEstimator();
	}

}