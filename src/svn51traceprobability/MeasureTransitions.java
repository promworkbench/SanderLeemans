package svn51traceprobability;

import org.deckfour.xes.model.XLog;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import nl.tue.astar.AStarException;

public class MeasureTransitions implements Measure {

	public String getTitle() {
		return "transitions";
	}

	public String getLatexTitle() {
		return "transitions";
	}

	public String[] getMeasureNames() {
		return new String[] { "transitions" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "transitions" };
	}

	public String[] getMeasureFormatting() {
		return new String[] { "0" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return false;
	}

	public double[] compute(XLog log, StochasticLabelledPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		double[] result = new double[1];

		result[0] = model.getNumberOfTransitions();

		return result;
	}

}
