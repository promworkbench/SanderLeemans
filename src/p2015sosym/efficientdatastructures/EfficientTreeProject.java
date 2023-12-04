package p2015sosym.efficientdatastructures;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeFactory;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeImpl;

import gnu.trove.map.TObjectIntMap;

public class EfficientTreeProject {

	/**
	 * Project a tree onto activities.
	 * 
	 * @param efficientTree
	 * @param keepActivities
	 * @return
	 */
	public static EfficientTree project(EfficientTreeImpl efficientTree, String... keepActivities) {
		//map: [newIndex] = oldindex
		int[] keepActivitiesInt = new int[keepActivities.length];

		TObjectIntMap<String> newActivity2int = EfficientTreeImpl.getEmptyActivity2int();
		String[] newInt2activity = new String[keepActivities.length];

		for (int newIndex = 0; newIndex < keepActivities.length; newIndex++) {
			String activity = keepActivities[newIndex];
			int oldIndex = efficientTree.getActivity2int().get(activity);

			//if the activity does not appear in the tree, oldIndex will be < 0.
			keepActivitiesInt[newIndex] = oldIndex;

			newActivity2int.put(activity, newIndex);
			newInt2activity[newIndex] = activity;
		}

		int[] newTree = project(efficientTree.getTree(), keepActivitiesInt);
		return EfficientTreeFactory.create(newTree, newActivity2int, newInt2activity);
	}

	/**
	 * 
	 * @param efficientTree
	 * @param keepActivities
	 * @return a copy of the tree in which every activity not
	 *         from @keepActivities is replaced with tau, and every occurrence
	 *         is replaced by the number of the activity as given in
	 *         keepActivities.
	 */
	public static int[] project(int[] efficientTree, int... keepActivities) {
		int[] result = efficientTree.clone();

		//walk through the tree and replace things
		for (int i = 0; i < result.length; i++) {
			if (result[i] >= 0) {
				boolean found = false;
				for (int j = 0; j < keepActivities.length; j++) {
					if (result[i] == keepActivities[j]) {
						result[i] = j;
						found = true;
						break;
					}
				}
				if (!found) {
					result[i] = EfficientTree.NodeType.tau.code;
				}
			}
		}

		return result;
	}

}
