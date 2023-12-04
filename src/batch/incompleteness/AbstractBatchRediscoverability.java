package batch.incompleteness;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.conversion.ReduceTree;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParameters;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.IMdProcessTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.jobList.JobList;
import org.processmining.plugins.InductiveMiner.jobList.JobListConcurrent;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;
import org.processmining.processtree.ProcessTree;

import batch.ThreadPoolSingleton;
import batch.incompleteness.miners.MinerOption;
import generation.CompareTrees;
import generation.Entropy;
import generation.EntropyResult;
import generation.GenerateTree;
import generation.GenerateTreeParameters;

public abstract class AbstractBatchRediscoverability {

	public static class RunResult {
		public final XLog log;
		public final ProcessTree discoveredTree;
		public final boolean rediscovered;
		public final int logSize;
		public final XTrace lastTrace;
		public final long miningTime;

		public RunResult(XLog log, ProcessTree discoveredTree, boolean rediscovered, int logSize, XTrace lastTrace,
				long miningTime) {
			this.log = log;
			this.discoveredTree = discoveredTree;
			this.rediscovered = rediscovered;
			this.logSize = logSize;
			this.lastTrace = lastTrace;
			this.miningTime = miningTime;
		}
	}

	public static class RunsResult {
		public final double averageLargestFailingLogSize;
		public final double averageDfgCompleteness;
		public final double averageEfgCompleteness;
		public final double averageMiningTime;
		public final int rediscovered;

		public RunsResult(double averageLargestFailingLogSize, double averageDfgCompleteness,
				double averageEfgCompleteness, int rediscovered, double averageMiningTime) {
			this.averageLargestFailingLogSize = averageLargestFailingLogSize;
			this.averageDfgCompleteness = averageDfgCompleteness;
			this.averageEfgCompleteness = averageEfgCompleteness;
			this.rediscovered = rediscovered;
			this.averageMiningTime = averageMiningTime;
		}
	}

	private IncompletenessParameters incompletenessParameters = new IncompletenessParameters();

