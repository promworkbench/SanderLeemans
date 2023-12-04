package thesis.testMiners.testRediscoverability;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import thesis.testMiners.TestRediscoverability;

public class Indulpet extends TestRediscoverability {
	public static final EfficientTree[] trees = new EfficientTree[] { //
			leaf("=== Indulpet bottom up ==="), //

			seq(and(a, b, c), d), //

			loop(seq(a, b, c), tau, tau), //

			xor(loop(a, b, tau), loop(c, d, tau), loop(e, f, tau)), //

			and(loop(a, b, tau), c), //

			seq(a, b), //
			seq(a, b, c, d), //

			and(a, b), //
			and(a, b, c, d, e, f), //

			seq(and(a, b, c), d, and(e, f, g)), //
			and(seq(a, b, c), seq(d, e, f)), //

			seq(a, and(b, loop(and(c, d), seq(e, f), tau), g), h), //

			loop(a, b, tau), //
			loop(seq(a, b, c), and(d, e, f), tau), //
			loop(and(a, b), c, tau), //
			loop(a, loop(b, c, tau), tau), //

			xor(a, b), //
			xor(a, seq(b, c)), //
			seq(a, xor(b, c)), //
			seq(xor(a, b), xor(c, d)), //
			seq(xor(and(a, b), c), d), //
			and(a, xor(b, seq(c, d), e, f), g), //

			seq(xor(a, seq(b, c)), d), //
			seq(a, xor(b, seq(c, d)), e), //
			seq(s, and(seq(f, and(h, seq(g, i)), k), seq(b, xor(d, seq(c, e)), j)), leaf("a12")), //

			seq(xor(seq(a, and(b, c), d), e), f), //
			seq(s, xor(seq(f, and(h, seq(g, i)), k), b), leaf("a12")), //
			seq(s, xor(seq(f, and(h, seq(g, i)), k), seq(b, xor(d, seq(c, e)), j)), leaf("a12")), //

			seq(loop(a, b, tau), loop(c, d, tau), loop(e, f, tau)), //
			xor(loop(a, b, tau), loop(c, d, tau), loop(e, f, tau)), //
			and(loop(a, b, tau), loop(c, d, tau), loop(e, f, tau)), //
			loop(xor(a, b, loop(c, d, tau)), loop(e, f, tau), tau), //

			and(a, b, c, d, e, f, g, h, i, j, k), // test fall through

			loop(a, tau, tau), //
			loop(seq(a, b, c), tau, tau), //
			loop(tau, xor(a, b, c, d, e, f), tau), //flower model

			and(loop(a, b, tau), loop(c, d, tau)), //
			loop(and(a, c), and(b, d), tau), //

	};

	public EfficientTree[] getTrees() {
		return trees;
	}
}