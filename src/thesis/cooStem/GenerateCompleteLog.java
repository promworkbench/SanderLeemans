package thesis.cooStem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

/**
 * Generates all occurrence-traces of an coo-tree.
 * 
 * @author sleemans
 *
 */
public class GenerateCompleteLog {
	public static List<int[]> generate(EfficientTree tree) {
		return generate(tree, tree.getRoot());
	}

	private static List<int[]> generate(EfficientTree tree, int node) {
		List<int[]> result = new ArrayList<>();
		if (tree.isTau(node)) {
			result.add(new int[0]);
		} else if (tree.isActivity(node)) {
			result.add(new int[] { tree.getActivity(node) });
		} else if (tree.isXor(node)) {
			for (int child : tree.getChildren(node)) {
				result.addAll(generate(tree, child));
			}
		} else if (tree.isConcurrent(node)) {
			result = generate(tree, tree.getChild(node, 0));
			for (int childNr = 1; childNr < tree.getNumberOfChildren(node); childNr++) {
				int child = tree.getChild(node, childNr);
				List<int[]> childTraces = generate(tree, child);
				result = cartesianProduct(result, childTraces);
			}
		} else if (tree.isOr(node)) {
			boolean aChildHasTheEmptyTrace = false;
			result = generate(tree, tree.getChild(node, 0));
			if (containsEmptyTrace(result)) {
				aChildHasTheEmptyTrace = true;
			} else {
				result.add(new int[0]);
			}

			for (int childNr = 1; childNr < tree.getNumberOfChildren(node); childNr++) {
				int child = tree.getChild(node, childNr);
				List<int[]> childTraces = generate(tree, child);

				if (containsEmptyTrace(childTraces)) {
					aChildHasTheEmptyTrace = true;
				} else {
					childTraces.add(new int[0]);
				}
				result = cartesianProduct(result, childTraces);
			}

			//if no child had the empty trace, then we should remove the empty traces from the result
			if (!aChildHasTheEmptyTrace) {
				Iterator<int[]> it = result.iterator();
				while (it.hasNext()) {
					if (it.next().length == 0) {
						it.remove();
					}
				}
			}
		} else {
			throw new UnknownTreeNodeException();
		}

		return result;
	}

	public static List<int[]> cartesianProduct(List<int[]> A, List<int[]> B) {
		List<int[]> result = new ArrayList<>(A.size() * B.size());
		for (int[] traceA : A) {
			for (int[] traceB : B) {
				result.add(ArrayUtils.addAll(traceA, traceB));
			}
		}
		return result;
	}

	public static boolean containsEmptyTrace(List<int[]> traces) {
		for (int[] trace : traces) {
			if (trace.length == 0) {
				return true;
			}
		}
		return false;
	}

}
