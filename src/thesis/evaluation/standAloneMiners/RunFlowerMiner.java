package thesis.evaluation.standAloneMiners;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree.NodeType;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2HumanReadableString;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeFactory;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeImpl;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.set.hash.THashSet;
import thesis.helperClasses.XLogParserIncremental;

public class RunFlowerMiner {

	public static void main(String[] args) throws Exception {
		boolean help = false;
		File file = null;
		if (args.length != 1) {
			help = true;
		} else {
			file = new File(args[0]);
			help = help || !file.exists();
		}

		if (help) {
			System.out.println("Usage: FlowerMiner.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}

		System.out.println(EfficientTree2HumanReadableString.toMachineString(mine(file)));
	}

	public static EfficientTree mine(File file) throws Exception {

		final XEventClassifier classifier = MiningParameters.getDefaultClassifier();

		//gather the activities
		final THashSet<String> vertices = new THashSet<>();
		XLogParserIncremental.parseEvents(file, new Function<XEvent, Object>() {
			public Object call(XEvent event) throws Exception {
				vertices.add(classifier.getClassIdentity(event));
				return null;
			}
		}, null);

		//construct activity structures
		String[] int2activity = new String[vertices.size()];
		TObjectIntMap<String> activity2int = EfficientTreeImpl.getEmptyActivity2int();
		{
			int i = 0;
			for (String vertex : vertices) {
				int2activity[i] = vertex;
				activity2int.put(vertex, i);
				i++;
			}
		}

		//construct the tree
		int[] tree = new int[int2activity.length + 4];
		tree[0] = NodeType.loop.code - 3 * EfficientTreeImpl.childrenFactor;
		assert (tree[0] < 0);
		tree[1] = NodeType.tau.code;
		tree[2] = NodeType.xor.code - int2activity.length * EfficientTreeImpl.childrenFactor;
		assert (tree[1] < 0);
		for (int i = 0; i < int2activity.length; i++) {
			tree[3 + i] = i;
		}
		tree[3 + int2activity.length] = NodeType.tau.code;
		return EfficientTreeFactory.create(tree, activity2int, int2activity);
	}

}
