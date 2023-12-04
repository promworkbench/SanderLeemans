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

public class MeasureStructure implements Measure {
	public String getTitle() {
		return "structure";
	}

	public String getLatexTitle() {
		return "structure";
	}

	public String[] getMeasureNames() {
		return new String[] { "structure" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "structure" };
	}

	public int getNumberOfMeasures() {
		return 1;
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
		double[] result = new double[1];

		double t = model.getNet().getTransitions().size() * 1.0;
		double p = model.getNet().getPlaces().size() * 1.0;
		double e = model.getNet().getEdges().size();

		if (t != 0 && p != 0) {
			result[0] = 1 - e / (t * p * 2);
		} else {
			result[0] = 1;
		}

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
