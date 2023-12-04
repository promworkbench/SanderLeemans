package thesis.testMiners.testRediscoverability;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import thesis.testMiners.TestRediscoverability;

public class IM extends TestRediscoverability {
	public static final EfficientTree[] trees = new EfficientTree[] { //
			leaf("=== trees for IM ==="), //
			xor(a, b), //
			xor(tau, a, b), //
			xor(a, loop(b, tau, tau)), //
			xor(a, tau, loop(b, tau, tau)), //
			xor(tau, and(a, b)), //
			xor(tau, and(xor(tau, a), b)), //
			xor(loop(a, tau, tau), loop(b, tau, tau)), //

			seq(a, b), //
			seq(a, xor(b, c)), //
			seq(a, loop(b, tau, tau)), //
			seq(a, loop(tau, b, tau)), //
			seq(a, xor(tau, b)), //
			seq(xor(tau, a), b), //
			seq(xor(tau, a), xor(tau, b)), //
			seq(loop(a, tau, tau), loop(b, tau, tau)), //
			seq(a, xor(tau, b), xor(tau, c)), //
			seq(a, xor(tau, seq(b, c))), //
			seq(a, xor(tau, seq(xor(b, c, d), e)), f), //

			and(a, b), //
			and(a, xor(tau, b)), //
			and(xor(tau, a), b), //
			and(a, loop(b, tau, tau)), //
			and(a, loop(tau, b, tau)), //
			and(loop(a, tau, tau), loop(b, tau, tau)), //

			loop(a, b, tau), //
			loop(a, xor(b, tau), tau), //
			loop(a, loop(b, tau, tau), tau), //
			seq(xor(tau, a), xor(tau, seq(b, xor(tau, c))), xor(tau, d)), //

			seq(s, and(seq(f, and(h, seq(g, i)), k), seq(b, xor(d, seq(c, e)), j)), leaf("a12")), //
			seq(s, xor(seq(f, and(h, seq(g, i)), k), seq(b, xor(d, seq(c, e)), j)), leaf("a12")), //
			seq(loop(a, b, tau), loop(c, d, tau), loop(e, f, tau)), //
			seq(xor(tau, a), xor(tau, seq(b, xor(tau, c))), xor(tau, d)), //
			seq(xor(tau, a), xor(tau, seq(xor(tau, b), e, xor(tau, c))), xor(tau, d)), //

			and(a, b, c, d, e, f, g, h, i, j, k), // test fall through

			loop(a, tau, tau), //
			loop(seq(a, b, c), tau, tau), //
			loop(tau, xor(a, b, c, d, e, f, g, h, i, j, k), tau), //flower model

			leaf("=== optional sequence under sequence trees ==="), //			
			seq(xor(tau, seq(a, xor(tau, b))), c), //
			seq(xor(tau, a), xor(tau, b), c), //
			seq(xor(tau, a), xor(tau, b), xor(tau, c)), //
			seq(xor(tau, a), xor(tau, seq(b, c))), //
			seq(xor(tau, seq(a, xor(tau, b))), c, d), //
			seq(xor(tau, a), xor(tau, seq(xor(tau, b), xor(tau, c), d, xor(tau, e), xor(tau, f))), xor(tau, g)), //
			seq(xor(tau, seq(a, xor(tau, seq(b, c)))), xor(tau, d)), //
			seq(xor(tau, seq(a, xor(tau, seq(b, c)))), d), //
			seq(xor(tau, seq(a, b)), xor(tau, seq(c, d))), //
			seq(xor(tau, seq(a, b)), xor(tau, seq(c, d)), xor(tau, seq(e, f))), //
			xor(tau, seq(xor(tau, seq(a, xor(tau, b))), c)), //
			seq(xor(tau, a), xor(tau, seq(b, xor(tau, c), xor(tau, d), e))), //
	};

	public EfficientTree[] getTrees() {
		return trees;
	}
}