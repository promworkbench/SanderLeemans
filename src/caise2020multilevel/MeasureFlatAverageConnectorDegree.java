package caise2020multilevel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.multilevelminer.multilevelmodel.SubModel;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureFlatAverageConnectorDegree implements Measure {

	public String getTitle() {
		return "flat avg conn deg";
	}
	
	public String getLatexTitle() {
		return "flat avg conn deg";
	}

	public String[] getMeasureNames() {
		return new String[] { "flat avg conn deg" };
	}
	
	public String[] getMeasureLatexNames() {
		return new String[] { "avg conn deg" };
	}

	public boolean isSupportsTrees() {
		return false;
	}
	
	public boolean isSupportsBPMN() {
		return false;
	}

	public boolean isSupportsMultiLevelModels() {
		return false;
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException,
			IOException, ConnectionCannotBeObtained, JSONException, Exception {
		Pair<Long, Integer> p = getValue(aNet.getNet());
		return new double[] { p.getA() / (p.getB() * 1.0) };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}

	public static <M, T> Pair<Long, Integer> processModel(SubModel<M, T> model) {
		Pair<AcceptingPetriNet, Map<T, List<Transition>>> anet = model.getType().model2AcceptingPetriNet(model);

		Pair<Long, Integer> p = getValue(anet.getA().getNet());
		long sum = p.getA();
		int count = p.getB();

		for (T transition : model.getTransitions()) {
			if (model.hasSubModel(transition)) {
				Pair<Long, Integer> q = processModel(model.getSubModel(transition));
				sum += q.getA();
				count += q.getB();
			}
		}

		return Pair.of(sum, count);
	}

	public static Pair<Long, Integer> getValue(Petrinet net) {
		long sum = 0;
		int count = 0;
		for (Place place : net.getPlaces()) {
			count++;
			sum += net.getInEdges(place).size() + net.getOutEdges(place).size();
		}
		for (Transition transition : net.getTransitions()) {
			count++;
			sum += net.getInEdges(transition).size() + net.getOutEdges(transition).size();
		}

		return Pair.of(sum, count);
	}
	
	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}
}