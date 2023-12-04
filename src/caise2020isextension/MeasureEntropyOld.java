package caise2020isextension;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticawareconformancechecking.plugins.RelativeEntropyPlugin;

import nl.tue.astar.AStarException;

public class MeasureEntropyOld implements Measure {

	public String getTitle() {
		return "entO";
	}

	public String getLatexTitle() {
		return "entropy";
	}

	public String[] getMeasureNames() {
		return new String[] { "stochastic recall", "stochastic precision" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "recall", "precision" };
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public boolean printTime() {
		return true;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException {
		Pair<Double, Double> r = RelativeEntropyPlugin.compute(log, model, new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}

		});
		return new double[] { r.getA(), r.getB() };
	}

}
