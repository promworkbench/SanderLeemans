package batch.infrequency2;

import java.util.concurrent.ExecutionException;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.conversion.ReduceTree;
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
import batch.incompleteness.AbstractBatchRediscoverability;
import batch.incompleteness.miners.MinerOption;
import batch.incompleteness.miners.IMiA.MinerIMiA20;
import generation.CompareTrees;
import generation.GenerateTreeParameters;

@Plugin(name = "Batch test infrequency2", returnLabels = { "Batch infrequency2 result" }, returnTypes = {
		Infrequency2Result.class }, parameterLabels = { "Process tree" }, userAccessible = true)
public class Infrequency2 {

	//	public static MinerOption[] as = new MinerOption[] { new MinerIM(), new MinerIMd(), new MinerIMi01(),
	//			new MinerIMiD01(), new MinerIMi05(), new MinerIMiD05(), new MinerIMi20(), new MinerIMiD20(),
	//			new MinerIMi80(), new MinerIMiD80(), new MinerIMin(), new MinerIMinD() };
	public static MinerOption[] as = new MinerOption[] { new MinerIMiA20() };
	public static GenerateTreeParameters[] ts = new GenerateTreeParameters[] {
			new GenerateTreeParameters(123, true, 15, 3), new GenerateTreeParameters(55555, true, 15, 3),
			new GenerateTreeParameters(112334, true, 15, 3), new GenerateTreeParameters(234, true, 15, 3),
			new GenerateTreeParameters(10651, true, 15, 3), new GenerateTreeParameters(88888, true, 15, 3),
			new GenerateTreeParameters(18, true, 15, 3), new GenerateTreeParameters(2, true, 15, 3),
			new GenerateTreeParameters(25112013, true, 15, 3), new GenerateTreeParameters(1022, true, 15, 3),
			new GenerateTreeParameters(2312, true, 15, 3), new GenerateTreeParameters(5282, true, 15, 3),
			new GenerateTreeParameters(3, true, 15, 3), new GenerateTreeParameters(56666666, true, 15, 3),
			new GenerateTreeParameters(472321841, true, 15, 3), new GenerateTreeParameters(9118919, true, 15, 3),
			new GenerateTreeParameters(521, true, 15, 3), new GenerateTreeParameters(502010, true, 15, 3),
			new GenerateTreeParameters(500100, true, 15, 3), new GenerateTreeParameters(30101055, true, 15, 3),
			new GenerateTreeParameters(5210, true, 15, 3), new GenerateTreeParameters(9607, true, 15, 3),
			new GenerateTreeParameters(5114, true, 15, 3), new GenerateTreeParameters(671700, true, 15, 3),
			new GenerateTreeParameters(1052, true, 15, 3) };
	//	public static GenerateTreeParameters[] ts = new GenerateTreeParameters[] { new GenerateTreeParameters(30101055,
	//			true, 15, 3) };
	public static long[] ds = new long[] { 12345, 6872131, 383, 473321, 14989710, 34987, 4523, 87987, 35645, 65468712,
			4874, 112004112014l, 0, 103487, 93449, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };

	//======

	public static int maximumNumberOfDeviations = 100;
	public static long logSeed = 123456l;
	public static int logSize = 10000;
	public static double probabilityOfDeviation = 0.1;

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness2, default", requiredParameterLabels = {})
	public Infrequency2Result test(PluginContext context) {
		try {
			return testAlgorithmsAndTrees(as, ts, ds);
		} catch (ExecutionException | UnknownTreeNodeException | ReductionFailedException e) {
			return null;
		}
	}

	public static Infrequency2Result testAlgorithmsAndTrees(final MinerOption[] algorithms,
			final GenerateTreeParameters[] treeParameterss, final long[] deviationsSeeds)
			throws ExecutionException, UnknownTreeNodeException, ReductionFailedException {
		JobList jobList = new JobListConcurrent(ThreadPoolSingleton.getInstance());

		final int result[][][] = new int[algorithms.length][treeParameterss.length][deviationsSeeds.length];

		for (int t = 0; t < treeParameterss.length; t++) {
			final int t2 = t;
			final ProcessTree tree = generateTree(treeParameterss[t]);
			for (int a = 0; a < algorithms.length; a++) {
				final int a2 = a;
				for (int d = 0; d < deviationsSeeds.length; d++) {
					final int d2 = d;
					jobList.addJob(new Runnable() {
						public void run() {
							try {
								result[a2][t2][d2] = findMaximumNumberOfDeviations(algorithms[a2], tree,
										deviationsSeeds[d2]);
							} catch (Exception e) {

							}
						}
					});
				}
			}
		}

		jobList.join();
		ThreadPoolSingleton.shutdown();

		return new Infrequency2Result(algorithms, treeParameterss, deviationsSeeds, result);
	}

	/**
	 * Returns the maximum number of deviations for which the tree is
	 * rediscovered.
	 * 
	 * @param algorithm
	 * @param tree
	 * @param deviationsSeed
	 * @return
	 * @throws Exception
	 */
	public static int findMaximumNumberOfDeviations(MinerOption algorithm, ProcessTree tree, long deviationsSeed)
			throws Exception {
		int lastNumberOfDeviations = -1;
		int secondLastNumberOfDeviations = -1;
		int deviatingTraces = 0;
		boolean rediscovered = true;
		while (rediscovered && lastNumberOfDeviations < maximumNumberOfDeviations) {
			Pair<XLog, Integer> p = generateLog(tree, deviatingTraces, deviationsSeed);
			XLog log = p.getA();
			secondLastNumberOfDeviations = lastNumberOfDeviations;
			lastNumberOfDeviations = p.getB();
			rediscovered = doesRediscover(algorithm, tree, log);
			deviatingTraces++;
		}
		return secondLastNumberOfDeviations;
	}

	/**
	 * Returns whether the algorithm returns this tree for this log.
	 * 
	 * @param algorithm
	 * @param tree
	 * @param log
	 * @return
	 * @throws UnknownTreeNodeException
	 * @throws ReductionFailedException
	 */
	public static boolean doesRediscover(MinerOption algorithm, ProcessTree tree, XLog log)
			throws UnknownTreeNodeException, ReductionFailedException {
		ProcessTree discoveredTree;
		if (algorithm.getMiningParameters() != null) {
			discoveredTree = IMProcessTree.mineProcessTree(log, algorithm.getMiningParameters());
		} else {
			discoveredTree = IMdProcessTree.mineProcessTree(
					IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(log, MiningParameters.getDefaultClassifier(),
							MiningParameters.getDefaultLifeCycleClassifier())).getDfg(),
					algorithm.getDfgMiningParameters());
		}
		ReduceTree.reduceTree(discoveredTree, new EfficientTreeReduceParameters(false));
		return CompareTrees.isLanguageEqual(tree, discoveredTree);
	}

	public static Pair<XLog, Integer> generateLog(ProcessTree tree, int numberOfDeviatingTraces, long deviationsSeed)
			throws Exception {
		//generate clean log
		return GenerateLog2.generateLog(tree, logSize, logSeed, deviationsSeed, probabilityOfDeviation,
				numberOfDeviatingTraces);
	}

	public static ProcessTree generateTree(GenerateTreeParameters treeParameters)
			throws UnknownTreeNodeException, ReductionFailedException {
		return AbstractBatchRediscoverability.generateTree(treeParameters);
	}
}
