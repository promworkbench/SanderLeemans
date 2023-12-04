package svn53longdistancedependenciesresample;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelDefault;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import nl.tue.astar.AStarException;

public class MeasureEMSC implements Measure {

	public String getTitle() {
		return "emsc";
	}

	public String getLatexTitle() {
		return "EMSC";
	}

	public String[] getMeasureNames() {
		return new String[] { "emsc" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "" };
	}

	public String[] getMeasureFormatting() {
		return new String[] { "0.000" };
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

	public double[] compute(XLog log, StochasticLabelledPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		double[] result = new double[1];

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		EMSCParametersLogModelAbstract parameters = new EMSCParametersLogModelDefault();
		parameters.setComputeStochasticTraceAlignments(false);
		parameters.setNumberOfThreads(10);
		parameters.getTerminationStrategyB().setMaxDuration(100000);
		parameters.getTerminationStrategyB().setMaxTraces(50000);
		parameters.setDebug(true);
		StochasticTraceAlignmentsLogModel ta = EarthMoversStochasticConformancePlugin.measureLogModel(log, model,
				parameters, canceller);

		result[0] = ta.getSimilarity();

		return result;
	}

}