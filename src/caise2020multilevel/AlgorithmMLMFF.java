package caise2020multilevel;

import org.processmining.multilevelminer.mining.SubModelMiner;
import org.processmining.multilevelminer.mining.SubModelMinerFlower;

public class AlgorithmMLMFF extends AlgorithmMLMAbstract {

	public String getVariant() {
		return "ff";
	}

	public SubModelMiner<?, ?, ?>[] getMiners() {
		return new SubModelMiner<?, ?, ?>[] { new SubModelMinerFlower(), new SubModelMinerFlower(),
				new SubModelMinerFlower(), new SubModelMinerFlower(), new SubModelMinerFlower(),
				new SubModelMinerFlower(), new SubModelMinerFlower() };
	}

}
