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

public class remaining extends TestRediscoverability {
	public static final EfficientTree[] trees = new EfficientTree[] { //
			leaf("=== ETM + IM cases ==="), xor(seq(a, and(b, loop(c, d, tau))), e), and(a, seq(b, and(c, d))),

			seq(a, b), seq(a, b, c), seq(a, b, c, d), and(a, b), and(a, b, c), seq(loop(a, b, tau), c),
			seq(loop(a, seq(b, c), tau), d, e),

			loop(seq(a, b), and(c, d), tau), loop(a, b, tau), loop(c, xor(d, seq(a, b)), tau),
			loop(a, loop(b, loop(c, d, tau), tau), tau), loop(c, seq(a, b), tau), loop(a, and(b, c, d), tau),

			loop(c, and(d, loop(a, b, tau)), tau), loop(a, and(b, loop(c, d, tau)), tau),

			leaf("=== harder cases; should be rediscovered ==="), or(a, b), //

			and(a, b, c), and(loop(a, c, tau), loop(b, d, tau)), xor(tau, or(a, b, c)), and(a, xor(tau, and(b, c))),
			loop(and(a, b), and(c, d), tau), loop(and(a, b, c), and(d, e, f), tau), loop(seq(a, b, c), xor(d, e), tau),
			loop(xor(seq(a, b, c), seq(d, e, f)), and(g, h), tau),
			loop(and(seq(a, b, c), seq(d, e, f)), and(g, h), tau), loop(seq(a, xor(tau, b)), c, tau),
			loop(seq(xor(tau, a), b, xor(tau, c)), d, tau),
			loop(seq(xor(tau, leaf("toEat")), leaf("fromHome"),
					xor(leaf("toShop"), seq(leaf("toSport"), leaf("fromSport"))), leaf("toHome")), tau, tau),
			inte(seq(a, b), c), inte(loop(a, b, tau), c), inte(seq(a, b), loop(c, tau, tau)),
			inte(seq(a, b), and(seq(c, d), e), f), inte(seq(a, b, xor(tau, c)), d), inte(a, b, c, d, seq(e, f)),
			inte(and(a, b, seq(c, d)), seq(e, f, g)), inte(xor(a, b), seq(c, d), loop(e, f, tau)), or(a, b, c),
			or(a, and(or(b, c), d, or(e, f))), or(a, and(b, or(c, d))), and(a, or(b, and(c, d))),
			and(xor(tau, a), or(b, c)), and(a, or(b, c)), and(a, or(b, seq(c, d))),

			leaf("=== concurrent-or-optional trees ==="), or(a, and(b, or(c, d))), or(a, and(xor(tau, b), or(c, d))),
			and(a, or(b, and(c, xor(tau, or(d, e))))), and(a, xor(tau, and(b, c)), or(d, e)),
			and(a, xor(tau, and(b, xor(tau, c))), or(d, e)), xor(tau, or(and(a, b), and(c, d))),
			and(a, xor(tau, and(b, xor(tau, c)))), and(a, or(b, seq(xor(tau, c), xor(tau, d)))),
			and(a, or(b, and(c, seq(xor(tau, d), xor(tau, e))))),

			leaf("=== example constraint 3 thesis ==="),
			//		loop(and(loop(seq(a, b), c, tau), d), seq(e, f), tau),
			loop(and(seq(a, b), d), xor(seq(e, f), c), tau),

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