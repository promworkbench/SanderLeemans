package thesis.testMiners;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree.NodeType;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeImpl;

/**
 * Construct a tree if the activities are known beforehand.
 * 
 * @author sleemans
 *
 */
public class InlineTree2 {

	public static int[] leaf(int activity) {
		//construct the tree
		int[] tree = new int[1];
		tree[0] = activity;
		return tree;
	}

	public static int[] tau() {
		//construct the tree
		int[] tree = new int[1];
		tree[0] = NodeType.tau.code;
		return tree;
	}

	public static int[] xor(int[] child1, int[]... children) {
		return combineTrees(NodeType.xor, child1, children);
	}

	public static int[] seq(int[] child1, int[]... children) {
		return combineTrees(NodeType.sequence, child1, children);
	}

	public static int[] and(int[] child1, int[]... children) {
		return combineTrees(NodeType.concurrent, child1, children);
	}

	public static int[] loop(int[] body, int[] redo, int[] exit) {
		int[][] children = new int[2][];
		children[0] = redo;
		children[1] = exit;
		return combineTrees(NodeType.loop, body, children);
	}

	public static int[] inte(int[] child1, int[]... children) {
		return combineTrees(NodeType.interleaved, child1, children);
	}

	public static int[] or(int[] child1, int[]... children) {
		return combineTrees(NodeType.or, child1, children);
	}

	private static int[] combineTrees(NodeType operator, int[] child1, int[]... children) {
		int length = child1.length;
		for (int[] child : children) {
			length += child.length;
		}

		int[] result = new int[length + 1];
		result[0] = operator.code - EfficientTreeImpl.childrenFactor * (children.length + 1);
		System.arraycopy(child1, 0, result, 1, child1.length);
		int pos = child1.length + 1;
		for (int[] child : children) {
			System.arraycopy(child, 0, result, pos, child.length);
			pos += child.length;
		}
		return result;
	}
}
