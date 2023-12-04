package caise2020multilevel;

import java.io.IOException;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureAutomated implements Measure {

	public String getTitle() {
		return "automated complexity";
	}

	public String getLatexTitle() {
		return "automated";
	}

	public String[] getMeasureNames() {
		return new String[] { "size", "avg con deg" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "size", "avg con deg" };
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
		return 2;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException,
			IOException, ConnectionCannotBeObtained, JSONException, Exception {

		int flatSize = aNet.getNet().getPlaces().size() + aNet.getNet().getTransitions().size()
				+ aNet.getNet().getEdges().size();

		Pair<Long, Integer> p = MeasureFlatAverageConnectorDegree.getValue(aNet.getNet());

		return new double[] { flatSize, p.getA() / (p.getB() * 1.0) };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}
}
