package generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.conversion.ReduceTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.processtree.Block;
import org.processmining.processtree.Block.And;
import org.processmining.processtree.Block.Def;
import org.processmining.processtree.Block.DefLoop;
import org.processmining.processtree.Block.Seq;
import org.processmining.processtree.Block.Xor;
import org.processmining.processtree.Block.XorLoop;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.Task;

public class CompareTrees {

	@Plugin(name = "Compare process trees by hashes", returnLabels = { "Comparison result" }, returnTypes = { HTMLToString.class }, parameterLabels = {
			"Process Tree #1", "Process tree #2" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Compare process trees by hashes, default", requiredParameterLabels = { 0, 1 })
	public HTMLToString compareTrees(PluginContext context, final ProcessTree a, final ProcessTree b)
			throws UnknownTreeNodeException, ReductionFailedException {
		ReduceTree.reduceTree(a, new EfficientTreeReduceParameters(false));
		ReduceTree.reduceTree(b, new EfficientTreeReduceParameters(false));
		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				StringBuilder result = new StringBuilder();
				result.append(hash(a.getRoot()));
				result.append("<br>");
				result.append(hash(b.getRoot()));
				result.append("<br>equal: ");
				result.append(isLanguageEqual(a, b));
				return result.toString();
			}
		};
	}

	/**
	 * Returns whether two trees are language equivalent. Note that both trees
	 * must have been reduced before.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isLanguageEqual(ProcessTree a, ProcessTree b) {
		return hash(a.getRoot()).equals(hash(b.getRoot()));
	}

	/**
	 * Returns the hash of a tree. Guaranteed to be equivalent even under
	 * language-equivalent reordering of children.
	 * 
	 * @param node
	 * @return
	 */
	public static String hash(Node node) {
		if (node instanceof Task.Manual) {
			return ((Task) node).getName();
		} else if (node instanceof Task.Automatic) {
			return "tau";
		} else if (node instanceof Xor || node instanceof Def) {
			return "xor(" + childHashes((Block) node, true) + ")";
		} else if (node instanceof Seq) {
			return "seq(" + childHashes((Block) node, false) + ")";
		} else if (node instanceof And) {
			return "and(" + childHashes((Block) node, true) + ")";
		} else if (node instanceof XorLoop || node instanceof DefLoop) {
			return "loop(" + childHashes((Block) node, false) + ")";
		}
		return null;
	}

	private static String childHashes(Block node, boolean sort) {
		List<String> childHashes = new ArrayList<String>();
		for (Node child : node.getChildren()) {
			childHashes.add(hash(child));
		}
		if (sort) {
			Collections.sort(childHashes);
		}
		// Format the ArrayList as a string, similar to implode
		StringBuilder builder = new StringBuilder();
		builder.append(childHashes.remove(0));
		for (String s : childHashes) {
			builder.append(",");
			builder.append(s);
		}
		return builder.toString();
	}
}
