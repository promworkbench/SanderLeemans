package batch.stability;

import generation.Entropy;
import generation.EntropyResult;
import generation.GenerateLog;
import generation.GenerateLogParameters;
import generation.GenerateTree;
import generation.GenerateTreeParameters;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.jobList.JobList;
import org.processmining.plugins.InductiveMiner.jobList.JobListBlocking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.processtree.ProcessTree;

import batch.miners.Miner;
import batch.miners.MinerClass;
import batch.miners.isomorphism.Isomorphic;

@Plugin(name = "Batch-test stability", returnLabels = { "Batch stability result",
		"dummy for alpha miner that for some reason requires two return objects" }, returnTypes = {
		StabilityResult.class, StabilityResult.class }, parameterLabels = { "Process tree" }, userAccessible = true)
public class batchStability {

	public static class RunResult {
		public final XLog log;
		public final PetrinetWithInitialMarking discoveredTree;
		public final boolean rediscovered;
		public final int logSize;
		public final XTrace lastTrace;

		public RunResult(XLog log, PetrinetWithInitialMarking discoveredTree, boolean rediscovered, int logSize,
				XTrace lastTrace) {
			this.log = log;
			this.discoveredTree = discoveredTree;
			this.rediscovered = rediscovered;
			this.logSize = logSize;
			this.lastTrace = lastTrace;
		}
	}

	public static class RunsResult {
		public final double averageLargestFailingLogSize;
		public final double averageDfgCompleteness;
		public final double averageEfgCompleteness;

		public RunsResult(double averageLargestFailingLogSize, double averageDfgCompleteness,
				double averageEfgCompleteness) {
			this.averageLargestFailingLogSize = averageLargestFailingLogSize;
			this.averageDfgCompleteness = averageDfgCompleteness;
			this.averageEfgCompleteness = averageEfgCompleteness;
		}
	}

	public static class PetrinetWithInitialMarking {
		public final Petrinet petrinet;
		public final Marking initialMarking;

		public PetrinetWithInitialMarking(Petrinet petrinet, Marking initialMarking) {
			this.petrinet = petrinet;
			this.initialMarking = initialMarking;
		}
	}

	private StabilityParameters stabilityParameters = new StabilityParameters();

	/*
	 * Test all logs from the parameters
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness, default", requiredParameterLabels = {})
	public Object[] batch(final PluginContext context) {

		//context.getConnectionManager().setEnabled(false);

		final StabilityResult result = new StabilityResult(stabilityParameters, null);
		JobList pool = new JobListBlocking();
		//JobList pool = new JobListConcurrent(ThreadPoolSingleton3.getInstance());

		for (final GenerateTreeParameters treeParameters : stabilityParameters.getTreeSeeds()) {
			pool.addJob(new Runnable() {
				public void run() {
					runMinersOnTree(context, result, generateTree(treeParameters), treeParameters.toString());
				}
			});
		}

		try {
			pool.join();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return new Object[] { result, result };
	}

	/*
	 * Test a single tree
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness, default", requiredParameterLabels = { 0 })
	public StabilityResult findLargestNonrediscoverableLog(PluginContext context, ProcessTree tree) {
		batchStability inc = new batchStability();
		StabilityParameters incompletenessParameters = new StabilityParameters();
		incompletenessParameters.setTreeSeeds(null);

		List<String> trees = new LinkedList<String>();
		trees.add("");
		StabilityResult result = new StabilityResult(incompletenessParameters, trees);
		inc.runMinersOnTree(context, result, tree, "");

		return result;
	}

	public void runMinersOnTree(PluginContext context, StabilityResult result, ProcessTree tree,
			GenerateTreeParameters treeSeed) {
		runMinersOnTree(context, result, tree, treeSeed.toString());
	}

	public void runMinersOnTree(PluginContext context, StabilityResult result, ProcessTree tree, String treeSeed) {
		for (MinerClass minerClass : stabilityParameters.getMinerClasses()) {
			RunsResult x;
			try {
				x = runMinerOnLogs(context, tree, minerClass);

				debug(" miner " + minerClass.identification + " and tree " + treeSeed.toString() + ":");
				debug(" average log size " + x.averageLargestFailingLogSize);
				debug(" average directly-follows completeness " + x.averageDfgCompleteness);
				debug(" average eventually-follows completeness " + x.averageEfgCompleteness);
				debug("");
				result.recordResult(stabilityParameters, minerClass, treeSeed, x.averageLargestFailingLogSize,
						x.averageDfgCompleteness, x.averageEfgCompleteness);
			} catch (UnknownTreeNodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//run a miner on all given logs
	private RunsResult runMinerOnLogs(PluginContext context, ProcessTree generatedTree, MinerClass minerClass)
			throws UnknownTreeNodeException {

		long totalLogSize = 0;
		double totalDfgCompleteness = 0;
		double totalEfgCompleteness = 0;
		for (long logSeed = stabilityParameters.getStartLogSeed(); logSeed <= stabilityParameters.getEndLogSeed(); logSeed++) {
			RunResult[] result = findBiggestFailingLog(context, generatedTree, logSeed, minerClass,
					minerClass.identification);

			if (result[1] != null) {
				EntropyResult entropy = Entropy.check(generatedTree, result[1].log);
				totalDfgCompleteness += entropy.getDfgEntropy();
				totalEfgCompleteness += entropy.getEfgEntropy();
				totalLogSize += result[1].logSize;
				//debug(entropy.toString());
			} else {
				totalDfgCompleteness += 1;
				totalEfgCompleteness += 1;
				totalLogSize += stabilityParameters.getMaxLogSize();
			}
		}

		double numberOfLogs = 1 + stabilityParameters.getEndLogSeed() - stabilityParameters.getStartLogSeed();

		double averageLogSize = totalLogSize / numberOfLogs;
		double averageDfgCompleteness = totalDfgCompleteness / numberOfLogs;
		double averageEfgCompleteness = totalEfgCompleteness / numberOfLogs;

		return new RunsResult(averageLogSize, averageDfgCompleteness, averageEfgCompleteness);

	}

	public RunResult[] findBiggestFailingLog(PluginContext context, ProcessTree generatedTree, long logSeed,
			MinerClass minerClass, String minerId) {

		//find the top element
		PetrinetWithInitialMarking topElement = generateLogDiscoverTopElement(context, generatedTree, logSeed,
				minerClass);

		RunResult a = new RunResult(null, null, false, 0, null);
		RunResult b = new RunResult(null, null, false, 1, null);

		//find a log size for which the model is rediscovered
		while (!b.rediscovered) {
			a = b;
			int newB = b.logSize * 4;

			b = generateLogDiscover(context, generatedTree, minerClass, logSeed, newB, topElement);
			if (newB >= stabilityParameters.getMaxLogSize() && !b.rediscovered) {

				/*
				 * debug(" tree not rediscovered for for " + minerId +
				 * ", log seed " + logSeed); debug("  got    " +
				 * CompareTrees.hash(b.discoveredTree.getRoot()));
				 * debug("  should " +
				 * CompareTrees.hash(generatedTree.getRoot())); debug("");
				 */

