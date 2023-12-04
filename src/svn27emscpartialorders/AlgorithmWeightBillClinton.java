package svn27emscpartialorders;

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
		return "IMfBC";
	}

	public AbstractFrequencyEstimator getEstimator() {
		return new BillClintonEstimator();
	}

}