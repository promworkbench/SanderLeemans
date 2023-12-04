package adriano;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.inductiveminer2.withoutlog.MiningParametersWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsdImpl;
import org.processmining.plugins.inductiveminer2.withoutlog.variants.MiningParametersIMWithoutLog;

import au.edu.unimelb.processmining.optimization.SimpleDirectlyFollowGraph;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;

public class IMdViaDfgMatrix {
	public static AcceptingPetriNet DFG2ModelWithIMd(SimpleDirectlyFollowGraph dfg)
			throws UnknownTreeNodeException, ReductionFailedException {

		int dfgStartIndex = 0;
		int dfgEndIndex = dfg.size() - 1;
		Map<String, Integer> dfgActivity2Index = dfg.getSimpleLog().getReverseMap();

		//copy the reverse map to a non-reverse map
		TIntObjectMap<String> dfgIndex2Activity = new TIntObjectHashMap<>(10, 0.5f, -100);
		{
			for (Entry<String, Integer> entry : dfgActivity2Index.entrySet()) {

				String activity = entry.getKey();
				int dfgCode = entry.getValue();

				dfgIndex2Activity.put(dfgCode, activity);
			}

			assert (!dfgIndex2Activity.containsKey(dfgStartIndex));
			assert (!dfgIndex2Activity.containsKey(dfgEndIndex));
		}

		//gather activities and make a IM-dfg map
		String[] activities;
		TObjectIntMap<String> activity2index = new TObjectIntHashMap<>(10, 0.5f, -1);
		{
			Set<String> activitiesL = new THashSet<>();
			for (Entry<String, Integer> entry : dfgActivity2Index.entrySet()) {

				String activity = entry.getKey();

				activitiesL.add(activity);
			}
			activities = new String[activitiesL.size()];
			activities = activitiesL.toArray(activities);

			//make a map index => activity
			for (int index = 0; index < activities.length; index++) {
				activity2index.put(activities[index], index);
			}
		}
		System.out.println("IMd: identified activities: " + Arrays.toString(activities));
		DfgMsdImpl graph = new DfgMsdImpl(activities);

		//start activities
		for (int dfgTarget = 0; dfgTarget < dfg.size(); dfgTarget++) {
			if (dfg.getMatrixDFG().get(dfgStartIndex * dfg.size() + dfgTarget)) {
				String target = dfgIndex2Activity.get(dfgTarget);
				int targetIndex = activity2index.get(target);

				graph.getStartActivities().add(targetIndex);
			}
		}
		System.out.println("IMd: start activities: " + graph.getStartActivities());

		//end activities
		for (int dfgSource = 0; dfgSource < dfg.size(); dfgSource++) {
			if (dfg.getMatrixDFG().get(dfgSource * dfg.size() + dfgEndIndex)) {
				String source = dfgIndex2Activity.get(dfgSource);
				int sourceIndex = activity2index.get(source);

				graph.getEndActivities().add(sourceIndex);
			}
		}

		//edges
		for (int dfgSource = 0; dfgSource < dfg.size(); dfgSource++) {
			for (int dfgTarget = 0; dfgTarget < dfg.size(); dfgTarget++) {
				if (dfgSource != dfgStartIndex && dfgSource != dfgEndIndex && dfgTarget != dfgStartIndex
						&& dfgTarget != dfgEndIndex) {
					if (dfg.getMatrixDFG().get(dfgSource * dfg.size() + dfgTarget)) {
						String source = dfgIndex2Activity.get(dfgSource);
						int sourceIndex = activity2index.get(source);

						String target = dfgIndex2Activity.get(dfgTarget);
						int targetIndex = activity2index.get(target);

						graph.getDirectlyFollowsGraph().addEdge(sourceIndex, targetIndex, 1);
					}
				}
			}
		}

		MiningParametersWithoutLog parameters = new MiningParametersIMWithoutLog();

		return org.processmining.plugins.inductiveminer2.plugins.InductiveMinerWithoutLogPlugin.minePetriNet(graph,
				parameters, new Canceller() {
					public boolean isCancelled() {
						return false;
					}
				});
	}
}
