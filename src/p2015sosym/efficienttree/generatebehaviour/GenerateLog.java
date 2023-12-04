package p2015sosym.efficienttree.generatebehaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgImpl;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeHash;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeUtils;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GenerateLog {

	@Plugin(name = "Generate log from efficient tree - 1 trace", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog0(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 1, 0l, false);
	}

	@Plugin(name = "Generate log from efficient tree - 10 traces", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog1(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 10, 0l, false);
	}

	@Plugin(name = "Generate log from efficient tree - 100 traces", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog2(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 100, 0l, false);
	}

	@Plugin(name = "Generate log from efficient tree - 1,000 traces", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog3(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 1000, 0l, false);
	}

	@Plugin(name = "Generate log from efficient tree - 10,000 traces", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog4(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 10000, 0l, false);
	}

	@Plugin(name = "Generate log from efficient tree - 100,000 traces", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog5(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 100000, 0l, false);
	}

	@Plugin(name = "Generate log from efficient tree - 1,000,000 traces", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog6(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 1000000, 0l, false);
	}

	@Plugin(name = "Generate log from efficient tree - 10,000,000 traces", returnLabels = { "Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public XLog generateLog7(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateLog(tree, 10000000, 0l, false);
	}

	@Plugin(name = "Generate dfg from efficient tree - 1000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateDfg3(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 1000, 0l);
	}

	@Plugin(name = "Generate dfg from efficient tree - 10,000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateDfg4(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 10000, 0l);
	}

	@Plugin(name = "Generate dfg from efficient tree - 100,000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateDfg5(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 100000, 0l);
	}

	@Plugin(name = "Generate dfg from efficient tree - 1,000,000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateDfg6(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 1000000, 0l);
	}

	@Plugin(name = "Generate dfg from efficient tree - 10,000,000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateLog8(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 10000000, 0l);
	}

	@Plugin(name = "Generate dfg from efficient tree - 100,000,000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateLog9(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 100000000, 0l);
	}

	@Plugin(name = "Generate dfg from efficient tree - 1,000,000,000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateLog10(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 1000000000, 0l);
	}

	@Plugin(name = "Generate dfg from efficient tree - 10,000,000,000 traces", returnLabels = { "Dfg" }, returnTypes = {
			Dfg.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Dfg generateLog11(UIPluginContext context, EfficientTree tree) throws Exception {
		return generateDfg(tree, 10000000000l, 0l);
	}

	public static XLog generateLog(EfficientTree system, long numberOfTraces, long randomSeed, boolean lifeCycles)
			throws Exception {
		EfficientTree system2 = sortChildren(system);

		XAttributeMap logMap = new XAttributeMapImpl();
		putLiteral(logMap, "concept:name", "generated log from process tree");
		Random randomGenerator = new Random(randomSeed);

		XLog result = new XLogImpl(logMap);

		//generate trace
		for (long t = 0; t < numberOfTraces; t++) {
			int[] trace = generateTraceFromSortedTree(system2, 0, randomGenerator, lifeCycles);

			XTrace trace3 = trace2xTrace(system2.getInt2activity(), trace);
			result.add(trace3);
		}

		return result;
	}

	public static XLog generateIMLog(EfficientTree system, int numberOfTraces, long randomSeed, boolean lifeCycles)
			throws Exception {
		EfficientTree system2 = sortChildren(system);

		Random randomGenerator = new Random(randomSeed);

		XAttributeMap logMap = new XAttributeMapImpl();
		putLiteral(logMap, "concept:name", "generated log from process tree");
		XLog result = new XLogImpl(logMap);

		//IMLogImpl result = new IMLogImpl(numberOfTraces, system.getActivity2int(), system.getInt2activity());

		//generate trace
		for (int t = 0; t < numberOfTraces; t++) {
			int[] trace = generateTraceFromSortedTree(system2, 0, randomGenerator, lifeCycles);

			//			long[] trace2 = new long[trace.length];
			//			for (int i = 0; i < trace.length; i++) {
			//				trace2[i] = IMLogImpl.getEvent(trace[i], 0);
			//			}
			//
			//			result.setTrace(trace2, t);

			XTrace trace3 = trace2xTrace(system2.getInt2activity(), trace);
			result.add(trace3);
		}

		return result;
	}

	/**
	 * transform a int[] trace into an XTrace
	 * 
	 * @param data.system
	 * @param trace
	 * @return
	 */
	public static XTrace trace2xTrace(String[] int2activity, int[] trace) {
		//transform to xevents
		List<XEvent> trace2 = new ArrayList<>();
		if (trace.length == 0) {
			//System.out.println("empty trace generated");
		}
		for (int e : trace) {
			XAttributeMap attMap = new XAttributeMapImpl();
			putLiteral(attMap, "concept:name", int2activity[e < 0 ? ~e : e]);
			putLiteral(attMap, "lifecycle:transition", e < 0 ? "start" : "complete");
			putLiteral(attMap, "org:resource", "artificial");
			trace2.add(new Event(attMap));
		}

		XTrace trace3 = new XTraceImpl(new XAttributeMapImpl());
		trace3.addAll(trace2);
		return trace3;
	}

	public static Dfg generateDfg(EfficientTree system, long numberOfTraces, long randomSeed) throws Exception {
		EfficientTree system2 = sortChildren(system);
		//Dfg dfg = DfgImpl.createTimeOptimised(system.getInt2activity().length);
		Dfg dfg = new DfgImpl();

		assert (EfficientTreeUtils.isConsistent(system));
		assert (EfficientTreeUtils.isConsistent(system2));

		//make event classes
		XEventClass[] eventClasses = new XEventClass[system2.getInt2activity().length];
		for (int i = 0; i < system2.getInt2activity().length; i++) {
			eventClasses[i] = new XEventClass(system2.getInt2activity()[i], i);
			dfg.addActivity(eventClasses[i]);
		}

		Random randomGenerator = new Random(randomSeed);

		//generate traces
		for (long t = 0; t < numberOfTraces; t++) {
			int[] trace = generateTraceFromSortedTree(system, 0, randomGenerator, false);

			if (trace.length == 0) {
				System.out.println("empty trace generated");
			}

			//add to dfg
			XEventClass last = null;
			for (int e : trace) {
				if (e < 0) {
					e = ~e;
				}
				if (last == null) {
					//add start activity
					dfg.addStartActivity(eventClasses[e], 1);
				} else {
					//add dfg-edge
					dfg.addDirectlyFollowsEdge(last, eventClasses[e], 1);
				}
				last = eventClasses[e];
			}
			//add end activity
			if (last != null) {
				dfg.addEndActivity(last, 1);
			}
		}

		return dfg;
	}

	public static int[] generateTrace(EfficientTree system, int node, Random randomGenerator, boolean lifeCycles)
			throws Exception {
		EfficientTree tree = sortChildren(system);
		return generateTraceFromSortedTree(tree, node, randomGenerator, lifeCycles);
	}

	public static Iterable<int[]> generateTraces(EfficientTree system, final long numberOfTraces,
			final Random randomGenerator, final boolean lifeCycles) {
		final EfficientTree tree = sortChildren(system);
		return new Iterable<int[]>() {
			public Iterator<int[]> iterator() {
				return new Iterator<int[]>() {
					private long i = numberOfTraces;

					public boolean hasNext() {
						return i > 0;
					}

					public int[] next() {
						i--;
						try {
							return generateTraceFromSortedTree(tree, 0, randomGenerator, lifeCycles);
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}

					public void remove() {
						//not implemented
						throw new RuntimeException("not implemented");
					}
				};

			}
		};
	}

	private static int[] generateTraceFromSortedTree(EfficientTree system, int node, Random randomGenerator,
			boolean lifeCycles) throws Exception {
		if (system.isTau(node)) {
			return generateTraceTau(system, node, lifeCycles);
		} else if (system.isActivity(node)) {
			return generateTraceActivity(system, node, lifeCycles);
		} else if (system.isXor(node)) {
			return generateTraceXor(system, node, randomGenerator, lifeCycles);
		} else if (system.isSequence(node)) {
			return generateTraceSeq(system, node, randomGenerator, lifeCycles);
		} else if (system.isLoop(node)) {
			return generateTraceXorLoop(system, node, randomGenerator, lifeCycles);
		} else if (system.isConcurrent(node)) {
			return generateTraceAnd(system, node, randomGenerator, lifeCycles);
		} else if (system.isInterleaved(node)) {
			return generateTraceInt(system, node, randomGenerator, lifeCycles);
		} else if (system.isOr(node)) {
			return generateTraceOr(system, node, randomGenerator, lifeCycles);
		}
		throw new UnknownTreeNodeException();
	}

	private static int[] generateTraceTau(EfficientTree system, int node, boolean lifeCycles) {
		return new int[0];
	}

	private static int[] generateTraceActivity(EfficientTree system, int node, boolean lifeCycles) {
		if (lifeCycles) {
			return new int[] { ~system.getActivity(node), system.getActivity(node) };
		} else {
			return new int[] { system.getActivity(node) };
		}
	}

	private static int[] generateTraceXor(EfficientTree system, int node, Random randomGenerator, boolean lifeCycles)
			throws Exception {
		int childNr = randomGenerator.nextInt(system.getNumberOfChildren(node));
		return generateTraceFromSortedTree(system, system.getChild(node, childNr), randomGenerator, lifeCycles);
	}

	private static int[] generateTraceSeq(EfficientTree system, int node, Random randomGenerator, boolean lifeCycles)
			throws Exception {
		int[] result = new int[0];
		for (int child : system.getChildren(node)) {
			result = merge(result, generateTraceFromSortedTree(system, child, randomGenerator, lifeCycles));
		}
		return result;
	}

	private static int[] generateTraceXorLoop(EfficientTree system, int node, Random randomGenerator,
			boolean lifeCycles) throws Exception {
		int leftChild = system.getChild(node, 0);
		int middleChild = system.getChild(node, 1);
		int rightChild = system.getChild(node, 2);

		int[] result = generateTraceFromSortedTree(system, leftChild, randomGenerator, lifeCycles);
		while (randomGenerator.nextInt(10) > 4) {
			result = merge(result, generateTraceFromSortedTree(system, middleChild, randomGenerator, lifeCycles));
			result = merge(result, generateTraceFromSortedTree(system, leftChild, randomGenerator, lifeCycles));
		}
		result = merge(result, generateTraceFromSortedTree(system, rightChild, randomGenerator, lifeCycles));
		return result;
	}

	private static int[] generateTraceOr(EfficientTree system, int node, Random randomGenerator, boolean lifeCycles)
			throws Exception {
		int countChildren = system.getNumberOfChildren(node);

		//pick a child that is always in the result
		int guaranteedChild = system.getChild(node, randomGenerator.nextInt(countChildren));

		//walk over the children and randomly pick children that will generate a trace
		List<Integer> childrenInResult = new ArrayList<>();
		for (int child : system.getChildren(node)) {
			if (child == guaranteedChild || randomGenerator.nextBoolean()) {
				childrenInResult.add(child);
			}
		}

		return generateTraceConcurrent(system, childrenInResult, childrenInResult.size(), randomGenerator, lifeCycles);
	}

	private static int[] generateTraceAnd(EfficientTree system, int node, Random randomGenerator, boolean lifeCycles)
			throws Exception {
		return generateTraceConcurrent(system, system.getChildren(node), system.getNumberOfChildren(node),
				randomGenerator, lifeCycles);
	}

	private static int[] generateTraceConcurrent(EfficientTree system, Iterable<Integer> children, int countChildren,
			Random randomGenerator, boolean lifeCycles) throws Exception {
		//put all events in a single trace
		int[] result = new int[0];
		int[][] childrenTraces = new int[countChildren][];
		int[] iterators = new int[countChildren];
		{
			int branch = 0;
			for (int child : children) {
				childrenTraces[branch] = generateTraceFromSortedTree(system, child, randomGenerator, lifeCycles);
				int childTraceLength = childrenTraces[branch].length;

				//put the branch in the result
				int[] branchTrace = new int[childTraceLength];
				for (int i = 0; i < childTraceLength; i++) {
					branchTrace[i] = branch;
				}

				iterators[branch] = 0;
				result = merge(result, branchTrace);
				branch++;
			}
		}

		//result up till now in result: [000001111122233333333]

		//shuffle the result
		shuffle(result, randomGenerator);

		//replace each branch number by its event
		for (int e = 0; e < result.length; e++) {
			int branch = result[e];
			int pos = iterators[branch];
			iterators[branch]++;
			result[e] = childrenTraces[branch][pos];
		}

		return result;
	}

	private static int[] generateTraceInt(EfficientTree system, int node, Random randomGenerator, boolean lifeCycles)
			throws Exception {
		//sort the children
		List<Integer> list = Lists.newArrayList(system.getChildren(node));

		//make a random permutation of children
		Collections.shuffle(list, randomGenerator);

		int[] result = new int[0];
		for (int child : list) {
			result = merge(result, generateTraceFromSortedTree(system, child, randomGenerator, lifeCycles));
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
		//make a copy of the tree
		EfficientTree tree = oldTree.clone();

		for (int node = tree.getMaxNumberOfNodes() - 1; node >= 0; node--) {
			sortChildren(tree, node);
		}

		return tree;
	}

	private static void sortChildren(EfficientTree tree, int node) {
		if (tree.isInterleaved(node) || tree.isConcurrent(node) || tree.isXor(node)) {
			//make a sorted list of children
			Integer[] sortedChildren = Iterables.toArray(tree.getChildren(node), Integer.class);
			Arrays.sort(sortedChildren, new CustomComparator(tree));

			tree.reorderNodes(sortedChildren, tree.traverse(node));
//			//copy the children
//			int[] childrenTree = new int[0];
//			for (int child : sortedChildren) {
//				childrenTree = merge(childrenTree, tree.getChildTree(child));
//			}
//
//			System.arraycopy(childrenTree, 0, tree.getTree(), node + 1, childrenTree.length);
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
}