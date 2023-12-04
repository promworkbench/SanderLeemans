package thesis.testMiners.testRediscoverability;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import thesis.testMiners.TestRediscoverability;

public class focus extends TestRediscoverability {
	public static final EfficientTree[] trees = new EfficientTree[] { //
			leaf("=== focus ==="), //
			seq(s, and(seq(f, and(h, seq(g, i)), k), seq(b, xor(d, seq(c, e)), j)), leaf("a12")), //
			seq(s, xor(seq(f, and(h, seq(g, i)), k), seq(b, xor(d, seq(c, e)), j)), leaf("a12")), //
			seq(loop(a, b, tau), loop(c, d, tau), loop(e, f, tau)), //
			seq(xor(tau, a), xor(tau, seq(b, xor(tau, c))), xor(tau, d)), //
			seq(xor(tau, a), xor(tau, seq(xor(tau, b), e, xor(tau, c))), xor(tau, d)), //

			and(a, b, c, d, e, f, g, h, i, j, k), // test fall through

			loop(a, tau, tau), //
			loop(seq(a, b, c), tau, tau), //

	};

	public EfficientTree[] getTrees() {
		return trees;
	}
}