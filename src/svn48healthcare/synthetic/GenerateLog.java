package svn48healthcare.synthetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeHash;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import gnu.trove.map.TIntObjectMap;

public class GenerateLog {

	public static XLog generateLog(EfficientTree system, long numberOfTraces, long randomSeed, boolean lifeCycle,
			ProMCanceller canceller, TIntObjectMap<DistributionInstance> distributions) throws Exception {
		XAttributeMap logMap = new XAttributeMapImpl();
		putLiteral(logMap, "concept:name", "generated log from process tree");
		Random randomGenerator = new Random(randomSeed);
		XLog result = new XLogImpl(logMap);
		Random randomTimestampGenerator = new Random(randomSeed);

		// generate trace
		for (long t = 0; t < numberOfTraces; t++) {
			int[] trace = generateTraceFromSortedTree(system, 0, randomGenerator, lifeCycle);

			if (canceller.isCancelled()) {
				return null;
			}

			XTrace trace3 = trace2xTrace(system, trace, randomTimestampGenerator, distributions);
			result.add(trace3);
		}

		return result;
	}

	public static int[] generateTrace(EfficientTree system, int node, Random randomGenerator, boolean lifeCycle)
			throws Exception {
		EfficientTree tree = sortChildren(system);
		return generateTraceFromSortedTree(tree, node, randomGenerator, lifeCycle);
	}

	private static int[] generateTraceFromSortedTree(EfficientTree system, int node, Random randomGenerator,
			boolean lifeCycle) throws Exception {
		if (system.isTau(node)) {
			return generateTraceTau(system, node);
		} else if (system.isActivity(node)) {
			return generateTraceActivity(system, node, lifeCycle);
		} else if (system.isXor(node)) {
			return generateTraceXor(system, node, randomGenerator, lifeCycle);
		} else if (system.isSequence(node)) {
			return generateTraceSeq(system, node, randomGenerator, lifeCycle);
		} else if (system.isLoop(node)) {
			return generateTraceXorLoop(system, node, randomGenerator, lifeCycle);
		} else if (system.isConcurrent(node)) {
			return generateTraceAnd(system, node, randomGenerator, lifeCycle);
		} else if (system.isInterleaved(node)) {
			return generateTraceInt(system, node, randomGenerator, lifeCycle);
		} else if (system.isOr(node)) {
			return generateTraceOr(system, node, randomGenerator, lifeCycle);
		}
		throw new UnknownTreeNodeException();
	}

	private static int[] generateTraceTau(EfficientTree system, int node) {
		return new int[0];
	}

	private static int[] generateTraceActivity(EfficientTree system, int node, boolean lifeCycle) {
		if (lifeCycle) {
			return new int[] { -(system.getActivity(node) + 1), system.getActivity(node) };
		} else {
			return new int[] { system.getActivity(node) };
		}
	}

	private static int[] generateTraceXor(EfficientTree system, int node, Random randomGenerator, boolean lifeCycle)
			throws Exception {
		int childNr = randomGenerator.nextInt(system.getNumberOfChildren(node));
		return generateTraceFromSortedTree(system, system.getChild(node, childNr), randomGenerator, lifeCycle);
	}

	private static int[] generateTraceSeq(EfficientTree system, int node, Random randomGenerator, boolean lifeCycle)
			throws Exception {
		int[] result = new int[0];
		for (int child : system.getChildren(node)) {
			result = merge(result, generateTraceFromSortedTree(system, child, randomGenerator, lifeCycle));
		}
		return result;
	}

	private static int[] generateTraceXorLoop(EfficientTree system, int node, Random randomGenerator, boolean lifeCycle)
			throws Exception {
		int leftChild = system.getChild(node, 0);
		int middleChild = system.getChild(node, 1);
		int rightChild = system.getChild(node, 2);

		int[] result = generateTraceFromSortedTree(system, leftChild, randomGenerator, lifeCycle);
		while (randomGenerator.nextInt(10) > 4) {
			result = merge(result, generateTraceFromSortedTree(system, middleChild, randomGenerator, lifeCycle));
			result = merge(result, generateTraceFromSortedTree(system, leftChild, randomGenerator, lifeCycle));
		}
		result = merge(result, generateTraceFromSortedTree(system, rightChild, randomGenerator, lifeCycle));
		return result;
	}

	private static int[] generateTraceOr(EfficientTree system, int node, Random randomGenerator, boolean lifeCycle)
			throws Exception {
		int countChildren = system.getNumberOfChildren(node);

		// pick a child that is always in the result
		int guaranteedChild = system.getChild(node, randomGenerator.nextInt(countChildren));

		// walk over the children and randomly pick children that will generate
		// a trace
		List<Integer> childrenInResult = new ArrayList<>();
		for (int child : system.getChildren(node)) {
			if (child == guaranteedChild || randomGenerator.nextBoolean()) {
				childrenInResult.add(child);
			}
		}

		return generateTraceConcurrent(system, childrenInResult, childrenInResult.size(), randomGenerator, lifeCycle);
	}

	private static int[] generateTraceAnd(EfficientTree system, int node, Random randomGenerator, boolean lifeCycle)
			throws Exception {
		return generateTraceConcurrent(system, system.getChildren(node), system.getNumberOfChildren(node),
				randomGenerator, lifeCycle);
	}

