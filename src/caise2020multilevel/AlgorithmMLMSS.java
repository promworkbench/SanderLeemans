package caise2020multilevel;

import org.processmining.multilevelminer.mining.SubModelMiner;

public class AlgorithmMLMSS extends AlgorithmMLMAbstract {

	public String getVariant() {
		return "ss";
	}

	public SubModelMiner<?, ?, ?>[] getMiners() {
		return new SubModelMiner<?, ?, ?>[] { new SubModelMinerSplitMiner(), new SubModelMinerSplitMiner(),
				new SubModelMinerSplitMiner(), new SubModelMinerSplitMiner(), new SubModelMinerSplitMiner(),
				new SubModelMinerSplitMiner(), new SubModelMinerSplitMiner() };
	}

}