				return new RunResult[] { b, null };
			}
		}

		int logSizeC = (a.logSize + b.logSize) / 2;

		//find the largest log for which the model is not rediscovered
		while (a.logSize < b.logSize - 1) {

			RunResult c = generateLogDiscover(context, generatedTree, minerClass, logSeed, logSizeC, topElement);

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
		//		if (a.discoveredTree != null) {
		//			debug("  got    " + CompareTrees.hash(a.discoveredTree.getRoot()));
		//		}
		//		debug("  should " + CompareTrees.hash(generatedTree.getRoot()));
		debug("  rediscovery-enabling trace " + traceToString(b.lastTrace));
		debug("");

		return new RunResult[] { a, b };

	}

	/*
	 * generates a log, discovers a model from it, and compares it to the top
	 * element
	 */
	public RunResult generateLogDiscover(PluginContext context, ProcessTree generatedTree, MinerClass minerClass,
			long logSeed, int logSize, PetrinetWithInitialMarking topElement) {

		//generate log
		XLog log = generateLog(generatedTree, logSeed, logSize);

		//discover a model
		PetrinetWithInitialMarking discoveredNet = discover(context, log, minerClass);

		//isomorphism of petri nets
		boolean rediscovered = isEqual(discoveredNet, topElement, minerClass);

		context.clear();

		return new RunResult(log, discoveredNet, rediscovered, logSize, log.get(log.size() - 1));
	}

	public PetrinetWithInitialMarking generateLogDiscoverTopElement(PluginContext context, ProcessTree tree,
			long logSeed, MinerClass minerClass) {
		//generate log
		XLog log = generateLog(tree, logSeed, stabilityParameters.getMaxLogSize());

		//discover a model
		PetrinetWithInitialMarking topElement = discover(context, log, minerClass);

		return topElement;
	}

	private ProcessTree generateTree(GenerateTreeParameters treeParameters) {
		//generate a tree
		return (new GenerateTree()).generateTree(treeParameters);
	}

	private XLog generateLog(ProcessTree tree, long logSeed, int logSize) {
		//generate a log
		try {
			return (new GenerateLog()).generateLog(tree, new GenerateLogParameters(logSize, logSeed));
		} catch (Exception e) {
			return null;
		}
	}

	public static PetrinetWithInitialMarking discover(PluginContext context, XLog log, MinerClass minerClass) {
		XEventClassifier classifier = new XEventAndClassifier(new XEventNameClassifier(),
				new XEventLifeTransClassifier());
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
		Miner miner = minerClass.newInstantation();
		Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> q;
		try {
			q = miner.mine(context, log, classifier, logInfo, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new PetrinetWithInitialMarking(q.getA(), q.getB());
	}

	private boolean isEqual(PetrinetWithInitialMarking a, PetrinetWithInitialMarking b, MinerClass minerClass) {
		Isomorphic isoCheck = minerClass.newIsoCheckInstantation();
		return isoCheck.isIsomorphic(a, b);
	}

	private List<String> traceToString(XTrace trace) {
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
