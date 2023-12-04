package thesis.cooStem;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeFactory;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeImpl;

import gnu.trove.map.TObjectIntMap;
import thesis.testMiners.InlineTree2;

public class GenerateCooTrees {

	public static List<EfficientTree> generate(List<String> activities) {

		String[] int2activity = new String[activities.size()];
		TObjectIntMap<String> activity2int = EfficientTreeImpl.getEmptyActivity2int();
		int[] activityIndexList = new int[activities.size()];
		int i = 0;
		for (String activity : activities) {
			int2activity[i] = activity;
			activity2int.put(activity, i);
			activityIndexList[i] = i;
			i++;
		}

		//generate the trees as int-arrays
		List<int[]> trees = generate(false, activityIndexList);

		//make efficientTree-objects
		List<EfficientTree> result = new ArrayList<>(trees.size());
		for (int[] tree : trees) {
			result.add(EfficientTreeFactory.create(tree, activity2int, int2activity));
		}

		return result;
	}

	private static List<int[]> generate(boolean allowSkip, int[] activities) {
		assert (activities.length >= 1);

		if (allowSkip) {
			List<int[]> recursiveResult = generate(false, activities);
			List<int[]> result = new ArrayList<>();
			result.addAll(recursiveResult);
			for (int[] tree : recursiveResult) {
				result.add(InlineTree2.xor(InlineTree2.tau(), tree));
			}
			return result;
		}

		if (activities.length == 1) {
			List<int[]> result = new ArrayList<>();
			result.add(InlineTree2.leaf(activities[0]));
			return result;
		}

		List<int[]> result = new ArrayList<>();

		//walk through all possible combinations of binary activity partitions
		BinaryUnorderedPartitionsIterator it = new BinaryUnorderedPartitionsIterator(activities);
		while (it.hasNext()) {
			it.next();

			//recurse
			List<int[]> recursiveResultLeft = generate(true, it.getLeft());
			List<int[]> recursiveResultRight = generate(true, it.getRight());

			//combine the trees
			for (int[] leftTree : recursiveResultLeft) {
				for (int[] rightTree : recursiveResultRight) {
					result.add(InlineTree2.and(leftTree, rightTree));
					result.add(InlineTree2.or(leftTree, rightTree));
				}
			}
		}

		return result;
	}

	public static class BinaryUnorderedPartitionsIterator {

		private final int[] activities;
		private long now;
		private final long max;

		public BinaryUnorderedPartitionsIterator(int[] activities) {
			this.activities = activities;
			max = (long) Math.pow(2, activities.length - 1) - 1;
			now = 0;
		}

		public boolean hasNext() {
			return now < max;
		}

		public void next() {
			now = now + 1;
		}

		public int[] getLeft() {
			long p = now;

			int count = 0;
			for (int i = 0; i < activities.length; i++) {
				if (contains(now, i)) {
					count++;
				}
			}
			int[] result = new int[count];
			int j = 0;
			for (int i = 0; i < activities.length; i++) {
				if (contains(now, i)) {
					result[j] = activities[i];
					j++;
				}
			}
			return result;
		}

		public int[] getRight() {
			long p = now;

			int count = 0;
			for (int i = 0; i < activities.length; i++) {
				if (!contains(now, i)) {
					count++;
				}
			}
			int[] result = new int[count];
			int j = 0;
			for (int i = 0; i < activities.length; i++) {
				if (!contains(now, i)) {
					result[j] = activities[i];
					j++;
				}
			}
			return result;
		}

		public static boolean contains(long value, int bitNumber) {
			return ((value & (1L << bitNumber)) != 0);
		}
	}
}