	public BatchRediscoverabilityResult batch(final PluginContext context, String title) {
		final BatchRediscoverabilityResult result = new BatchRediscoverabilityResult(incompletenessParameters, null,
				title);
		JobList pool = new JobListConcurrent(ThreadPoolSingleton.getInstance());

		for (final GenerateTreeParameters treeParameters : incompletenessParameters.getTreeSeeds()) {
			pool.addJob(new Runnable() {
				public void run() {
					try {
						runMinersOnTree(context, result, generateTree(treeParameters), treeParameters.toString());
					} catch (UnknownTreeNodeException | ReductionFailedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		try {
			pool.join();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		ThreadPoolSingleton.shutdown();
		System.out.println("finished");
		return result;
	}

	public BatchRediscoverabilityResult findLargestNonrediscoverableLog(PluginContext context, ProcessTree tree,
			String title) {
		IncompletenessParameters incompletenessParameters = new IncompletenessParameters();
		incompletenessParameters.setTreeSeeds(null);

		List<String> trees = new LinkedList<String>();
		trees.add("");
		BatchRediscoverabilityResult result = new BatchRediscoverabilityResult(incompletenessParameters, trees, title);
		runMinersOnTree(context, result, tree, "");

		return result;
	}

	public void runMinersOnTree(PluginContext context, BatchRediscoverabilityResult result, ProcessTree tree,
			GenerateTreeParameters treeSeed) {
		runMinersOnTree(context, result, tree, treeSeed.toString());
	}

	public void runMinersOnTree(final PluginContext context, final BatchRediscoverabilityResult result,
			final ProcessTree tree, final String treeSeed) {
		JobList pool = new JobListConcurrent(ThreadPoolSingleton.getInstance());
		for (final MinerOption minerOption : incompletenessParameters.getMinerOptions()) {
			pool.addJob(new Runnable() {
				public void run() {
					RunsResult x;
					try {
						x = runMinerOnLogs(context, tree, minerOption);

						debug(" miner " + minerOption.toString() + " and tree " + treeSeed.toString() + ":");
						debug(" average log size " + x.averageLargestFailingLogSize);
						debug(" average directly-follows completeness " + x.averageDfgCompleteness);
						debug(" average eventually-follows completeness " + x.averageEfgCompleteness);
						debug("");
						result.recordResult(incompletenessParameters, minerOption, treeSeed,
								x.averageLargestFailingLogSize, x.averageDfgCompleteness, x.averageEfgCompleteness,
								x.rediscovered, x.averageMiningTime);
					} catch (UnknownTreeNodeException | ReductionFailedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		try {
			pool.join();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		ThreadPoolSingleton.shutdown();
	}

	//run a miner on all given logs
	private RunsResult runMinerOnLogs(PluginContext context, ProcessTree generatedTree, MinerOption minerOption)
			throws UnknownTreeNodeException, ReductionFailedException {

		long totalLogSize = 0;
		double totalDfgCompleteness = 0;
		double totalEfgCompleteness = 0;
		long totalMiningTime = 0;
		int rediscovered = 0;
		for (long logSeed = incompletenessParameters.getStartLogSeed(); logSeed <= incompletenessParameters
				.getEndLogSeed(); logSeed++) {
			RunResult[] result = findBiggestFailingLog(context, generatedTree, logSeed,
					minerOption.getMiningParameters(), minerOption.getDfgMiningParameters(), minerOption.toString());

			if (result[1] != null) {
				//log was rediscovered
				EntropyResult entropy = Entropy.check(generatedTree, result[1].log);
				totalDfgCompleteness += entropy.getDfgEntropy();
				totalEfgCompleteness += entropy.getEfgEntropy();
				totalMiningTime += result[1].miningTime;
				totalLogSize += result[1].logSize;
				rediscovered++;
				//debug(entropy.toString());
			} else {
				//log was not rediscovered
				//				totalDfgCompleteness += 1;
				//				totalEfgCompleteness += 1;
				//				totalLogSize += incompletenessParameters.getMaxLogSize();
			}
		}

		double numberOfLogs = 1 + incompletenessParameters.getEndLogSeed() - incompletenessParameters.getStartLogSeed();

		if (rediscovered > 0) {
			double averageLogSize = totalLogSize / rediscovered;
			double averageDfgCompleteness = totalDfgCompleteness / rediscovered;
			double averageEfgCompleteness = totalEfgCompleteness / rediscovered;
			double averageMiningTime = totalMiningTime / (rediscovered * 1000.0);
			return new RunsResult(averageLogSize, averageDfgCompleteness, averageEfgCompleteness, rediscovered,
					averageMiningTime);
		} else {
			return new RunsResult(-1, -1, -1, 0, -1);
		}
	}

	public RunResult[] findBiggestFailingLog(PluginContext context, ProcessTree generatedTree, long logSeed,
			MiningParameters parameters, DfgMiningParameters dfgParameters, String minerId)
			throws UnknownTreeNodeException, ReductionFailedException {

		RunResult a = new RunResult(null, null, false, 0, null, -1);
		RunResult b = new RunResult(null, null, false, 1, null, -1);

		//find a log size for which the model is rediscovered
		while (!b.rediscovered) {
			a = b;
			int newB = b.logSize * 4;

			b = generateLogDiscover(context, generatedTree, logSeed, newB, parameters, dfgParameters);
			if (newB >= incompletenessParameters.getMaxLogSize() && !b.rediscovered) {

				debug(" tree not rediscovered for " + minerId + ", log seed " + logSeed);
				debug("  got    " + CompareTrees.hash(b.discoveredTree.getRoot()));
				debug("  should " + CompareTrees.hash(generatedTree.getRoot()));
				debug("");

				return new RunResult[] { b, null };
			}
		}

		int logSizeC = (a.logSize + b.logSize) / 2;

		//find the largest log for which the model is not rediscovered
		while (a.logSize < b.logSize - 1) {

			RunResult c = generateLogDiscover(context, generatedTree, logSeed, logSizeC, parameters, dfgParameters);

			if (c.rediscovered) {
				//log can be rediscovered, take a smaller log
				b = c;
			} else {
				//log cannot be rediscovered, take a bigger log
				a = c;
			}
			logSizeC = (a.logSize + b.logSize) / 2;
		}

		debug(" largest failing log size " + a.logSize + " for " + minerId + ", log seed " + logSeed);
		if (a.discoveredTree != null) {
			debug("  got    " + CompareTrees.hash(a.discoveredTree.getRoot()));
		}
		debug("  should " + CompareTrees.hash(generatedTree.getRoot()));
		debug("  rediscovery-enabling trace " + traceToString(b.lastTrace));
		debug("");

		return new RunResult[] { a, b };

	}

	public static ProcessTree generateTree(GenerateTreeParameters treeParameters)
			throws UnknownTreeNodeException, ReductionFailedException {
		//generate a tree
		ProcessTree tree = (new GenerateTree()).generateTree(treeParameters);
		ReduceTree.reduceTree(tree, new EfficientTreeReduceParameters(false));
		return tree;
	}

	protected abstract XLog generateLog(ProcessTree tree, long logSeed, int logSize);

	/**
	 * generates a log, discovers a model from it, and compares it to the given
	 * tree
	 * 
	 * @param context
	 * @param tree
	 *            (must be reduced)
	 * @param logSeed
	 * @param logSize
	 * @param parameters
	 * @param dfgParameters
	 * @return
	 * @throws UnknownTreeNodeException
	 * @throws ReductionFailedException
	 */
	public RunResult generateLogDiscover(PluginContext context, ProcessTree tree, long logSeed, int logSize,
			MiningParameters parameters, DfgMiningParameters dfgParameters)
			throws UnknownTreeNodeException, ReductionFailedException {

		//generate log
		XLog log = generateLog(tree, logSeed, logSize);

		long start = System.currentTimeMillis();

		//discover a model
		ProcessTree discoveredTree = discover(context, log, parameters, dfgParameters);

		long end = System.currentTimeMillis();

		//reduce the discovered tree
		ReduceTree.reduceTree(discoveredTree, new EfficientTreeReduceParameters(false));

		//check rediscoverability
		boolean rediscovered = CompareTrees.isLanguageEqual(tree, discoveredTree);

		return new RunResult(log, discoveredTree, rediscovered, logSize, log.get(log.size() - 1), end - start);
	}

	public static ProcessTree discover(PluginContext context, XLog log, MiningParameters parameters,
			DfgMiningParameters dfgParameters) {
		if (parameters != null) {
			return IMProcessTree.mineProcessTree(log, parameters);
		} else {
			return IMdProcessTree.mineProcessTree(IMLog2IMLogInfoDefault
					.log2logInfo(new IMLogImpl(log, parameters.getClassifier(), parameters.getLifeCycleClassifier()))
					.getDfg(), dfgParameters);
		}
	}

	public static List<String> traceToString(XTrace trace) {
		List<String> s = new LinkedList<String>();
		for (XEvent e : trace) {
			s.add(e.getAttributes().get("concept:name").toString());
		}
		return s;
	}

	private void debug(String x) {
		System.out.println(x);
	}
}
