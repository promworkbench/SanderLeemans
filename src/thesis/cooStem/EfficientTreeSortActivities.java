package thesis.cooStem;

import java.util.Arrays;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeImpl;

import gnu.trove.map.TObjectIntMap;

public class EfficientTreeSortActivities {

	/**
	 * 
	 * @param tree
	 * @return A new tree, in which the activities are sorted by their label.
	 */
	public static EfficientTree sort(EfficientTree tree) {

		String[] int2activity = new String[tree.getInt2activity().length];
		System.arraycopy(tree.getInt2activity(), 0, int2activity, 0, int2activity.length);
		Arrays.sort(int2activity);

		TObjectIntMap<String> activity2int = EfficientTreeImpl.getEmptyActivity2int();
		for (int i = 0; i < int2activity.length; i++) {
			activity2int.put(int2activity[i], i);
		}
		
		int[] newTree = new int[tree.getTree().length];
		System.arraycopy(tree.getTree(), 0, newTree, 0, newTree.length);
		for (int node = 0; node < newTree.length ; node++) {
			if (tree.isActivity(node)) {
				newTree[node] = activity2int.get(tree.getActivityName(node));
			}
		}

		return new EfficientTreeImpl(newTree, activity2int, int2activity);
	}
}
