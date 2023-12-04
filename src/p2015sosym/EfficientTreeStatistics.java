package p2015sosym;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeMetrics;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.InvalidProcessTreeException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.NotYetImplementedException;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import thesis.helperClasses.Simplicity;

public class EfficientTreeStatistics {
	@Plugin(name = "Get efficient tree statistics", returnLabels = { "efficient tree statistics" }, returnTypes = {
			HTMLToString.class }, parameterLabels = { "Efficient Tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Compare process trees by hashes, default", requiredParameterLabels = { 0 })
	public HTMLToString getStatistics(PluginContext context, final EfficientTree tree)
			throws UnknownTreeNodeException, NotYetImplementedException, InvalidProcessTreeException {
		final TIntSet startActivities = getStartActivities(tree, 0);
		final TIntSet endActivities = getEndActivities(tree, 0);
		final int operatorsWithoutChildren = getOperatorsWithoutChildren(tree);
		final int simplicity = Simplicity.measure(tree);

		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return "<html><table><tr><td>start activities</td><td>" + startActivities.size()
						+ "</td></tr><tr><td>end activities</td><td>" + endActivities.size()
						+ "</td></tr><tr><td>operators without children</td><td>" + operatorsWithoutChildren
						+ "</td></tr><tr><td>simplicity</td><td> " + simplicity + "</td></tr></table></html>";
			}
		};
	}

	public static int getOperatorsWithoutChildren(EfficientTree tree) {
		int result = 0;
		for (int node = 0; node < tree.getMaxNumberOfNodes(); node++) {
			if (tree.isOperator(node) && tree.getNumberOfChildren(node) < 1) {
				result++;
			}
		}
		return result;
	}

	public static TIntSet getStartActivities(EfficientTree tree, int node) throws UnknownTreeNodeException {
		if (tree.isTau(node)) {
			return new TIntHashSet();
		} else if (tree.isActivity(node)) {
			TIntSet result = new TIntHashSet();
			result.add(tree.getActivity(node));
			return result;
		} else if (tree.isXor(node) || tree.isConcurrent(node) || tree.isInterleaved(node)) {
			TIntSet result = new TIntHashSet();
			for (int child : tree.getChildren(node)) {
				result.addAll(getStartActivities(tree, child));
			}
			return result;
		} else if (tree.isSequence(node)) {
			TIntSet result = new TIntHashSet();
			for (int child : tree.getChildren(node)) {
				result.addAll(getStartActivities(tree, child));
				if (!EfficientTreeMetrics.canProduceTau(tree, child)) {
					return result;
				}
			}
			return result;
		} else if (tree.isLoop(node)) {
			int body = tree.getChild(node, 0);
			TIntSet result = getStartActivities(tree, body);

			if (EfficientTreeMetrics.canProduceTau(tree, body)) {
				int redo = tree.getChild(node, 1);
				result.addAll(getStartActivities(tree, redo));

				int exit = tree.getChild(node, 2);
				result.addAll(getStartActivities(tree, exit));
			}

			return result;
		}
		throw new UnknownTreeNodeException();
	}

	public static TIntSet getEndActivities(EfficientTree tree, int node) throws UnknownTreeNodeException {
		if (tree.isTau(node)) {
			return new TIntHashSet();
		} else if (tree.isActivity(node)) {
			TIntSet result = new TIntHashSet();
			result.add(tree.getActivity(node));
			return result;
		} else if (tree.isXor(node) || tree.isConcurrent(node) || tree.isInterleaved(node)) {
			TIntSet result = new TIntHashSet();
			for (int child : tree.getChildren(node)) {
				result.addAll(getEndActivities(tree, child));
			}
			return result;
		} else if (tree.isSequence(node)) {
			int child = tree.getNumberOfChildren(node) - 1;
			TIntSet result = getEndActivities(tree, tree.getChild(node, child));

			while (child >= 0 && EfficientTreeMetrics.canProduceTau(tree, tree.getChild(node, child))) {
				child--;
				result.addAll(getEndActivities(tree, tree.getChild(node, child)));
			}
			return result;
		} else if (tree.isLoop(node)) {
			int exit = tree.getChild(node, 2);
			TIntSet result = getEndActivities(tree, exit);

			if (EfficientTreeMetrics.canProduceTau(tree, exit)) {
				int body = tree.getChild(node, 0);
				result.addAll(getEndActivities(tree, body));

				if (EfficientTreeMetrics.canProduceTau(tree, body)) {
					int redo = tree.getChild(node, 1);
					result.addAll(getEndActivities(tree, redo));
				}
			}

			return result;
		}
		throw new UnknownTreeNodeException();
	}
}
