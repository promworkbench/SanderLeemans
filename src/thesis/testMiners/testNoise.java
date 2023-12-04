package thesis.testMiners;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.inte;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.or;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.tau;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;
import static thesis.testMiners.InlineLog.fromTree;
import static thesis.testMiners.InlineLog.log;
import static thesis.testMiners.InlineLog.trace;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeHash;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeUtils;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMa;
import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;

public class testNoise {

	private static MiningParameters miningParameters = new MiningParametersIMa();
	private static boolean debug = true;

	private static class N {
		int[][] log;
		EfficientTree tree;
	}

	private static N check(EfficientTree tree, int[][]... sublogs) {
		N r = new N();
		r.log = log(sublogs);
		r.tree = tree;
		return r;
	}

	private static N check(EfficientTree tree, int numberOfTraces, int[][]... sublogs) {
		N r = new N();
		r.log = log(fromTree(tree, numberOfTraces), sublogs);
		r.tree = tree;
		return r;
	}

	//abbreviations
	private static EfficientTree a = leaf("a");
	private static EfficientTree b = leaf("b");
	private static EfficientTree c = leaf("c");
	private static EfficientTree d = leaf("d");
	private static EfficientTree e = leaf("e");
	private static EfficientTree f = leaf("f");
	private static EfficientTree g = leaf("g");
	private static EfficientTree tau = tau();

	private static String[] int2activity = new String[] { "a", "b", "c", "d", "e", "f" };
	private static int la = 0;
	private static int lb = 1;
	private static int lc = 2;
	private static int ld = 3;

	// @formatter:off
	private static N[] systems = new N[] { check(a, trace(la)), check(inte(seq(a, b), c), 1000, trace(la, lb)),
			check(inte(seq(a, b), c), 1000, trace(la, lb, lc, la, lb)), };
	// @formatter:on

	public static boolean test(boolean stopAtError) throws Exception {
		miningParameters.setDebug(false);

		for (N system : systems) {

			assert (EfficientTreeUtils.isConsistent(system.tree));

			String treeHash = EfficientTreeHash.hash(system.tree);

			//reduce the tree
			EfficientTreeReduce.reduce(system.tree);

			//generate an XLog
			XLog log = log(int2activity, system.log);

			//mine a process tree
			EfficientTree discoveredTree = ProcessTree2EfficientTree
					.convert(IMProcessTree.mineProcessTree(log, miningParameters));

			//verify equality using hashes
			if (treeHash.equals(EfficientTreeHash.hash(discoveredTree))) {
				System.out.println("tree rediscovered with noise:     " + treeHash);
			} else {
				System.out.println("tree not rediscovered with noise: " + treeHash);
				System.out.println("                             got: " + discoveredTree);

				if (debug) {
					System.out.println("=======================");
					System.out.println(" mine again with debug output");
					miningParameters.setDebug(true);
					IMProcessTree.mineProcessTree(log, miningParameters);
					miningParameters.setDebug(false);
					System.out.println("=======================");
				}
				if (stopAtError) {
					System.out.println("stop further tests");
					return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void neverCalledButKeepsImports() {
		xor(seq(and(loop(inte(or(a, b), c), d, e), f), or(g, tau)), leaf(""));
		log(int2activity, fromTree(a, 2), trace(la));
	}
}
