package batch.incompleteness.miners.IMiA;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.graphs.GraphFactory;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMf.CutFinderIMf;

public class CutFinderIMiA extends CutFinderIMf {

	/**
	 * Filter a graph. Only keep the edges that occur more times than threshold.
	 * 
	 * @param graph
	 * @param threshold
	 * @return
	 */
	public static Graph<XEventClass> filterGraph(Graph<XEventClass> graph, MultiSet<XEventClass> endActivities,
			float threshold) {
		//filter directly-follows graph
		Graph<XEventClass> filtered = GraphFactory.create(XEventClass.class, graph.getNumberOfVertices());

		System.out.println("filter IMiA graph");

		//add nodes
		filtered.addVertices(graph.getVertices());

		//add edges
		for (XEventClass activity : graph.getVertices()) {
			//add all edges that are strong enough
			for (long edge : graph.getOutgoingEdgesOf(activity)) {
				if (graph.getEdgeWeight(edge) >= threshold) {
					XEventClass from = graph.getEdgeSource(edge);
					XEventClass to = graph.getEdgeTarget(edge);
					filtered.addEdge(from, to, graph.getEdgeWeight(edge));
				}
			}
		}
		return filtered;
	}

	/**
	 * Filter start or end activities. Only keep those occurring more times than
	 * threshold.
	 * 
	 * @param activities
	 * @param threshold
	 * @return
	 */
	public static MultiSet<XEventClass> filterActivities(MultiSet<XEventClass> activities, float threshold) {
		System.out.println("filter IMiA activities");
		MultiSet<XEventClass> filtered = new MultiSet<XEventClass>();
		for (XEventClass activity : activities) {
			if (activities.getCardinalityOf(activity) >= threshold) {
				filtered.add(activity, activities.getCardinalityOf(activity));
			}
		}
		return filtered;
	}
}
