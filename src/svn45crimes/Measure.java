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

public interface Measure {

	public String getTitle();

	public String getLatexTitle();

	public String[] getMeasureNames();

	public String[] getMeasureLatexNames();

	public int getNumberOfMeasures();

	public boolean printTime();

	public boolean isSupportsTrees();

	public double[] compute(File logFile, XLog log, AcceptingPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception;

	public double[] compute(File logFile, XLog log, EfficientTree model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception;
}