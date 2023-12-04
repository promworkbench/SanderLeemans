package thesis.testMiners;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.inte;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.or;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.tau;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeHash;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeUtils;

public class testReductionRules {

	private static class R {
		EfficientTree before;
		EfficientTree after;
	}

	private static R check(EfficientTree before, EfficientTree after) {
		R r = new R();
		r.before = before;
		r.after = after;
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
	private static EfficientTree h = leaf("h");
	private static EfficientTree i = leaf("i");
	private static EfficientTree j = leaf("j");
	private static EfficientTree k = leaf("k");
	private static EfficientTree s = leaf("s");
	private static EfficientTree tau = tau();

	// @formatter:off
	private static R[] systems = new R[] {
			//dummy
			check(tau, tau), check(xor(tau, a), xor(a, tau)),
			//nesting
			check(seq(a, seq(b, c), d), seq(a, b, c, d)), check(or(a, or(b, c)), or(a, b, c)),
			//tau-removal from or
			check(or(a, b, tau), xor(tau, or(a, b))), check(or(a, xor(tau, b)), xor(tau, or(a, b))),
			check(or(a, xor(tau, tau, tau, b, b)), xor(tau, or(a, xor(b, b)))),
			//and -> or
			check(and(c, xor(tau, d), xor(tau, e)), and(xor(tau,or(d,e)),c)), //
			check(and(xor(tau, a), xor(tau, b)), xor(tau, or(a, b))), //
			check(and(xor(tau, a), xor(tau, b), c), and(c, xor(tau, or(a, b)))), //
			//loop-tau removal
			check(loop(a, xor(seq(xor(tau, b), xor(tau, c)), tau), tau), loop(a, seq(xor(tau, b), xor(tau, c)), tau)),
			//and2or bug test: wouldn't copy nodes after the given node was finished
			check(and(xor(tau, a), and(xor(tau, b), and(c, and(xor(tau, d), xor(tau, e))))),
					and(c, xor(tau, or(d, e, a, b)))),
			//concurrent-or-optional
			check(and(a, b, c), null), check(and(a, b, xor(tau, c)), null), check(and(a, xor(tau, b), c), null),
			check(and(xor(tau, a), b, c), null), check(and(a, xor(tau, or(b, c))), null),
			check(and(b, xor(tau, or(a, c))), null), check(and(c, xor(tau, or(a, b))), null),
			check(and(a, or(b, c)), null), check(and(b, or(a, c)), null), check(and(c, or(a, b)), null),
			check(or(a, b, c), null), check(or(a, and(b, c)), null), check(or(b, and(a, c)), null),
			check(or(c, and(a, b)), null), check(or(a, and(xor(tau, b), c)), null),
			check(or(a, and(b, xor(tau, c))), null), check(or(b, and(xor(tau, a), c)), null),
			check(or(b, and(a, xor(tau, c))), null), check(or(c, and(xor(tau, a), b)), null),
			check(or(c, and(a, xor(tau, b))),
					null),
			check(xor(tau,
					and(a, b,
							c)),
					null),
			check(xor(tau, and(a, b, xor(tau, c))), null), check(xor(tau, and(a, xor(tau, b), c)), null),
			check(xor(tau, and(xor(tau, a), b, c)), null), check(xor(tau, and(a, xor(tau, or(b, c)))), null),
			check(xor(tau, and(b, xor(tau, or(a, c)))), null), check(xor(tau, and(c, xor(tau, or(a, b)))), null),
			check(xor(tau, and(a, or(b, c))), null), check(xor(tau, and(b, or(a, c))), null),
			check(xor(tau, and(c, or(a, b))), null),
			check(xor(tau, or(a, b, c)),
					null),
			check(xor(tau, or(a, and(b, c))),
					null),
			check(xor(tau, or(b, and(a, c))),
					null),
			check(xor(tau, or(c, and(a, b))), null), check(xor(tau, or(a, and(xor(tau, b), c))), null),
			check(xor(tau, or(a, and(b, xor(tau, c)))), null), check(xor(tau, or(b, and(xor(tau, a), c))), null),
			check(xor(tau, or(b, and(a, xor(tau, c)))), null), check(xor(tau, or(c, and(xor(tau, a), b))), null),
			check(xor(tau, or(c, and(a, xor(tau, b)))), null), };
	// @formatter:on

	public static boolean test(boolean stopAtError) throws Exception {

		EfficientTreeReduceParameters parameters = new EfficientTreeReduceParameters(false, true);

		for (R system : systems) {
			if (!testSystem(stopAtError, parameters, system)) {
				return false;
			}
		}
		return true;
	}

	private static boolean testSystem(boolean stopAtError, EfficientTreeReduceParameters parameters, R system)
			throws CloneNotSupportedException, ReductionFailedException {
		if (system.after == null) {
			system.after = system.before.clone();
		}

		assert (EfficientTreeUtils.isConsistent(system.before));
		assert (EfficientTreeUtils.isConsistent(system.after));

		String hashBefore = EfficientTreeHash.hash(system.before);

		//reduce the tree
		EfficientTreeReduce.reduce(system.before, parameters);

		//verify equality using hashes
		if (EfficientTreeHash.hash(system.before).equals(EfficientTreeHash.hash(system.after))) {
			System.out.println("reduction succesful: " + hashBefore + " -> " + system.after);
		} else {
			System.out.println("reduction failed:    " + hashBefore + " -> " + system.after);
			System.out.println("             got:    " + system.before);

			if (stopAtError) {
				System.out.println("stop further tests");
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void neverCalledButKeepsImports() {
		xor(seq(and(loop(inte(or(a, a), a), a, a), a), a), leaf(""));
	}
}
