package generation;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.processtree.Block;
import org.processmining.processtree.Edge;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.impl.AbstractBlock;
import org.processmining.processtree.impl.AbstractTask;
import org.processmining.processtree.impl.ProcessTreeImpl;

@Plugin(name = "Generate random process tree", returnLabels = { "Process Tree" }, returnTypes = { ProcessTree.class }, parameterLabels = {}, userAccessible = true)
public class GenerateTree {

	private class Possibility {
		public Node node;
		public Boolean startEndDisjoint = null;
		public Integer numberOfChildren = null;

		public Possibility(Node node) {
			this.node = node;
		}

		public Possibility(Node node, boolean startEndComplete) {
			this.node = node;
			this.startEndDisjoint = startEndComplete;
		}

		public Possibility(Node node, int numberOfChildren) {
			this.node = node;
			this.numberOfChildren = numberOfChildren;
		}

		public Possibility(Node node, boolean startEndComplete, int numberOfChildren) {
			this.node = node;
			this.startEndDisjoint = startEndComplete;
			this.numberOfChildren = numberOfChildren;
		}
	}

	private int currentActivity;
	private ProcessTree tree;
	private Random randomGenerator;

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Generate Random Process Tree, default", requiredParameterLabels = {})
	public ProcessTree generateTree(PluginContext context) {
		return generateTree(new GenerateTreeParameters());
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Generate Random Process Tree, dialog", requiredParameterLabels = {})
	public ProcessTree mineGuiPetrinet(UIPluginContext context) {
		GenerateTreeParameters parameters = new GenerateTreeParameters();
		GenerateTreeDialog dialog = new GenerateTreeDialog(parameters);
		InteractionResult result = context.showWizard("Generate a random process tree", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return generateTree(parameters);
	}

	public ProcessTree generateTree(GenerateTreeParameters parameters) {
		currentActivity = 0;
		randomGenerator = new Random(parameters.getSeed());
		tree = new ProcessTreeImpl();
		tree.setRoot(generateNode(false, parameters.getNumberOfActivities(), parameters));
		return tree;
	}

	public Node generateNode(boolean startEndDisjoint, int numberOfActivities, GenerateTreeParameters parameters) {

		//select the operator/activity
		Node node;
		boolean newStartEndDisjoint = startEndDisjoint;
		if (numberOfActivities == 1) {
			//activity

			node = new AbstractTask.Manual("a_" + currentActivity++);
			node.setProcessTree(tree);
			tree.addNode(node);
			return node;
		}

		int numberOfChildren = randomGenerator
				.nextInt(Math.min(numberOfActivities, parameters.getMaxNumberOfChildren()) - 1) + 2;

		if (startEndDisjoint && numberOfActivities == 2) {
			//forced sequence

			node = new AbstractBlock.Seq("");
			newStartEndDisjoint = false;

		} else {

			List<Possibility> possibilities = new LinkedList<Possibility>();
			//xor
			if (startEndDisjoint && (numberOfActivities < numberOfChildren * 2)) {
				if (numberOfActivities > 4) {
					possibilities.add(new Possibility(new AbstractBlock.Xor(""), true, (int) Math
							.floor(numberOfActivities / 2.0)));
				}
			} else {
				possibilities.add(new Possibility(new AbstractBlock.Xor("")));
			}

			//sequence
			possibilities.add(new Possibility(new AbstractBlock.Seq(""), false));

			//parallel
			if (startEndDisjoint && (numberOfActivities < numberOfChildren * 2)) {
				if (numberOfActivities >= 4) {
					possibilities.add(new Possibility(new AbstractBlock.And(""), true, (int) Math
							.floor(numberOfActivities / 2.0)));
				}
			} else {
				possibilities.add(new Possibility(new AbstractBlock.And("")));
			}

			//loop
			if (!parameters.isStartEndDisjointInLoop() || numberOfActivities >= 3) {
				possibilities.add(new Possibility(new AbstractBlock.XorLoop(""), 2));
			}

			Possibility p = possibilities.get(randomGenerator.nextInt(possibilities.size()));
			node = p.node;
			if (p.numberOfChildren != null) {
				numberOfChildren = p.numberOfChildren;
			}
			if (p.startEndDisjoint != null) {
				newStartEndDisjoint = p.startEndDisjoint;
			}
		}

		int[] child2numActs = new int[numberOfChildren];

		//put one activity in each child
		for (int i = 0; i < numberOfChildren; i++) {
			child2numActs[i] = 1;
		}

		//put at least two children in loop body
		int j = numberOfChildren;
		if (node instanceof AbstractBlock.XorLoop && parameters.isStartEndDisjointInLoop()) {
			child2numActs[0]++;
			j++;
		}
		
		//put at least two children in each parallel/xor branch if start+end disjoint
		if ((node instanceof AbstractBlock.Xor || node instanceof AbstractBlock.And) && startEndDisjoint) {
			for (int i = 0; i < numberOfChildren; i++) {
				child2numActs[i] = 2;
			}
			j = numberOfChildren * 2;
		}

		//distribute the other ones randomly
		for (int i = j; i < numberOfActivities; i++) {
			child2numActs[randomGenerator.nextInt(numberOfChildren)]++;
		}

		node.setProcessTree(tree);
		tree.addNode(node);

		//Non block are leafs which end here
		if (node instanceof AbstractBlock.XorLoop) {
			//body
			connectChildToParent(
					generateNode(parameters.isStartEndDisjointInLoop() || newStartEndDisjoint, child2numActs[0],
							parameters), node);
			//redo
			connectChildToParent(generateNode(false, child2numActs[1], parameters), node);
			//exit = tau
			Node exitChild = new AbstractTask.Automatic("");
			exitChild.setProcessTree(tree);
			tree.addNode(exitChild);
			connectChildToParent(exitChild, node);
		} else {
			for (int i = 0; i < numberOfChildren; i++) {
				Node child = generateNode(newStartEndDisjoint, child2numActs[i], parameters);
				connectChildToParent(child, node);
			}
		}

		return node;
	}

	private void connectChildToParent(Node child, Node parent) {
		Edge edge = ((Block) parent).addChild(child);
		child.addIncomingEdge(edge);
		tree.addEdge(edge);
	}

//	private void debug(String x) {
//		System.out.println(x);
//	}
}
