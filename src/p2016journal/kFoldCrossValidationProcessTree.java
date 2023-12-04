package p2016journal;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.processtree.ProcessTree;
import org.processmining.projectedrecallandprecision.framework.CompareParameters;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2ProcessTreePlugin;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult;

import dk.brics.automaton2.BasicAutomata;

public abstract class kFoldCrossValidationProcessTree extends kFoldCrossValidation<ProcessTree> {

	@Override
	public Triple<Double, Double, Double> evaluate(XLog testLog, ProcessTree model, XEventClassifier classifier)
			throws Exception {
		CompareParameters compareParameters = new CompareParameters(2);
		compareParameters.setClassifier(classifier);
		ProjectedRecallPrecisionResult result = CompareLog2ProcessTreePlugin.measure(testLog, model, compareParameters,
				BasicAutomata.notCanceller);
		double simplicity = getSimplicity(ProcessTree2EfficientTree.convert(model), testLog);
		return Triple.of(result.getRecall(), result.getPrecision(), simplicity);
	}

	public double getSimplicity(EfficientTree tree, XLog log) {
		//the minimum simplicity is a single root with n children
		int activities = 0;
		int nodesToGo = 1;
		int node = 0;
		while (nodesToGo > 0) {
			if (tree.isActivity(node)) {
				activities++;
			} else if (tree.isOperator(node)) {
				nodesToGo += tree.getNumberOfChildren(node);
			}
			nodesToGo--;
			node++;
		}
		int minimum;
		if (activities == 1) {
			minimum = 1;
		} else {
			minimum = activities + 1;
		}

		//the maximum is one xor + one sequence per trace + one activity per event
		int numberOfEvents = 0;
		for (XTrace trace : log) {
			numberOfEvents += trace.size();
		}
		int maximum = 1 + log.size() + numberOfEvents;

		return Math.max(0, 1 - minimum / (maximum * 1.0));
	}
}
