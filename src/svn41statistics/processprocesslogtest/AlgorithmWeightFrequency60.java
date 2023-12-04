package svn41statistics.processprocesslogtest;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.pm.spn_estimator.FrequencyEstimator;

public class AlgorithmWeightFrequency60 extends AlgorithmWeightEstimatorDFM {

	public AlgorithmWeightFrequency60() {
		super(0.6);
	}

	public String getName() {
		return "DFM-60-frequency estimator";
	}

	public String getAbbreviation() {
		return "DFM60fe";
	}

	public String getLatexName() {
		return "DFM60fe";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new FrequencyEstimator();
	}

}