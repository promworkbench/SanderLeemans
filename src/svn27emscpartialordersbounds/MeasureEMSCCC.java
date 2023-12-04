package svn27emscpartialordersbounds;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogTotalModelPartialCertainDefault;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePartialOrderPlugin;
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

import nl.tue.astar.AStarException;

public class MeasureEMSCCC implements Measure {

	public String getTitle() {
		return "EMSCcc";
	}

	public String getLatexTitle() {
		return "EMSCcc";
	}

	public String[] getMeasureNames() {
		return new String[] { "lower bound", "upper bound" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "lower bound", "upper bound" };
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public boolean printTime() {
		return true;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call, int q, int r)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		EMSCParametersLogTotalModelPartialCertainDefault parameters = new EMSCParametersLogTotalModelPartialCertainDefault();
		parameters.getGenerationStrategyB().setNumberOfTracesWithHighestProbability(q);
		parameters.getGenerationStrategyB().setNumberOfTracesRandomWalk(r);
		parameters.getGenerationStrategyB().setSeed(1);
		Pair<Double, Double> p = EarthMoversStochasticConformancePartialOrderPlugin.measureLogModel(log, model,
				initialMarking, parameters, canceller);

		return new double[] { p.getA(), p.getB() };
	}

}
