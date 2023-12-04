package coopis2018;

import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public interface Measure {

	public String getTitle();

	public String[] getMeasureNames();

	/**
	 * If true, then the tree-method will be called whenever possible. If false,
	 * only the accepting Petri net method will be called.
	 * 
	 * @return
	 */
	public boolean isSupportsTrees();

	public int getNumberOfMeasures();

	public double[] compute(XLog log, AcceptingPetriNet aNet) throws AStarException, ProjectedMeasuresFailedException,
			AutomatonFailedException, InterruptedException, IOException, ConnectionCannotBeObtained, JSONException, Exception;

	public double[] compute(XLog log, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException;

	/**
	 * For repeated measures. The number of measures from the end of the list
	 * that should be combined into a single value.
	 * 
	 * @return
	 */
	public int getCombinedMeasures();
}
