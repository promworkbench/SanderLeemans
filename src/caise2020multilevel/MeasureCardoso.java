package caise2020multilevel;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.multilevelminer.multilevelmodel.SubModel;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import gnu.trove.set.hash.THashSet;
import nl.tue.astar.AStarException;

public class MeasureCardoso implements Measure {

	public String getTitle() {
		return "Cardoso";
	}
	
	public String getLatexTitle() {
		return "Cardoso";
	}

	public String[] getMeasureNames() {
		return new String[] { "Cardoso" };
	}
	
	public String[] getMeasureLatexNames() {
		return new String[] { "Cardoso" };
	}

	public boolean isSupportsTrees() {
		return false;
	}
	
	public boolean isSupportsBPMN() {
		return false;
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

		Petrinet net = aNet.getNet();
		long sum = getValue(net);

		return new double[] { sum };
	}

	public static long getValue(Petrinet net) {
		long sum = 0;
		for (Place place : net.getPlaces()) {
			Set<Set<Place>> reachableSubsets = new THashSet<>();

			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getOutEdges(place)) {
				Transition transition = (Transition) edge.getTarget();

				Set<Place> subset = new THashSet<>();
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge2 : net.getOutEdges(transition)) {
					subset.add((Place) edge2.getTarget());
				}
				reachableSubsets.add(subset);
			}
			sum += reachableSubsets.size();
		}
		return sum;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return new double[] { processModel(model.model) };
	}

	public static <M, T> long processModel(SubModel<M, T> model) {
		Pair<AcceptingPetriNet, Map<T, List<Transition>>> anet = model.getType().model2AcceptingPetriNet(model);

		long sum = getValue(anet.getA().getNet());

		for (T transition : model.getTransitions()) {
			if (model.hasSubModel(transition)) {
				sum += processModel(model.getSubModel(transition));
			}
		}

		return sum;
	}
	
	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}
}