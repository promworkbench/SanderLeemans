package svn48healthcare;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;

import gnu.trove.set.hash.THashSet;

public class DecisionTree2CostModel {

	public static Pair<CostModelAbstract, List<String>> computeModel(XLog log, XEventClassifier classifier)
			throws Exception {
		Set<String> activities = getActivities(log, classifier);
		List<String> activities2 = new ArrayList<>(activities);

		CostModelAbstract costModel = new CostModelImplExecutions(activities2, classifier);
		new CostModelComputerImplLP().compute(log, costModel, IvMCanceller.neverCancel);
		return Pair.of(costModel, activities2);
	}

	private static Set<String> getActivities(XLog log, XEventClassifier classifier) {
		THashSet<String> result = new THashSet<>();

		for (XTrace trace : log) {
			for (XEvent event : trace) {
				result.add(classifier.getClassIdentity(event));
			}
		}

		return result;
	}

}