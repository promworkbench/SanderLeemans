package thesis.testMiners.testRediscoverability;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.inte;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.or;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import thesis.testMiners.TestRediscoverability;

public class IMa extends TestRediscoverability {
	public static final EfficientTree[] trees = new EfficientTree[] { //
			
			leaf("=== concurrent-or-optional trees ==="), //
			or(a, and(b, or(c, d))), //
			or(a, and(xor(tau, b), or(c, d))), //
			and(a, or(b, and(c, xor(tau, or(d, e))))), //
			and(a, xor(tau, and(b, c)), or(d, e)), //
			and(a, xor(tau, and(b, xor(tau, c))), or(d, e)), //
			xor(tau, or(and(a, b), and(c, d))), //
			and(a, xor(tau, and(b, xor(tau, c)))), //
			and(a, or(b, seq(xor(tau, c), xor(tau, d)))), //
			and(a, or(b, and(c, seq(xor(tau, d), xor(tau, e))))), //
			and(xor(tau, or(a, b)), c), //
			and(xor(tau, or(c, xor(b, a))), d), //
			and(or(seq(xor(tau, a), xor(tau, b)), c), d), //
			or(d, c, seq(xor(tau, a), xor(tau, b))), //
			and(or(and(seq(xor(tau, a), xor(tau, b)), c), d), e), //

			leaf("=== example constraint 3 thesis ==="), //
			//loop(and(loop(seq(a, b), c, tau), d), seq(e, f), tau), //
			loop(and(seq(a, b), d), xor(seq(e, f), c), tau), //

			leaf("=== figure 5.5 thesis ==="),
			//		inte(xor(and(a, b), and(c, d)), e),
			and(xor(and(a, b), and(c, d)), e),

			leaf("=== figure 5.6 thesis ==="),
			//		inte(seq(a, b), inte(seq(c, d), seq(e, f))),
			//		inte(inte(seq(a, b), seq(c, d)), seq(e, f)),
			inte(seq(a, b), seq(c, d), seq(e, f)),

			leaf("=== incomplete sequence ==="),
			//		xor(seq(a, b, c, d), seq(d, a, b), seq(a, d, c), seq(b, c, d)),

			leaf("=== noise test ==="),
			//		xor(seq(a, b), seq(a, c), seq(d, b), seq(d, c), seq(a, b, a, c)),

	};

	public EfficientTree[] getTrees() {
		return trees;
	}
}