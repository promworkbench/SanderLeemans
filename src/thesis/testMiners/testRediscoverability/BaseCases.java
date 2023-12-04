package thesis.testMiners.testRediscoverability;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import thesis.testMiners.TestRediscoverability;

public class BaseCases extends TestRediscoverability {
	public static final EfficientTree[] trees = new EfficientTree[] { //
			leaf("=== base cases ==="), //
			tau, //
			a, //
			xor(a, tau), //
			loop(a, tau, tau), //
			loop(tau, a, tau), //reduced version of loop(tau, a, tau)
	};

	public EfficientTree[] getTrees() {
		return trees;
	}
}