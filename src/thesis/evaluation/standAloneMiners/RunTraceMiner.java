package thesis.evaluation.standAloneMiners;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree.NodeType;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2HumanReadableString;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeFactory;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeImpl;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;
import thesis.helperClasses.XLogParserIncremental;

public class RunTraceMiner {
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
			System.out.println("Usage: TraceMiner.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}

		System.out.println(EfficientTree2HumanReadableString.toMachineString(mine(file)));
	}

	public static EfficientTree mine(File file) throws Exception {
		final XEventClassifier classifier = MiningParameters.getDefaultClassifier();

		//keep set of traces; do not duplicate
		final TCustomHashSet<int[]> traces = new TCustomHashSet<>(new HashingStrategy<int[]>() {
			private static final long serialVersionUID = -8931056438775316360L;

			public int computeHashCode(int[] object) {
				return Arrays.hashCode(object);
			}

			public boolean equals(int[] o1, int[] o2) {
				return Arrays.equals(o1, o2);
			}
		});

		//initialise the tree
		final TObjectIntMap<String> activity2int = EfficientTreeImpl.getEmptyActivity2int();
		final ArrayList<String> int2activity = new ArrayList<>();
		final TIntArrayList tree = new TIntArrayList();
		tree.add(NodeType.xor.code);

		//parse the traces
		XLogParserIncremental.parseTraces(file, new Function<XTrace, Object>() {
			public Object call(XTrace input) throws Exception {

				//start the trace
				if (input.isEmpty()) {
					int[] intTree = new int[0];
					if (!traces.contains(intTree)) {
						traces.add(intTree);
						tree.add(NodeType.tau.code);
					}
				} else {
					int i = 0;
					int[] trace = new int[input.size()];
					for (XEvent event : input) {
						String activity = classifier.getClassIdentity(event);
						int actIndex = activity2int.putIfAbsent(activity, int2activity.size());
						if (actIndex == activity2int.getNoEntryValue()) {
							trace[i] = int2activity.size();
							int2activity.add(activity);
						} else {
							tree.add(actIndex);
							trace[i] = actIndex;
						}
						i++;
					}

					if (!traces.contains(trace)) {
						traces.add(trace);
						tree.add(NodeType.sequence.code - input.size() * EfficientTreeImpl.childrenFactor);
						for (int event : trace) {
							tree.add(event);
						}
					}
				}

				tree.set(0, tree.get(0) - EfficientTreeImpl.childrenFactor);

				return null;
			}
		});

		//construct the tree
		String[] int2activity2 = new String[activity2int.size()];
		for (TObjectIntIterator<String> it = activity2int.iterator(); it.hasNext();) {
			it.advance();
			int2activity2[it.value()] = it.key();
		}

		return EfficientTreeFactory.create(tree.toArray(), activity2int, int2activity2);
	}
}
