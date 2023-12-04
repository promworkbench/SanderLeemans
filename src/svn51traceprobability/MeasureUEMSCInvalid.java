package svn51traceprobability;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.uEMSCParametersAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.uEMSCParametersDefault;
import org.processmining.earthmoversstochasticconformancechecking.plugins.UnitEarthMoversStochasticConformancePlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import nl.tue.astar.AStarException;
import thesis.helperClasses.FakeContext;

public class MeasureUEMSCInvalid implements Measure {
	public String getTitle() {
		return "uemsc-i";
	}

	public String getLatexTitle() {
		return "uEMSC training log";
	}

	public String[] getMeasureNames() {
		return new String[] { "uemsc-i" };
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

		PluginContext context = new FakeContext();
		XLog invalidLog = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getLogFile());

		uEMSCParametersAbstract parameters = new uEMSCParametersDefault();
		parameters.setDebug(true);
		result[0] = UnitEarthMoversStochasticConformancePlugin.measureLogModel(invalidLog, model.getDefaultSemantics(),
				parameters, canceller);

		return result;
	}
}
