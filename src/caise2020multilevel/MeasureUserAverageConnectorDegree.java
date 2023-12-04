package caise2020multilevel;

import java.io.IOException;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.multilevelminer.multilevelmodel.SubModel;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree.NodeType;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureUserAverageConnectorDegree implements Measure {

	public String getTitle() {
		return "avg conn deg";
	}

	public String getLatexTitle() {
		return "avg conn deg";
	}

	public String[] getMeasureNames() {
		return new String[] { "avg conn deg" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public boolean isSupportsBPMN() {
		return true;
	}

	public boolean isSupportsMultiLevelModels() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException,
			IOException, ConnectionCannotBeObtained, JSONException, Exception {
		return new double[] { toDoublel(getAverageConnectorDegree(aNet)) };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return new double[] { toDoublel(getAverageConnectorDegree(tree)) };
	}

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return new double[] { toDoubled(getAverageConnectorDegree(model.model)) };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return new double[] { toDoublel(getAverageConnectorDegree(diagram)) };
	}

	public static <M, T> Pair<Double, Integer> getAverageConnectorDegree(SubModel<M, T> model) {
		double value;
		if (model.getModel() instanceof AcceptingPetriNet) {
			value = toDoublel(getAverageConnectorDegree((AcceptingPetriNet) model.getModel()));
		} else if (model.getModel() instanceof EfficientTree) {
			value = toDoublel(getAverageConnectorDegree((EfficientTree) model.getModel()));
		} else if (model.getModel() instanceof DirectlyFollowsModel) {
			value = toDoublel(getAverageConnectorDegree((DirectlyFollowsModel) model.getModel()));
		} else if (model.getModel() instanceof BPMNDiagram) {
			value = toDoublel(getAverageConnectorDegree((BPMNDiagram) model.getModel()));
		} else {
			assert (false);
			return null;
		}

		double sum = value;
		int count = 1;
		for (T transition : model.getTransitions()) {
			if (model.hasSubModel(transition)) {
				Pair<Double, Integer> q = getAverageConnectorDegree(model.getSubModel(transition));
				sum += q.getA();
				count += q.getB();
			}
		}

		return Pair.of(sum, count);
	}

	public static double toDoublel(Pair<Long, Integer> p) {
		return p.getA() / (p.getB() * 1.0);
	}

	public static double toDoubled(Pair<Double, Integer> p) {
		return p.getA() / (p.getB() * 1.0);
	}

	public static Pair<Long, Integer> getAverageConnectorDegree(EfficientTree tree) {
		long sum = 0;
		int count = 0;
		for (int node = 0; node < tree.getMaxNumberOfNodes(); node++) {
			NodeType type = tree.getNodeType(node);
			switch (type) {
				case activity :
				case tau :
					count++;
					sum++;
					break;
				case concurrent :
				case interleaved :
				case loop :
				case or :
				case sequence :
				case xor :
					count++;
					sum += tree.getNumberOfChildren(node) + 1;
					break;
				case skip :
					break;
				default :
					break;
			}
		}
		return Pair.of(sum, count);
	}

	public static Pair<Long, Integer> getAverageConnectorDegree(DirectlyFollowsModel model) {
		int count = model.getNumberOfNodes();
		long sum = 0;
		for (long edge : model.getEdges()) {
			sum++;
		}
		return Pair.of(sum, count);
	}

	public static Pair<Long, Integer> getAverageConnectorDegree(AcceptingPetriNet net) {
		int count = net.getNet().getNodes().size();
		long sum = net.getNet().getEdges().size() * 2;
		return Pair.of(sum, count);
	}

	public static Pair<Long, Integer> getAverageConnectorDegree(BPMNDiagram diagram) {
		int count = diagram.getNodes().size();
		long sum = diagram.getEdges().size() * 2;
		return Pair.of(sum, count);
	}
}