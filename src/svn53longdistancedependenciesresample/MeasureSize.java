package svn53longdistancedependenciesresample;

import org.deckfour.xes.model.XLog;
import org.processmining.longdistancedependencies.StochasticLabelledPetriNetAdjustmentWeights;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeights;

import nl.tue.astar.AStarException;

public class MeasureSize implements Measure {

	public String getTitle() {
		return "size";
	}

	public String getLatexTitle() {
		return "size";
	}

	public String[] getMeasureNames() {
		return new String[] { "nodes", "edges", "weights" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "nodes", "edges", "weights" };
	}

	public String[] getMeasureFormatting() {
		return new String[] { "0", "0", "0" };
	}

	public int getNumberOfMeasures() {
		return 3;
	}

	public boolean printTime() {
		return false;
	}

	public double[] compute(XLog log, StochasticLabelledPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		double[] result = new double[3];

		result[0] = model.getNumberOfPlaces() + model.getNumberOfTransitions();

		result[1] = 0;
		for (int place = 0; place < model.getNumberOfPlaces(); place++) {
			result[1] += model.getOutputTransitions(place).length + model.getInputTransitions(place).length;
		}

		result[2] = 0;
		if (model instanceof StochasticLabelledPetriNetSimpleWeights) {
			for (int transition = 0; transition < model.getNumberOfTransitions(); transition++) {
				if (((StochasticLabelledPetriNetSimpleWeights) model).getTransitionWeight(transition) != 1) {
					result[2]++;
				}
			}
		} else if (model instanceof StochasticLabelledPetriNetAdjustmentWeights) {
			for (int transitionA = 0; transitionA < model.getNumberOfTransitions(); transitionA++) {
				if (((StochasticLabelledPetriNetAdjustmentWeights) model).getTransitionBaseWeight(transitionA) != 1) {
					result[2]++;
				}

				for (int transitionB = 0; transitionB < model.getNumberOfTransitions(); transitionB++) {
					if (((StochasticLabelledPetriNetAdjustmentWeights) model).getTransitionAdjustmentWeight(transitionA,
							transitionB) != 1) {
						result[2]++;
					}
				}
			}
		}

		return result;
	}

}
