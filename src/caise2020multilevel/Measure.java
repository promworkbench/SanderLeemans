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

public interface Measure {

	public String getTitle();

	public String getLatexTitle();

	public String[] getMeasureNames();

	public String[] getMeasureLatexNames();

	/**
	 * If true, then the tree-method will be called whenever possible. If false,
	 * only the accepting Petri net method will be called.
	 * 
	 * @return
	 */
	public boolean isSupportsTrees();

	public boolean isSupportsBPMN();

	/**
	 * If true, then the mlm-method method will be called whenever possible. If
	 * false, only the tree or accepting Petri net methods will be called.
	 * 
	 * @return
	 */
	public boolean isSupportsMultiLevelModels();

	public int getNumberOfMeasures();

	public double[] compute(XLog log, XEventClassifier combinedClassifier, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException,
			IOException, ConnectionCannotBeObtained, JSONException, Exception;

	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException;

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException;

	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException;
}