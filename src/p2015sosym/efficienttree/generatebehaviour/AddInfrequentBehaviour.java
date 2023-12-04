package p2015sosym.efficienttree.generatebehaviour;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeHash;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.processtree.ProcessTree;

import com.google.common.collect.Lists;

/**
 * idea: generate a trace but only count the number of nodes visited. Second,
 * generate the trace exactly again, but insert a deviation at a random point.
 */

public class AddInfrequentBehaviour {

	@Plugin(name = "Add infrequent behaviour to log - 1 trace", returnLabels = {
			"Log with added infrequent behaviour" }, returnTypes = {
					XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog0(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddInfrequentTraces(system, log, 1);
	}

	@Plugin(name = "Add infrequent behaviour to log - 10 traces", returnLabels = {
			"Log with added infrequent behaviour" }, returnTypes = {
					XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog1(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddInfrequentTraces(system, log, 10);
	}

	@Plugin(name = "Add infrequent behaviour to log - 100 traces", returnLabels = {
			"Log with added infrequent behaviour" }, returnTypes = {
					XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog2(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddInfrequentTraces(system, log, 100);
	}

	@Plugin(name = "Add infrequent behaviour to log - 1,000 traces", returnLabels = {
			"Log with added infrequent behaviour" }, returnTypes = {
					XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog3(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddInfrequentTraces(system, log, 1000);
	}

	@Plugin(name = "Add infrequent behaviour to log - 10,000 traces", returnLabels = {
			"Log with added infrequent behaviour" }, returnTypes = {
					XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog4(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddInfrequentTraces(system, log, 10000);
	}

	@Plugin(name = "Add infrequent behaviour to log - 100,000 traces", returnLabels = {
			"Log with added infrequent behaviour" }, returnTypes = {
					XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog5(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddInfrequentTraces(system, log, 100000);
	}

	public static XLog copyAndAddInfrequentTraces(ProcessTree system, XLog log, int nrOfDeviatingTraces)
			throws Exception {
		XLog log2 = (XLog) log.clone();
		addInfrequentTraces(ProcessTree2EfficientTree.convert(system), log2, nrOfDeviatingTraces);
		return log2;
	}

	public static void addInfrequentTraces(EfficientTree system, XLog log, int numberOfTraces) {
		EfficientTree tree = GenerateLog.sortChildren(system);

		for (int t = 0; t < numberOfTraces; t++) {
			//generate trace
			int[] trace = generateTraceFromSortedTree(tree, t);

			//add to log
			log.add(GenerateLog.trace2xTrace(tree.getInt2activity(), trace));
		}
	}

	private static int[] generateTraceFromSortedTree(EfficientTree system, long randomInit) {
		//run 1: count the generating steps
		int numberOfSteps;
		{
			Random randomGenerator = new Random(randomInit);
			AtomicInteger countDownToDeviation = new AtomicInteger(Integer.MAX_VALUE);
			generateTraceFromSortedTree(system, 0, randomGenerator, null, countDownToDeviation);
			numberOfSteps = Integer.MAX_VALUE - countDownToDeviation.get();
		}

		//run 2: generate the trace with deviations
		{
			Random randomGenerator = new Random(randomInit);
			Random deviationRandomGenerator = new Random(randomInit + 1);
			AtomicInteger countDownToDeviation = new AtomicInteger(
					deviationRandomGenerator.nextInt(numberOfSteps - 1) + 1);
			System.out.print("trace " + randomInit + " (" + countDownToDeviation + ") ");
			return generateTraceFromSortedTree(system, 0, randomGenerator, deviationRandomGenerator,
					countDownToDeviation);
		}
	}

	private static int[] generateTraceFromSortedTree(EfficientTree system, int node, Random randomGenerator,
			Random deviationRandomGenerator, AtomicInteger countDownToDeviation) {
		if (system.isTau(node)) {
			return generateTraceTau(system, node);
		} else if (system.isActivity(node)) {
			return generateTraceActivity(system, node);
		} else if (system.isXor(node)) {
			return generateTraceXor(system, node, randomGenerator, deviationRandomGenerator, countDownToDeviation);
		} else if (system.isSequence(node)) {
			return generateTraceSeq(system, node, randomGenerator, deviationRandomGenerator, countDownToDeviation);
		} else if (system.isLoop(node)) {
			return generateTraceXorLoop(system, node, randomGenerator, deviationRandomGenerator, countDownToDeviation);
		} else if (system.isConcurrent(node)) {
			return generateTraceAnd(system, node, randomGenerator, deviationRandomGenerator, countDownToDeviation);
		} else if (system.isInterleaved(node)) {
			return generateTraceInt(system, node, randomGenerator, deviationRandomGenerator, countDownToDeviation);
		}
		throw new RuntimeException("not implemented");
	}

	private static int[] generateTraceTau(EfficientTree system, int node) {
		return new int[0];
	}

	private static int[] generateTraceActivity(EfficientTree system, int node) {
		return new int[] { system.getActivity(node) };
	}

	private static int[] generateTraceXor(EfficientTree system, int node, Random randomGenerator,
			Random deviationRandomGenerator, AtomicInteger countDownToDeviation) {
		int childNr = randomGenerator.nextInt(system.getNumberOfChildren(node));
		int[] childTrace = generateTraceFromSortedTree(system, system.getChild(node, childNr), randomGenerator,
				deviationRandomGenerator, countDownToDeviation);

		if (countDownToDeviation.decrementAndGet() != 0) {
			//normal
			return childTrace;
		} else {
			//deviation: execute two children. use the deviation random generator

			System.out.println("deviation: execute two children of " + EfficientTreeHash.hash(system, node));
			int childNr2 = deviationRandomGenerator.nextInt(system.getNumberOfChildren(node));
			int[] childTrace2 = generateTraceFromSortedTree(system, system.getChild(node, childNr2),
					deviationRandomGenerator, deviationRandomGenerator, countDownToDeviation);
			return GenerateLog.merge(childTrace, childTrace2);
		}
	}

	private static int[] generateTraceSeq(EfficientTree system, int node, Random randomGenerator,
			Random deviationRandomGenerator, AtomicInteger countDownToDeviation) {
		int[] result = new int[0];

		if (countDownToDeviation.decrementAndGet() != 0) {
			//normal execution
		} else {
			//insert random child (use deviation random generator)
			System.out.println("deviation: execute random start child of " + EfficientTreeHash.hash(system, node));
			int childNr2 = deviationRandomGenerator.nextInt(system.getNumberOfChildren(node));
			int child2 = system.getChild(node, childNr2);
			result = GenerateLog.merge(result, generateTraceFromSortedTree(system, child2, deviationRandomGenerator,
					deviationRandomGenerator, countDownToDeviation));
		}

		for (int child : system.getChildren(node)) {
			result = GenerateLog.merge(result, generateTraceFromSortedTree(system, child, randomGenerator,
					deviationRandomGenerator, countDownToDeviation));

			if (countDownToDeviation.decrementAndGet() != 0) {
				//normal execution
			} else {
				//deviation
				if (deviationRandomGenerator.nextBoolean()) {
					//skip child
					System.out.println("deviation: skip child of " + EfficientTreeHash.hash(system, node));
				} else {
					//insert random child (use deviation random generator)
					System.out.println("deviation: execute random child of " + EfficientTreeHash.hash(system, node));
					int childNr2 = deviationRandomGenerator.nextInt(system.getNumberOfChildren(node));
					int child2 = system.getChild(node, childNr2);
					result = GenerateLog.merge(result, generateTraceFromSortedTree(system, child2,
							deviationRandomGenerator, deviationRandomGenerator, countDownToDeviation));
				}
			}
		}
		return result;
	}

	private static int[] generateTraceXorLoop(EfficientTree system, int node, Random randomGenerator,
			Random deviationRandomGenerator, AtomicInteger countDownToDeviation) {
		int leftChild = system.getChild(node, 0);
		int middleChild = system.getChild(node, 1);
		int rightChild = system.getChild(node, 2);

		int[] result;
		if (countDownToDeviation.decrementAndGet() != 0) {
			//normal execution
			result = generateTraceFromSortedTree(system, leftChild, randomGenerator, deviationRandomGenerator,
					countDownToDeviation);
		} else {
			//deviation: skip (still execute for the random generator)
			System.out.println("deviation: skip body of " + EfficientTreeHash.hash(system, node));
			result = new int[0];
			generateTraceFromSortedTree(system, leftChild, randomGenerator, deviationRandomGenerator,
					countDownToDeviation);
		}

		while (randomGenerator.nextInt(10) > 4) {
			if (countDownToDeviation.decrementAndGet() != 0) {
				//normal execution
				result = GenerateLog.merge(result, generateTraceFromSortedTree(system, middleChild, randomGenerator,
						deviationRandomGenerator, countDownToDeviation));
			} else {
				//deviation: skip (still execute for the random generator)
				System.out.println("deviation: skip redo of " + EfficientTreeHash.hash(system, node));
				generateTraceFromSortedTree(system, middleChild, randomGenerator, deviationRandomGenerator,
						countDownToDeviation);
			}

			if (countDownToDeviation.decrementAndGet() != 0) {
				//normal execution
				result = GenerateLog.merge(result, generateTraceFromSortedTree(system, leftChild, randomGenerator,
						deviationRandomGenerator, countDownToDeviation));
			} else {
				//deviation: skip (still execute for the random generator)
				System.out.println("deviation: skip body of " + EfficientTreeHash.hash(system, node));
				generateTraceFromSortedTree(system, leftChild, randomGenerator, deviationRandomGenerator,
						countDownToDeviation);
			}
		}

		if (!system.isTau(rightChild)) {
			//only if the exit is not a tau, we consider it for deviations

			if (countDownToDeviation.decrementAndGet() != 0) {
				//normal execution
				result = GenerateLog.merge(result, generateTraceFromSortedTree(system, rightChild, randomGenerator,
						deviationRandomGenerator, countDownToDeviation));
			} else {
				//deviation: skip (still execute for the random generator)
				System.out.println("deviation: skip exit of " + EfficientTreeHash.hash(system, node));
				generateTraceFromSortedTree(system, rightChild, randomGenerator, deviationRandomGenerator,
						countDownToDeviation);
			}
		}
		return result;
	}

	private static int[] generateTraceAnd(EfficientTree system, int node, Random randomGenerator,
			Random deviationRandomGenerator, AtomicInteger countDownToDeviation) {
		int countChildren = system.getNumberOfChildren(node);

		//put all events in a single trace
		int[] result = new int[0];
		int[][] childrenTraces = new int[countChildren + 1][];
		int[] iterators = new int[countChildren + 1];
		{
			int executeChildAgain = -1; //holds the index of the child that is to be executed twice
			int branch = 0; //loop variable to hold the child number (local index; not related to anything)
			{
				for (int child : system.getChildren(node)) {

					childrenTraces[branch] = generateTraceFromSortedTree(system, child, randomGenerator,
							deviationRandomGenerator, countDownToDeviation);

					if (countDownToDeviation.decrementAndGet() != 0) {
						//normal execution

						//put the branch in the result
						int childTraceLength = childrenTraces[branch].length;
						int[] branchTrace = new int[childTraceLength];
						for (int i = 0; i < childTraceLength; i++) {
							branchTrace[i] = branch;
						}
						iterators[branch] = 0;
						result = GenerateLog.merge(result, branchTrace);

					} else {
						if (deviationRandomGenerator.nextBoolean()) {
							//deviation: skip child
							System.out.println("deviation: skip child of " + EfficientTreeHash.hash(system, node));
						} else {
							//deviation: execute child twice

							//first execution
							int childTraceLength = childrenTraces[branch].length;
							int[] branchTrace = new int[childTraceLength];
							for (int i = 0; i < childTraceLength; i++) {
								branchTrace[i] = branch;
							}
							iterators[branch] = 0;
							result = GenerateLog.merge(result, branchTrace);

							//schedule second execution
							executeChildAgain = child;
							System.out.println(
									"deviation: execute child twice of " + EfficientTreeHash.hash(system, node));
						}
					}

					branch++;
				}
			}

			if (executeChildAgain != -1) {
				//perform the deviation
				childrenTraces[branch] = generateTraceFromSortedTree(system, executeChildAgain,
						deviationRandomGenerator, deviationRandomGenerator, countDownToDeviation);

				//put the branch in the result
				int childTraceLength = childrenTraces[branch].length;
				int[] branchTrace = new int[childTraceLength];
				for (int i = 0; i < childTraceLength; i++) {
					branchTrace[i] = branch;
				}
				iterators[branch] = 0;
				result = GenerateLog.merge(result, branchTrace);
			}
		}

		//result up till now in result: [000001111122233333333]

		//shuffle the result
		GenerateLog.shuffle(result, randomGenerator);

		//replace each branch number by an event
		for (int e = 0; e < result.length; e++) {
			int branch = result[e];
			int pos = iterators[branch];
			iterators[branch]++;
			result[e] = childrenTraces[branch][pos];
		}

		return result;
	}

	private static int[] generateTraceInt(EfficientTree system, int node, Random randomGenerator,
			Random deviationRandomGenerator, AtomicInteger countDownToDeviation) {
		//sort the children
		List<Integer> list = Lists.newArrayList(system.getChildren(node));

		//make a random permutation of children
		Collections.shuffle(list, randomGenerator);

		int[] result = new int[0];
		for (int child : list) {
			result = GenerateLog.merge(result, generateTraceFromSortedTree(system, child, randomGenerator,
					deviationRandomGenerator, countDownToDeviation));
		}
		return result;
	}
}