	private static int[] generateTraceConcurrent(EfficientTree system, Iterable<Integer> children, int countChildren,
			Random randomGenerator, boolean lifeCycle) throws Exception {
		// put all events in a single trace
		int[] result = new int[0];
		int[][] childrenTraces = new int[countChildren][];
		int[] iterators = new int[countChildren];
		{
			int branch = 0;
			for (int child : children) {
				childrenTraces[branch] = generateTraceFromSortedTree(system, child, randomGenerator, lifeCycle);
				int childTraceLength = childrenTraces[branch].length;

				// put the branch in the result
				int[] branchTrace = new int[childTraceLength];
				for (int i = 0; i < childTraceLength; i++) {
					branchTrace[i] = branch;
				}

				iterators[branch] = 0;
				result = merge(result, branchTrace);
				branch++;
			}
		}

		// result up till now in result: [000001111122233333333]

		// shuffle the result
		shuffle(result, randomGenerator);

		// replace each branch number by its event
		for (int e = 0; e < result.length; e++) {
			int branch = result[e];
			int pos = iterators[branch];
			iterators[branch]++;
			result[e] = childrenTraces[branch][pos];
		}

		return result;
	}

	private static int[] generateTraceInt(EfficientTree system, int node, Random randomGenerator, boolean lifeCycle)
			throws Exception {
		// sort the children
		List<Integer> list = Lists.newArrayList(system.getChildren(node));

		// make a random permutation of children
		Collections.shuffle(list, randomGenerator);

		int[] result = new int[0];
		for (int child : list) {
			result = merge(result, generateTraceFromSortedTree(system, child, randomGenerator, lifeCycle));
		}
		return result;
	}

	private final static class CustomComparator implements Comparator<Integer> {
		private final EfficientTree tree;

		public CustomComparator(EfficientTree tree) {
			this.tree = tree;
		}

		public int compare(Integer o1, Integer o2) {
			return EfficientTreeHash.hash(tree, o1).compareTo(EfficientTreeHash.hash(tree, o2));
		}
	}

	/**
	 * 
	 * @param oldTree
	 * @return a tree in which all children have been sorted
	 */
	public static EfficientTree sortChildren(EfficientTree oldTree) {
		// make a copy of the tree
		EfficientTree tree = oldTree.clone();

		for (int node = tree.getMaxNumberOfNodes() - 1; node >= 0; node--) {
			sortChildren(tree, node);
		}

		return tree;
	}

	private static void sortChildren(EfficientTree tree, int node) {
		if (tree.isInterleaved(node) || tree.isConcurrent(node) || tree.isXor(node)) {
			// make a sorted list of children
			Integer[] sortedChildren = Iterables.toArray(tree.getChildren(node), Integer.class);
			Arrays.sort(sortedChildren, new CustomComparator(tree));

			tree.reorderNodes(sortedChildren, tree.traverse(node));
			// //copy the children
			// int[] childrenTree = new int[0];
			// for (int child : sortedChildren) {
			// childrenTree = merge(childrenTree, tree.getChildTree(child));
			// }
			//
			// System.arraycopy(childrenTree, 0, tree.getTree(), node + 1,
			// childrenTree.length);
		}
	}

	public static void putLiteral(XAttributeMap attMap, String key, String value) {
		attMap.put(key, new XAttributeLiteralImpl(key, value));
	}

	public static class Event extends XEventImpl {
		public int branch = 0;

		public Event(XAttributeMap attMap) {
			super(attMap);
		}
	}

	/**
	 * Get a list of all manual leaves of a tree.
	 * 
	 * @param node
	 * @return
	 */
	public static List<String> getActivities(EfficientTree tree, int node) {

		List<String> result = new ArrayList<>();
		if (tree.isActivity(node)) {
			result.add(tree.getActivityName(node));
		} else if (tree.isOperator(node)) {
			for (int child : tree.getChildren(node)) {
				result.addAll(getActivities(tree, child));
			}
		}
		return result;
	}

	public static int[] merge(int[] a, int[] b) {
		int[] result = new int[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	/**
	 * code taken from Collections
	 * 
	 * @param array
	 * @param random
	 */
	public static void shuffle(int[] array, Random random) {
		int count = array.length;
		for (int i = count; i > 1; i--) {
			swap(array, i - 1, random.nextInt(i));
		}
	}

	/**
	 * code taken from Collections
	 * 
	 * @param array
	 * @param random
	 */
	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	private static String getActivity(EfficientTree system, int e) {
		if (e < 0) {
			return system.getInt2activity()[(-e) - 1];
		} else {
			return system.getInt2activity()[e];
		}
	}

	private static boolean isStart(int e) {
		return e < 0;
	}

	/**
	 * transform a int[] trace into an XTrace
	 * 
	 * @param system
	 * @param factory
	 * @param trace
	 * @return
	 */
	public static XTrace trace2xTrace(EfficientTree system, int[] trace, Random random,
			TIntObjectMap<DistributionInstance> distributions) {
		// transform to xevents
		List<XEvent> trace2 = new ArrayList<>();
		if (trace.length == 0) {
			System.out.println("empty trace generated");
		}
		long sum = 0;
		long timestamp = random.nextInt(50000000);
		for (int e : trace) {
			XAttributeMap attMap = new XAttributeMapImpl();

			putLiteral(attMap, "concept:name", getActivity(system, e));
			if (isStart(e)) {
				putLiteral(attMap, "lifecycle:transition", "start");
			} else {
				putLiteral(attMap, "lifecycle:transition", "complete");
			}
			putLiteral(attMap, "org:resource", "artificial");

			long time = Math.round(distributions.get(e).sample());
			sum += time;

			// make up a timestamp
			timestamp += time;
			attMap.put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", timestamp));

			trace2.add(new Event(attMap));
		}

		XTrace trace3 = new XTraceImpl(new XAttributeMapImpl());

		trace3.getAttributes().put("cost:total", new XAttributeDiscreteImpl("cost:total", sum));

		trace3.addAll(trace2);
		return trace3;
	}
}