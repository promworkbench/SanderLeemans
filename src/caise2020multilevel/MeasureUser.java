package caise2020multilevel;

import java.io.IOException;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureUser implements Measure {

	public String getTitle() {
		return "user complexity";
	}

	public String getLatexTitle() {
		return "user";
	}

	public String[] getMeasureNames() {
		return new String[] { "avg size", "avg conn deg" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "avg size", "avg conn deg" };
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
		return 2;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException,
			IOException, ConnectionCannotBeObtained, JSONException, Exception {
		double size = MeasureUserSize.getSize(aNet);
		double acd = MeasureUserAverageConnectorDegree
				.toDoublel(MeasureUserAverageConnectorDegree.getAverageConnectorDegree(aNet));
		return new double[] { size, acd };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		double size = MeasureUserSize.getSize(tree);
		double acd = MeasureUserAverageConnectorDegree
				.toDoublel(MeasureUserAverageConnectorDegree.getAverageConnectorDegree(tree));
		return new double[] { size, acd };
	}

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		double size = MeasureUserSize.toDouble(MeasureUserSize.getSize(model.model));
		double acd = MeasureUserAverageConnectorDegree
				.toDoubled(MeasureUserAverageConnectorDegree.getAverageConnectorDegree(model.model));
		return new double[] { size, acd };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		double size = MeasureUserSize.getSize(diagram);
		double acd = MeasureUserAverageConnectorDegree
				.toDoublel(MeasureUserAverageConnectorDegree.getAverageConnectorDegree(diagram));
		return new double[] { size, acd };
	}

}