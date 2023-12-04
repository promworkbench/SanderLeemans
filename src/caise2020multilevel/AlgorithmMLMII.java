package caise2020multilevel;

import org.processmining.multilevelminer.mining.SubModelMiner;
import org.processmining.multilevelminer.mining.SubModelMinerIMf;

public class AlgorithmMLMII extends AlgorithmMLMAbstract {

	public String getVariant() {
		return "ii";
	}

	public SubModelMiner<?, ?, ?>[] getMiners() {
		return new SubModelMiner<?, ?, ?>[] { new SubModelMinerIMf(), new SubModelMinerIMf(), new SubModelMinerIMf(),
				new SubModelMinerIMf(), new SubModelMinerIMf(), new SubModelMinerIMf(), new SubModelMinerIMf() };
	}

}