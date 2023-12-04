package svn53longdistancedependenciesresample;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.uEMSCParametersAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.uEMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.plugins.UnitEarthMoversStochasticConformancePlugin;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import nl.tue.astar.AStarException;

public class MeasureUEMSC implements Measure {
	public String getTitle() {
		return "uemsc";
	}

	public String getLatexTitle() {
		return "uEMSC";
	}

	public String[] getMeasureNames() {
		return new String[] { "uemsc" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "" };
	}

	public String[] getMeasureFormatting() {
		return new String[] { "0.00" };
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
		
		uEMSCParametersAbstract parameters = new uEMSCParametersDefault();
		parameters.setDebug(true);
		result[0] = UnitEarthMoversStochasticConformancePlugin.measureLogModel(log, model.getDefaultSemantics(), parameters, canceller);

		return result;
	}
}
