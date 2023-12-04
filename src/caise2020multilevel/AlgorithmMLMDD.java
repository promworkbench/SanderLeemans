package caise2020multilevel;

import org.processmining.multilevelminer.mining.SubModelMiner;
import org.processmining.multilevelminer.mining.SubModelMinerDFMM;

public class AlgorithmMLMDD extends AlgorithmMLMAbstract {

	public String getVariant() {
		return "dd";
	}

	public SubModelMiner<?, ?, ?>[] getMiners() {
		return new SubModelMiner<?, ?, ?>[] { new SubModelMinerDFMM(), new SubModelMinerDFMM(), new SubModelMinerDFMM(),
				new SubModelMinerDFMM(), new SubModelMinerDFMM(), new SubModelMinerDFMM(), new SubModelMinerDFMM() };
	}

}
