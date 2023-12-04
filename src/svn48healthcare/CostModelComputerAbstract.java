package svn48healthcare;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;

public abstract class CostModelComputerAbstract implements CostModelComputer {

	protected String message = null;

	public String getErrorMessage() {
		return message;
	}

	@Override
	public void compute(XLog log, CostModelAbstract result, IvMCanceller canceller) throws Exception {
		List<Pair<double[], Double>> data = getInputsAndCost(log, result, canceller);

		if (canceller.isCancelled()) {
			return;
		}

		if (data.size() == 0) {
			message = "no cost:total trace attribute available";
			return;
		}

		computeLogCost(result, data);

		if (canceller.isCancelled()) {
			return;
		}

		//fit model
		compute(data, result, canceller);

		if (canceller.isCancelled()) {
			return;
		}

		//compute average cost in cost model
		computeModelCost(log, result, canceller);
	}

	private void computeModelCost(XLog log, CostModelAbstract result, IvMCanceller canceller) {

		BigDecimal sum = BigDecimal.ZERO;
		int count = 0;
		for (XTrace trace : log) {

			Pair<double[], Double> p = result.getInputsAndCost(trace, canceller);

			if (canceller.isCancelled()) {
				return;
			}

			if (p != null) {
				BigDecimal modelCost = innerProduct(result.getParameters(), p.getA());
				sum = sum.add(modelCost);

				count++;
			}
		}
		BigDecimal averageAbsoluteError = sum.divide(BigDecimal.valueOf(count), 10, BigDecimal.ROUND_HALF_UP);

		result.getModelProperties().add(new DataRow<Object>("cost model (global)", "total cost (model)",
				DisplayType.numeric(sum.doubleValue())));
		result.getModelProperties().add(new DataRow<Object>("cost model (global)", "average cost (model)",
				DisplayType.numeric(averageAbsoluteError.doubleValue())));
	}

	public static BigDecimal innerProduct(double[] parameters, double[] observation) {
		assert parameters.length == observation.length;
		BigDecimal sum = BigDecimal.ZERO;
		for (int i = 0; i < parameters.length; i++) {
			sum = sum.add(BigDecimal.valueOf(parameters[i] * observation[i]));
		}
		return sum;
	}

	private void computeLogCost(CostModelAbstract result, List<Pair<double[], Double>> data) {
		//compute average cost in log
		{
			BigDecimal sum = BigDecimal.ZERO;
			for (Pair<double[], Double> p : data) {
				sum = sum.add(BigDecimal.valueOf(p.getB()));
			}
			BigDecimal averageAbsoluteError = sum.divide(BigDecimal.valueOf(data.size()), 10, BigDecimal.ROUND_HALF_UP);

			result.getModelProperties().add(new DataRow<Object>("cost model (global)", "total cost (log)",
					DisplayType.numeric(sum.doubleValue())));
			result.getModelProperties().add(new DataRow<Object>("cost model (global)", "average cost (log)",
					DisplayType.numeric(averageAbsoluteError.doubleValue())));
		}
	}

	public abstract void compute(List<Pair<double[], Double>> data, CostModelAbstract result, IvMCanceller canceller)
			throws Exception;

	protected List<Pair<double[], Double>> getInputsAndCost(XLog log, CostModelAbstract result,
			IvMCanceller canceller) {
		//gather trace data
		List<Pair<double[], Double>> data = new ArrayList<>();
		{
			for (XTrace trace : log) {

				if (canceller.isCancelled()) {
					return null;
				}

				Pair<double[], Double> p = result.getInputsAndCost(trace, canceller);

				if (p != null && StatUtils.sum(p.getA()) > 0) {
					data.add(p);
				}
			}
		}
		return data;
	}
}
