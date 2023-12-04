package GraphvizVisualisation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.graphviz.colourMaps.ColourMaps;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;

public class IMLogInfo2Dot {
	public static Dot toDot(IMLogInfo logInfo, Collection<Set<XEventClass>> cut) {

		final Graph<XEventClass> graph = logInfo.getDfg().getDirectlyFollowsGraph();
		final Dfg dfg = logInfo.getDfg();

		Dot dot = new Dot();

		//prepare the nodes
		HashMap<XEventClass, DotNode> activityToNode = new HashMap<XEventClass, DotNode>();
		for (Set<XEventClass> branch : cut) {
			//dot += "subgraph \"cluster_" + UUID.randomUUID().toString() + "\" {\n";
			for (XEventClass activity : branch) {
				DotNode node = dot.addNode(activity.toString());
				activityToNode.put(activity, node);

				node.setOption("shape", "box");

				//determine node colour using start and end activities
				if (dfg.isStartActivity(activity) && dfg.isEndActivity(activity)) {
					node.setOption("style", "filled");
					node.setOption(
							"fillcolor",
							ColourMaps.colourMapGreen(dfg.getStartActivityCardinality(activity),
									dfg.getMostOccurringStartActivityCardinality())
									+ ":"
									+ ColourMaps.colourMapRed(dfg.getEndActivityCardinality(activity),
											dfg.getMostOccurringEndActivityCardinality()));
				} else if (dfg.isStartActivity(activity)) {
					node.setOption("style", "filled");
					node.setOption(
							"fillcolor",
							ColourMaps.colourMapGreen(dfg.getStartActivityCardinality(activity),
									dfg.getMostOccurringStartActivityCardinality())
									+ ":white");
				} else if (dfg.isEndActivity(activity)) {
					node.setOption("style", "filled");
					node.setOption(
							"fillcolor",
							"white:"
									+ ColourMaps.colourMapRed(dfg.getEndActivityCardinality(activity),
											dfg.getMostOccurringEndActivityCardinality()));
				}
			}
		}

		//add the edges
		for (long edge : graph.getEdges()) {
			XEventClass from = graph.getEdgeSource(edge);
			XEventClass to = graph.getEdgeTarget(edge);
			long weight = graph.getEdgeWeight(edge);

			DotNode source = activityToNode.get(from);
			DotNode target = activityToNode.get(to);
			String label = String.valueOf(weight);

			dot.addEdge(source, target, label).setOption("color",
					ColourMaps.colourMapBlue(weight, logInfo.getDfg().getMostOccuringDirectlyFollowsEdgeCardinality()) + "");
		}

		return dot;
	}

	public static Dot toDot(IMLogInfo logInfo, boolean useEventuallyFollows) {
		Set<Set<XEventClass>> cut = new HashSet<Set<XEventClass>>();
		cut.add(logInfo.getActivities().toSet());
		return toDot(logInfo, cut);
	}
}
