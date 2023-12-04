package svn55datastochastics;

import org.deckfour.xes.model.XLog;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPN;
import org.processmining.stochasticlabelleddatapetrinet.weights.ConstantWeightFunction;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeights;

import nl.tue.astar.AStarException;

public class MeasureModel implements Measure {
	public String getTitle() {
		return "model";
	}

	public String getLatexTitle() {
		return "model";
	}

	public String[] getMeasureNames() {
		return new String[] { "transitions", "silent transitions", "non-1 weights", "data weights" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "", "silent", "non-1", "variables" };
	}

	public String[] getMeasureFormatting() {
		return new String[] { "0", "0", "0", "0" };
	}

	public int getNumberOfMeasures() {
		return 4;
	}

	public boolean printTime() {
		return false;
	}

	public double[] compute(XLog log, StochasticLabelledPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		double[] result = new double[4];

		result[0] = model.getNumberOfTransitions();

		//silent
		{
			int count = 0;
			for (int i = 0; i < model.getNumberOfTransitions(); i++) {
				if (model.isTransitionSilent(i)) {
					count++;
				}
			}
			result[1] = count;
		}

		//non-1
		{
			int count = 0;
			for (int i = 0; i < model.getNumberOfTransitions(); i++) {
				if (((StochasticLabelledPetriNetSimpleWeights) model).getTransitionWeight(i) != 1) {
					count++;
				}
			}
			result[2] = count;
		}

		//data
		result[3] = 0;

		return result;
	}

	public double[] compute(XLog log, SLDPN model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		double[] result = new double[4];

		result[0] = model.getModel().getNumberOfTransitions();

		//silent
		{
			int count = 0;
			for (int i = 0; i < model.getModel().getNumberOfTransitions(); i++) {
				if (model.getModel().isTransitionSilent(i)) {
					count++;
				}
			}
			result[1] = count;
		}

		//non-1
		{
			int count = 0;
			for (int transition = 0; transition < model.getModel().getNumberOfTransitions(); transition++) {
				if (model.getModel().getWeightFunction(transition) instanceof ConstantWeightFunction) {
					count++;
				}
			}
			result[2] = count;
		}

		//data
		result[3] = model.getModel().getNumberOfVariables();
		assert false;

		return result;
	}
}