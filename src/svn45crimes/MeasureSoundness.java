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
import thesis.helperClasses.CheckRelaxedSoundnessWithLola;

public class MeasureSoundness implements Measure {

	public String getTitle() {
		return "soundness";
	}

	public String getLatexTitle() {
		return "soundness";
	}

	public String[] getMeasureNames() {
		return new String[] { "soundness" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "soundness" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return false;
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public double[] compute(File logFile, XLog log, AcceptingPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		boolean result = CheckRelaxedSoundnessWithLola.isSound(model);

		if (result) {
			return new double[] { 1 };
		}
		return new double[] { 0 };
	}

	public double[] compute(File logFile, XLog log, EfficientTree model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		return new double[] { 1 };
	}

}
