package svn48healthcare;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.deckfour.xes.extension.std.XCostExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;

public abstract class CostModelAbstract implements CostModel {

	public static final String attribute = XCostExtension.KEY_TOTAL;
	public static final int ms2hr = 1000 * 60 * 60;

	protected double[] parameters;
	private List<DataRow<Object>> modelProperties = new ArrayList<>();

	/**
	 * No side effects allowed. Will only be called for non-silent activities.
	 * 
	 * @param node
	 * @param initiate
	 * @param enqueue
	 * @param start
	 * @param complete
	 * @return
	 */
	public abstract double[] getInputs(XEvent previous, XEvent current);

	public abstract int getNumberOfParameters();

	public Pair<double[], Double> getInputsAndCost(XTrace trace, IvMCanceller canceller) {
		double[] inputs = new double[getNumberOfParameters()];

		double traceCost = getCost(trace);

		if (Double.isNaN(traceCost)) {
			return null;
		}

		//capture activity instances
		{
			XEvent last = null;
			for (XEvent current : trace) {

				if (canceller.isCancelled()) {
					return null;
				}

				double[] inputsA = getInputs(last, current);

				merge(inputs, inputsA);

				last = current;
			}
		}

		return Pair.of(inputs, traceCost);
	}

	private void merge(double[] inputs, double[] inputsA) {
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] += inputsA[i];
		}
	}

	public double[] getParameters() {
		return parameters;
	}

	public void setParameters(double[] parameters) {
		this.parameters = parameters;
	}

	public List<DataRow<Object>> getModelRepresentation(Collection<XTrace> traces) {
		List<DataRow<Object>> result = new ArrayList<>();
		result.addAll(getModelProperties());
		result.add(new DataRow<Object>("cost model (global)", "number of parameters",
				DisplayType.numeric(parameters.length)));

		//trace-list specific things
		{
			//compute average cost in log
			{
				BigDecimal sumLog = BigDecimal.ZERO;
				BigDecimal sumModel = BigDecimal.ZERO;
				BigDecimal sumError = BigDecimal.ZERO;
				int sizeLog = 0;
				int sizeModel = 0;
				int sizeError = 0;
				for (XTrace trace : traces) {
					Pair<double[], Double> p = getInputsAndCost(trace, IvMCanceller.neverCancel);
					BigDecimal modelCost = getModelCost(trace);

					if (p != null) {
						sumLog = sumLog.add(BigDecimal.valueOf(p.getB()));
						sizeLog++;
					}

					if (modelCost != null) {
						sumModel = sumModel.add(modelCost);
						sizeModel++;
					}

					if (p != null && modelCost != null) {
						sumError = sumError.add(modelCost.subtract(BigDecimal.valueOf(p.getB())).abs());
						sizeError++;
					}
				}

				result.add(new DataRow<Object>("cost model", "total cost (log)",
						DisplayType.numeric(sumLog.doubleValue())));
				if (sizeLog > 0) {
					BigDecimal averageLog = sumLog.divide(BigDecimal.valueOf(sizeLog), 10, BigDecimal.ROUND_HALF_UP);
					result.add(new DataRow<Object>("cost model", "average cost (log)",
							DisplayType.numeric(averageLog.doubleValue())));
				} else {
					result.add(new DataRow<Object>("cost model", "average cost (log)", DisplayType.NA()));
				}

				result.add(new DataRow<Object>("cost model", "total cost (model)",
						DisplayType.numeric(sumModel.doubleValue())));
				if (sizeModel > 0) {
					BigDecimal averageModel = sumModel.divide(BigDecimal.valueOf(sizeModel), 10,
							BigDecimal.ROUND_HALF_UP);
					result.add(new DataRow<Object>("cost model", "average cost (model)",
							DisplayType.numeric(averageModel.doubleValue())));
				}

				result.add(new DataRow<Object>("cost model", "total absolute error cost",
						DisplayType.numeric(sumError.doubleValue())));
				if (sizeError > 0) {
					BigDecimal averageError = sumError.divide(BigDecimal.valueOf(sizeError), 10,
							BigDecimal.ROUND_HALF_UP);
					result.add(new DataRow<Object>("cost model", "average absolute error cost",
							DisplayType.numeric(averageError.doubleValue())));
				} else {
					result.add(new DataRow<Object>("cost model", "average absolute error cost", DisplayType.NA()));
				}
			}
		}

		return result;

	}

	@Override
	public BigDecimal getModelCost(XTrace trace) {

		Pair<double[], Double> p = getInputsAndCost(trace, IvMCanceller.neverCancel);

		if (p != null) {
			return CostModelComputerAbstract.innerProduct(parameters, p.getA());
		}

		return null;
	}

	public List<DataRow<Object>> getModelProperties() {
		return modelProperties;
	}

	public void setModelProperties(List<DataRow<Object>> modelProperties) {
		this.modelProperties = modelProperties;
	}

	public static final DecimalFormat formatE = new DecimalFormat("0.###E0");
	public static final DecimalFormat format = new DecimalFormat("0.##");

	public static final String format(double value) {
		if (value == 0) {
			return "0";
		}
		if (Math.abs(value) > 9999999 || Math.abs(value) < 0.01) {
			return formatE.format(value);
		}
		return format.format(value);
	}
}
