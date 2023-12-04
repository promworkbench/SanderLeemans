package p2015sosym.helperclasses;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgImpl;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

public class GenerateCompleteDfg {
	@Plugin(name = "Generate directly-follows graph from efficient tree - complete", returnLabels = {
			"Dfg" }, returnTypes = { Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg tree2dfg(PluginContext context, EfficientTree tree) {
		return tree2dfg(tree, 0);
	}

	/**
	 * Assumption: tree does not contain taus & third loop-child is a tau
	 * 
	 * @param tree
	 * @param node
	 * @return
	 */
	public static Dfg tree2dfg(EfficientTree tree, int node) {
		if (tree.isTau(node)) {
			return DfgImpl.createTimeOptimised(0);
		} else if (tree.isActivity(node)) {
			Dfg result = DfgImpl.createTimeOptimised(1);
			XEventClass a = new XEventClass(tree.getActivityName(node), tree.getActivity(node));
			result.addActivity(a);
			result.addStartActivity(a, 1);
			result.addEndActivity(a, 1);
			return result;
		} else if (tree.isOperator(node)) {
			//collect the children dfgs
			int numberOfChildren = tree.getNumberOfChildren(node);
			int numberOfActivities = 0;
			Dfg[] childrenDfgs = new Dfg[numberOfChildren];
			for (int i = 0; i < numberOfChildren; i++) {
				int child = tree.getChild(node, i);
				childrenDfgs[i] = tree2dfg(tree, child);
				numberOfActivities += childrenDfgs[i].getDirectlyFollowsGraph().getNumberOfVertices();
			}

			//copy the children
			Dfg result = DfgImpl.createTimeOptimised(numberOfActivities);
			for (int i = 0; i < numberOfChildren; i++) {
				put(childrenDfgs[i], result);

				if (tree.isXor(node) || tree.isConcurrent(node) || tree.isInterleaved(node)) {
					//copy the start and end activities
					result.addStartActivities(childrenDfgs[i]);
					result.addEndActivities(childrenDfgs[i]);
				}
			}

			if (tree.isXor(node)) {
				return result;
			} else if (tree.isConcurrent(node)) {
				for (int i = 0; i < numberOfChildren; i++) {
					for (int j = i + 1; j < numberOfChildren; j++) {
						for (XEventClass ai : childrenDfgs[i].getActivities()) {
							for (XEventClass aj : childrenDfgs[j].getActivities()) {
								result.addDirectlyFollowsEdge(ai, aj, 1);
								result.addDirectlyFollowsEdge(aj, ai, 1);
								result.addParallelEdge(ai, aj, 1);
							}
						}
					}
				}
				return result;
			} else if (tree.isSequence(node)) {
				for (int i = 0; i < numberOfChildren; i++) {
					if (i == 0) {
						//add start activities
						result.addStartActivities(childrenDfgs[i]);
					} else {
						for (XEventClass ai : childrenDfgs[i - 1].getEndActivities()) {
							for (XEventClass aj : childrenDfgs[i].getStartActivities()) {
								result.addDirectlyFollowsEdge(ai, aj, 1);
							}
						}
					}
				}
				//add end activities
				result.addEndActivities(childrenDfgs[numberOfChildren - 1]);
				return result;
			} else if (tree.isLoop(node)) {
				//add start & end activities
				result.addStartActivities(childrenDfgs[0]);
				result.addEndActivities(childrenDfgs[0]);

				//body -> redo
				for (XEventClass ai : childrenDfgs[0].getEndActivities()) {
					for (XEventClass aj : childrenDfgs[1].getStartActivities()) {
						result.addDirectlyFollowsEdge(ai, aj, 1);
					}
				}

				//redo -> body
				for (XEventClass ai : childrenDfgs[1].getEndActivities()) {
					for (XEventClass aj : childrenDfgs[0].getStartActivities()) {
						result.addDirectlyFollowsEdge(ai, aj, 1);
					}
				}
				return result;
			} else if (tree.isInterleaved(node)) {
				for (int i = 0; i < numberOfChildren; i++) {
					for (int j = i + 1; j < numberOfChildren; j++) {
						for (XEventClass ai : childrenDfgs[i].getEndActivities()) {
							for (XEventClass aj : childrenDfgs[j].getStartActivities()) {
								result.addDirectlyFollowsEdge(ai, aj, 1);
							}
						}
						for (XEventClass aj : childrenDfgs[j].getEndActivities()) {
							for (XEventClass ai : childrenDfgs[i].getStartActivities()) {
								result.addDirectlyFollowsEdge(aj, ai, 1);
							}
						}
					}
				}
				return result;
			}
		}
		return null;
	}

	public static void put(Dfg dfg, Dfg into) {
		//add activities
		for (XEventClass activity : dfg.getActivities()) {
			into.addActivity(activity);
		}

		//copy edges
		for (long edgeIndex : dfg.getDirectlyFollowsGraph().getEdges()) {
			XEventClass source = dfg.getDirectlyFollowsGraph().getEdgeSource(edgeIndex);
			XEventClass target = dfg.getDirectlyFollowsGraph().getEdgeTarget(edgeIndex);
			long cardinality = dfg.getDirectlyFollowsGraph().getEdgeWeight(edgeIndex);
			into.addDirectlyFollowsEdge(source, target, cardinality);
		}

		//copy concurrent edges
		for (long edgeIndex : dfg.getConcurrencyGraph().getEdges()) {
			XEventClass source = dfg.getConcurrencyGraph().getEdgeSource(edgeIndex);
			XEventClass target = dfg.getConcurrencyGraph().getEdgeTarget(edgeIndex);
			long cardinality = dfg.getConcurrencyGraph().getEdgeWeight(edgeIndex);
			into.addParallelEdge(source, target, cardinality);
		}
	}
}
