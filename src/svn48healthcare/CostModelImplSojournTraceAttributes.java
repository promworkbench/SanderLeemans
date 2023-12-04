package svn48healthcare;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class CostModelImplSojournTraceAttributes extends CostModelAbstract {

	public static final double ms2hr = 1000 * 60 * 60;

	private final int numberOfParameters;
	private final TObjectIntMap<String> node2index;

	private final XEventClassifier classifier;
	private final String[] attributes;

	public static enum ParameterType {
		attribute {
			public String toString() {
				return "trace attribute";
			}
		}
	}

	public static enum ParameterNodeType {
		execution {
			public String toString() {
				return "execution";
			}
		},
		perHr {
			public String toString() {
				return "sojourn time (/hr)";
			}
		}
	}

	public CostModelImplSojournTraceAttributes(Set<String> activities, XEventClassifier classifier,
			String... attributes) {
		this.classifier = classifier;
		this.attributes = attributes;
		int index = 0;

		//count nodes in model
		int nodes = activities.size();

		{
			node2index = new TObjectIntHashMap<String>(nodes, 0.5f, -1);
			for (String activity : activities) {
				node2index.put(activity, index);
				index += ParameterNodeType.values().length;
			}
		}

		numberOfParameters = index + attributes.length;
	}

	@Override
	public String getName() {
		return "execution, sojourn time (/hr) and trace attributes";
	}

	@Override
	public int getNumberOfParameters() {
		return numberOfParameters;
	}

	@Override
	public double[] getInputs(XEvent previous, XEvent current) {
		double[] result = new double[numberOfParameters];

		int nodeIndex = node2index.get(classifier.getClassIdentity(current));
		result[nodeIndex + ParameterNodeType.execution.ordinal()]++;

		if (previous != null) {
			//record sojourn time
			Date start = XTimeExtension.instance().extractTimestamp(previous);
			Date end = XTimeExtension.instance().extractTimestamp(current);

			double hrs = (end.getTime() - start.getTime()) / ms2hr;

			result[nodeIndex + ParameterNodeType.perHr.ordinal()] += hrs;
		}

		return result;
	}

	/**
	 * Times/durations will be in ms.
	 * 
	 * @param node
	 * @param parameterType
	 * @return
	 */
	public double getNodeParameter(String activity, ParameterNodeType parameterType) {
		return parameters[node2index.get(activity) + parameterType.ordinal()];
	}

	@Override
	public double getCost(XTrace trace) {
		if (!trace.hasAttributes()) {
			return Double.NaN;
		}

		if (!trace.getAttributes().containsKey(attribute)) {
			return Double.NaN;
		}

		XAttribute att = trace.getAttributes().get(attribute);
		double value = AttributeUtils.parseDoubleFast(att);
		if (value == -Double.MAX_VALUE) {
			return Double.NaN;
		}

		return value;
	}

	@Override
	public List<DataRow<Object>> getModelRepresentation(Collection<XTrace> traces) {
		List<DataRow<Object>> result = super.getModelRepresentation(traces);

		for (Object activity : node2index.keys()) {
			for (ParameterNodeType parameterType : ParameterNodeType.values()) {
				double value = getNodeParameter((String) activity, parameterType);
				result.add(new DataRow<Object>(DisplayType.numeric(value), "cost model (global)", (String) activity,
						"cost of " + parameterType.toString()));
			}
		}

		return result;
	}

}
