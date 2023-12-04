package batch.incompleteness.miners;

import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

public abstract class MinerOption {
	protected final MiningParameters parameters;
	protected final DfgMiningParameters dfgParameters;
	
	public MinerOption(MiningParameters parameters, DfgMiningParameters dfgParameters) {
		this.parameters = parameters;
		this.dfgParameters = dfgParameters;
	}
	
	public abstract String toString();
	public abstract String toLatex();
	
	public MiningParameters getMiningParameters() {
		return parameters;
	}
	
	public DfgMiningParameters getDfgMiningParameters() {
		return dfgParameters;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MinerOption) {
			return ((MinerOption) obj).toString().equals(this.toString());
		}
		return false;
	}
}
