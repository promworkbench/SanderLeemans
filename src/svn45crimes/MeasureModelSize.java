package svn45crimes;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;

import nl.tue.astar.AStarException;

public class MeasureModelSize implements Measure {

	public String getTitle() {
		return "modelSize";
	}

	public String getLatexTitle() {
		return "model size";
	}

	public String[] getMeasureNames() {
		return new String[] { "nodes", "edges", "nodesedges" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "nodes", "edges", "nodesedges" };
	}

	public int getNumberOfMeasures() {
		return 3;
	}

	public boolean printTime() {
		return false;
	}

	public boolean isSupportsTrees() {
		return false;
	}

	@Override
	public double[] compute(File logFile, XLog log, AcceptingPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		double[] result = new double[3];

		result[0] = model.getNet().getNodes().size();
		result[1] = model.getNet().getEdges().size();
		result[2] = model.getNet().getNodes().size() + model.getNet().getEdges().size();

		return result;
	}

	@Override
	public double[] compute(File logFile, XLog log, EfficientTree model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		return null;
	}

}
