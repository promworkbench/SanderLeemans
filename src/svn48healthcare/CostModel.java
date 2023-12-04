package svn48healthcare;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;

public interface CostModel {

	public String getName();

	/**
	 * The representation of this model (for the data analysis cost tab)
	 * 
	 * @param collection
	 * 
	 * @return
	 */
	public List<DataRow<Object>> getModelRepresentation(Collection<XTrace> collection);

	/**
	 * 
	 * @param trace
	 * @return the log-cost associated with the trace, or Double.NaN if it does
	 *         not exist.
	 */
	public double getCost(XTrace trace);

	/**
	 * 
	 * @param trace
	 * @return the cost that this trace should have according to this model.
	 */
	public BigDecimal getModelCost(XTrace trace);

}