package caise2020isextension;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.LanguageGenerationStrategyFromModelImpl;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureEarthMovers implements Measure {

	public String getTitle() {
		return "EMSC";
	}

	public String getLatexTitle() {
		return "EMSC~\\cite{leemans2021stochastic}";
	}

	public String[] getMeasureNames() {
		return new String[] { "EMSC" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "EMSC" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return true;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		EMSCParametersLogModelAbstract emscParameters = new EMSCParametersLogModelDefault();
		emscParameters.setModelTerminationStrategy(
				new LanguageGenerationStrategyFromModelImpl(3600000, 0.8, Integer.MAX_VALUE));
		emscParameters.setDebug(true);
		emscParameters.setComputeStochasticTraceAlignments(false);

		StochasticTraceAlignmentsLogModel p = EarthMoversStochasticConformancePlugin.measureLogModel(log, model,
				initialMarking, emscParameters, new ProMCanceller() {
					public boolean isCancelled() {
						return false;
					}
				});
		return new double[] { p.getSimilarity() };
	}

}
