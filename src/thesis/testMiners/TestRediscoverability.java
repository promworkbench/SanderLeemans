package thesis.testMiners;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.inte;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.or;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.tau;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.indulpetminer.mining.MiningParametersBottomUp;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeHash;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeUtils;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;
import org.processmining.plugins.inductiveminer2.plugins.InductiveMinerPlugin;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIM;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequentLifeCycle;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequentPartialTraces;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequentPartialTracesAli;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMLifeCycle;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMPartialTraces;

import p2015sosym.efficienttree.generatebehaviour.GenerateLog;
import thesis.testMiners.testRediscoverability.BaseCases;
import thesis.testMiners.testRediscoverability.IM;
import thesis.testMiners.testRediscoverability.Indulpet;

public abstract class TestRediscoverability {

	private static int numberOfTraces = 100;
	private static long randomSeed = 1;
	private static boolean debug = true;

	public abstract EfficientTree[] getTrees();

	//abbreviations
	protected static EfficientTree a = leaf("a");
	protected static EfficientTree b = leaf("b");
	protected static EfficientTree c = leaf("c");
	protected static EfficientTree d = leaf("d");
	protected static EfficientTree e = leaf("e");
	protected static EfficientTree f = leaf("f");
	protected static EfficientTree g = leaf("g");
	protected static EfficientTree h = leaf("h");
	protected static EfficientTree i = leaf("i");
	protected static EfficientTree j = leaf("j");
	protected static EfficientTree k = leaf("k");
	protected static EfficientTree s = leaf("s");
	protected static EfficientTree tau = tau();

	private static Test[] tests = new Test[] { //
			new Test(new MiningParametersBottomUp(), new BaseCases(), new Indulpet()), //
			new Test(new MiningParametersIM(), new BaseCases(), new IM()), //
			new Test(new MiningParametersIMInfrequent(), new BaseCases(), new IM()), //
			new Test(new MiningParametersIMLifeCycle(), new BaseCases(), new IM()), //
			new Test(new MiningParametersIMInfrequentLifeCycle(), new BaseCases(), new IM()), //
			new Test(new MiningParametersIMPartialTraces(), new BaseCases(), new IM()), //
			new Test(new MiningParametersIMInfrequentPartialTraces(), new BaseCases(), new IM()), //
			new Test(new MiningParametersIMInfrequentPartialTracesAli(), new BaseCases(), new IM()),
			//new Test(new MiningParametersIndulpet(), new BaseCases(), new IM()), //
	};

	public static class Test {
		public Test(MiningParametersAbstract miningParameters, TestRediscoverability... sets) {
			this.miningParameters = miningParameters;
			this.sets = sets;
		}

		MiningParametersAbstract miningParameters;
		TestRediscoverability[] sets;
	}

	public static final Canceller canceller = new Canceller() {
		public boolean isCancelled() {
			return false;
		}
	};

	public static boolean test(boolean stopAtNotRediscovered) throws Exception {
		for (Test test : tests) {
			System.out.println("===== test algorithm " + test.miningParameters.toString() + " ======");
			boolean result = testAlgorithm(stopAtNotRediscovered, test.miningParameters, test.sets);
			if (!result && stopAtNotRediscovered) {
				return false;
			}
		}
		System.out.println("===== rediscoverability tests completed ======");
		return true;
	}

	public static boolean testAlgorithm(boolean stopAtNotRediscovered, MiningParametersAbstract miningParameters,
			TestRediscoverability[] sets) throws Exception {
		miningParameters.setDebug(false);
		miningParameters.setNoiseThreshold(0);
		for (TestRediscoverability systems : sets) {
			for (EfficientTree system : systems.getTrees()) {
				assert (EfficientTreeUtils.isConsistent(system));
				XLog xLog = GenerateLog.generateIMLog(system, numberOfTraces, randomSeed, false);

				//generate a log and mine a process tree from it
				//XLog log = GenerateLog.generateLog(system, numberOfTraces, randomSeed);
				//EfficientTree discoveredTree = new EfficientTree(IMProcessTree.mineProcessTree(log, miningParameters));

				IMLog log = miningParameters.getIMLog(xLog);
				EfficientTree discoveredTree = InductiveMiner.mineEfficientTree(log, miningParameters, canceller);

				//verify equality using hashes
				if (EfficientTreeHash.hash(system).equals(EfficientTreeHash.hash(discoveredTree))) {
					System.out.println("tree rediscovered:     " + system);
				} else {
					System.out.println("tree not rediscovered: " + system);
					System.out.println("                  got: " + discoveredTree);

					if (debug) {
						System.out.println("=======================");
						System.out.println(" mine again with debug output");
						miningParameters.setDebug(true);
						InductiveMinerPlugin.mineTree(log, miningParameters, canceller);
						//IMProcessTree.mineProcessTree(log, miningParameters);
						miningParameters.setDebug(false);
						System.out.println("=======================");
					}
					if (stopAtNotRediscovered) {
						System.out.println("stop further tests");
						return false;
					}
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void neverCalledButKeepsImports() {
		xor(seq(and(loop(inte(or(a, a), a), a, a), a), a), leaf(""));
	}
}
