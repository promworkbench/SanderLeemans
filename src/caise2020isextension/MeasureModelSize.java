package caise2020isextension;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureModelSize implements Measure {

	public String getTitle() {
		return "modelSize";
	}

	public String getLatexTitle() {
		return "model size";
	}

	public String[] getMeasureNames() {
		return new String[] { "nodes", "edges" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "nodes", "edges" };
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public boolean printTime() {
		return false;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		double[] result = new double[2];

		result[0] = model.getNodes().size();
		result[1] = model.getEdges().size();

		return result;
	}

}
