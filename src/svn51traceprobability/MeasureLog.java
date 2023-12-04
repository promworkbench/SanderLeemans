package svn51traceprobability;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;

import gnu.trove.set.hash.THashSet;
import nl.tue.astar.AStarException;

public class MeasureLog implements Measure {

	public String getTitle() {
		return "log";
	}

	public String getLatexTitle() {
		return "log";
	}

	public String[] getMeasureNames() {
		return new String[] { "traces", "events", "activities" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "traces", "events", "activities" };
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
		double[] results = new double[3];

		results[0] = log.size();

		XEventClassifier classifier = MiningParametersIM.getDefaultClassifier();
		Set<String> activities = new THashSet<>();
		results[1] = 0;
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String activity = classifier.getClassIdentity(event);
				activities.add(activity);
				results[1]++;
			}
		}
		results[2] = activities.size();

		return results;
	}

}
