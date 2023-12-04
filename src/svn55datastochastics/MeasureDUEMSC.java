package svn55datastochastics;

import org.deckfour.xes.classification.XEventNameClassifier;
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
import org.processmining.stochasticlabelleddatapetrinet.plugins.DataUnitEarthMoversStochasticConformancePlugin;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPN;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import nl.tue.astar.AStarException;

public class MeasureDUEMSC implements Measure {
	public String getTitle() {
		return "duemsc";
	}

	public String getLatexTitle() {
		return "duEMSC";
	}

	public String[] getMeasureNames() {
		return new String[] { "duemsc" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "" };
	}

	public String[] getMeasureFormatting() {
		return new String[] { "0.0000" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return true;
	}

	public boolean supportsDataModels() {
		return true;
	}

	public double[] compute(XLog log, StochasticLabelledPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		double[] result = new double[1];
		uEMSCParametersAbstract parameters = new uEMSCParametersDefault();
		parameters.setDebug(true);
		result[0] = UnitEarthMoversStochasticConformancePlugin.measureLogModel(log, model.getDefaultSemantics(),
				parameters, canceller);
		return result;
	}

	public double[] compute(XLog log, SLDPN model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		double[] result = new double[1];
		result[0] = DataUnitEarthMoversStochasticConformancePlugin.measureLogModel(log, new XEventNameClassifier(),
				model, true, canceller);
		return result;
	}
}