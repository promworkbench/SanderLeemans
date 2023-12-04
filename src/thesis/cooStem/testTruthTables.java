package thesis.cooStem;

import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.and;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.inte;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.leaf;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.loop;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.or;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.seq;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.tau;
import static org.processmining.plugins.InductiveMiner.efficienttree.InlineTree.xor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeHash;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;

public class testTruthTables {

	//abbreviations
	private static EfficientTree a = leaf("a");
	private static EfficientTree b = leaf("b");
	private static EfficientTree c = leaf("c");
	private static EfficientTree tau = tau();

	public static void main(String... args) throws Exception {
		System.out.println(" generating trees...");
		List<EfficientTree> trees = GenerateCooTrees
				.generate(Arrays.asList(new String[] { "a", "b", "c", "d", "e", "f", "g" }));
		//		List<EfficientTree> trees = Arrays.asList(new EfficientTree[] { and(a, b), xor(tau, or(a, b)), and(xor(tau, a), xor(tau, b)) });
		System.out.println("  " + trees.size() + " trees to check");

		//verify uniqueness of trees
		System.out.println(" verifying uniqueness of trees...");
		Set<String> treeStrings = new THashSet<>();
		for (EfficientTree tree : trees) {
			treeStrings.add(tree.toString());
		}
		assert (trees.size() == treeStrings.size());
		treeStrings.clear();
		treeStrings = null;

		//sort all trees
		System.out.println(" sorting and hashing trees...");
		List<EfficientTree> sortedTrees = new ArrayList<>();
		List<String> hashes = new ArrayList<>();
		for (EfficientTree tree : trees) {
			EfficientTree sortedTree = EfficientTreeSortActivities.sort(tree);
			sortedTrees.add(sortedTree);

			EfficientTree tree1 = tree.clone();
			EfficientTreeReduce.reduce(tree1);
			hashes.add(EfficientTreeHash.hash(tree1));
		}

		//store the truth tables for all trees
		System.out.println(" computing truth tables...");
		List<TruthTable> truthTables = new ArrayList<>();
		for (EfficientTree sortedTree : sortedTrees) {
			TruthTable t = EfficientTree2TruthTable.convert2(sortedTree);
			truthTables.add(t);
		}

		//compare the truth tables using a set
		System.out.println(" comparing truth tables...");
		TObjectIntMap<TruthTable> in = new TObjectIntHashMap<>(trees.size(), 0.5f, -1);
		for (int i = 0; i < trees.size(); i++) {
			int alreadyPresent = in.putIfAbsent(truthTables.get(i), i);
			if (alreadyPresent != in.getNoEntryValue()) {
				//the truth table was already present
				if (!hashes.get(i).equals(hashes.get(alreadyPresent))) {
					System.out.println("  duplicate truthtable found, and trees do not reduce to the same normal form");
					System.out.println("   " + trees.get(i));
					System.out.println("   " + trees.get(alreadyPresent));
					System.out.println("   reduce to");
					System.out.println("    " + hashes.get(i));
					System.out.println("    " + hashes.get(alreadyPresent));
				}
			}
		}

		System.out.println("done");
	}

	@SuppressWarnings("unused")
	private void neverCalledButKeepsImports() {
		xor(seq(and(loop(inte(or(a, a), a), a, a), a), a), leaf(""));
	}
}
