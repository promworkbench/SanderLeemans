package thesis.helperClasses;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

public class EfficientTree2Latex {
	public static String latex(EfficientTree tree, boolean leavesInMathMode) {
		StringBuilder result = new StringBuilder();
		latex(tree, tree.getRoot(), leavesInMathMode, result);
		return result.toString();
	}
	
	private static void latex(EfficientTree tree, int node, boolean leavesInMathMode, StringBuilder result) {
		if (tree.isActivity(node)) {
			if (leavesInMathMode) {
				result.append("$");
			}
			result.append(tree.getActivityName(node));
			if (leavesInMathMode) {
				result.append("$");
			}
		} else if (tree.isTau(node)) {
			result.append("$\\tau$");
		} else if (tree.isOperator(node)) {
			result.append("[.");
			if (tree.isXor(node)) {
				result.append("$\\xorOp$ ");
			} else if (tree.isSequence(node)) {
				result.append("$\\sequenceOp$ ");
			} else if (tree.isConcurrent(node)) {
				result.append("$\\concurrentOp$ ");
			} else if (tree.isInterleaved(node)) {
				result.append("$\\interleavedOp$ ");
			} else if (tree.isLoop(node)) {
				//sorry people, here we use the n-ary loop
				result.append("$\\loopOp$ ");
				
				//body
				latex(tree, tree.getChild(node, 0), leavesInMathMode, result);
				result.append(" ");
				
				//redo
				int redo = tree.getChild(node, 1);
				if (tree.isXor(redo)) {
					for (int i = 0; i < tree.getNumberOfChildren(redo); i++) {
						int child = tree.getChild(redo, i);
						latex(tree, child, leavesInMathMode, result);
						result.append(" ");
					}
				} else {
					latex(tree, redo, leavesInMathMode, result);
					result.append(" ");
				}
				result.append("]");
				return;
			} else if (tree.isOr(node)) {
				result.append("$\\orOp$ ");
			} else {
				throw new UnknownTreeNodeException();
			}
			for (int i = 0; i < tree.getNumberOfChildren(node); i++) {
				int child = tree.getChild(node, i);
				latex(tree, child, leavesInMathMode, result);
				result.append(" ");
			}
			result.append("]");
		} else {
			throw new UnknownTreeNodeException();
		}

	}
}
