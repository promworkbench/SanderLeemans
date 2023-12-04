package svn45crimes;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.framework.CompareParameters;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2EfficientTreePlugin;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2PetriNetPlugin;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;

import nl.tue.astar.AStarException;

public class MeasureProjected implements Measure {
	public String getTitle() {
		return "project";
	}

	public String getLatexTitle() {
		return "projected";
	}

	public String[] getMeasureNames() {
		return new String[] { "fitness", "precision" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "fitness", "precision" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public boolean printTime() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public double[] compute(File logFile, XLog log, AcceptingPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		CompareParameters parameters = new CompareParameters();
		parameters.setThreads(10);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		ProjectedRecallPrecisionResult m = CompareLog2PetriNetPlugin.measure(log, model, parameters, canceller);
		if (m.getNumberOfFailedMeasures() > 0) {
			throw new Exception("not successful");
		}
		return new double[] { m.getRecall(), m.getPrecision() };
	}

	public double[] compute(File logFile, XLog log, EfficientTree model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		CompareParameters parameters = new CompareParameters();
		parameters.setThreads(10);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		ProjectedRecallPrecisionResult m = CompareLog2EfficientTreePlugin.measure(log, model, parameters, canceller);
		if (m.getNumberOfFailedMeasures() > 0) {
			throw new Exception("not successful");
		}
		return new double[] { m.getRecall(), m.getPrecision() };
	}
}
