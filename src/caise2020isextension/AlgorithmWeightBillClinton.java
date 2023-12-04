package caise2020isextension;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.pm.spn_estimator.BillClintonEstimator;

public class AlgorithmWeightBillClinton extends AlgorithmWeightEstimator {

	public String getName() {
		return "IMf+Adam Bill Clinton";
	}

	public String getAbbreviation() {
		return "IMAd-bc";
	}
	
	public String getLatexName() {
		return "BCIMf";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new BillClintonEstimator();
	}

}