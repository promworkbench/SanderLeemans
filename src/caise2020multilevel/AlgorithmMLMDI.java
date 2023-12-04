package caise2020multilevel;

import org.processmining.multilevelminer.mining.SubModelMiner;
import org.processmining.multilevelminer.mining.SubModelMinerDFMM;
import org.processmining.multilevelminer.mining.SubModelMinerIMf;

public class AlgorithmMLMDI extends AlgorithmMLMAbstract {

	public String getVariant() {
		return "di";
	}

	public SubModelMiner<?, ?, ?>[] getMiners() {
		return new SubModelMiner<?, ?, ?>[] { new SubModelMinerDFMM(), new SubModelMinerIMf(), new SubModelMinerIMf(),
				new SubModelMinerIMf(), new SubModelMinerIMf(), new SubModelMinerIMf(), new SubModelMinerIMf() };
	}

}