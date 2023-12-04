package svn48healthcare;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;

public interface CostModelComputer {

	public String getName();

	public void compute(XLog log, CostModelAbstract result, IvMCanceller canceller) throws Exception;

	/**
	 * 
	 * @return null if the computation was successful. Otherwise an error
	 *         message.
	 */
	public String getErrorMessage();

}