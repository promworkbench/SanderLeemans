package thesis.testMiners.testGraphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.graphs.GraphImplLinearEdge;
import org.processmining.plugins.InductiveMiner.graphs.GraphImplQuadratic;

public class testGraphs {

	public static final List<Graph<String>> graphs;
	static {
		graphs = new ArrayList<>();
		graphs.add(new GraphImplLinearEdge<String>(String.class));
		graphs.add(new GraphImplQuadratic<String>(String.class));
	}

	private static boolean s = true;
	private static boolean stopAtError;

	public static boolean test(boolean stopAtError) throws Exception {
		s = true;
		testGraphs.stopAtError = stopAtError;
		for (Graph<String> graph : graphs) {

			System.out.println("test graph class: " + graph.getClass());

			//test addition of nodes and edges
			graph.addVertex("aa");
			graph.addEdge("bb", "aa", 10);
			graph.addEdge("aa", "bb", 5);

			check(graph.getNumberOfVertices() == 2, "number of vertices");
			check(graph.containsEdge("aa", "bb"), "edge presence");
			check(graph.containsEdge("bb", "aa"), "edge presence");
			check(graph.getEdgeWeight("aa", "bb") == 5, "edge weight");
			check(graph.getEdgeWeight("bb", "aa") == 10, "edge weight");
			check(graph.getEdgeWeight("aa", "bx") == 0, "not-existing edge weight = 0");

			//test removal of edges
			Iterator<Long> it = graph.getEdges().iterator();
			assert (it.hasNext());
			long edge = it.next();
			if (graph.getEdgeSource(edge).equals("aa")) {
				it.remove();
				check(it.hasNext(), "has edges after removal");
				long edge2 = it.next();
				check(graph.getEdgeSource(edge2).equals("bb"), "remaining edge");
				check(graph.getWeightOfHeaviestEdge() == 10, "heaviest edge");
			} else {
				it.remove();
				check(it.hasNext(), "has edges after removal");
				long edge2 = it.next();
				check(graph.getEdgeSource(edge2).equals("aa"), "remaining edge");
				check(graph.getWeightOfHeaviestEdge() == 5, "heaviest edge");
			}

			assert (!graph.containsEdge("aa", "bb") || !graph.containsEdge("bb", "aa"));

			it.remove();

			//now the graph should be empty
			check(!graph.getEdges().iterator().hasNext(), "graph empty");

			{
				check(true, "=== incoming edges iterator ===");

				//test addition of nodes and edges
				graph.addVertex("aa");
				graph.addEdge("bb", "aa", 10);
				graph.addEdge("aa", "bb", 5);
				graph.addEdge("cc", "aa", 7);

				check(graph.getNumberOfVertices() == 3, "number of vertices");
				check(graph.containsEdge("aa", "bb"), "edge presence");
				check(graph.containsEdge("bb", "aa"), "edge presence");
				check(graph.getEdgeWeight("aa", "bb") == 5, "edge weight");
				check(graph.getEdgeWeight("bb", "aa") == 10, "edge weight");
				check(graph.getEdgeWeight("cc", "aa") == 7, "edge weight");
				check(graph.getEdgeWeight("aa", "bx") == 0, "not-existing edge weight = 0");

				it = graph.getIncomingEdgesOf("aa").iterator();
				edge = it.next();
				if (graph.getEdgeSource(edge).equals("bb")) {
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeSource(edge2).equals("cc"), "edge after removal");
					check(graph.getEdgeWeight(edge2) == 7, "edge weight after removal");
				} else {
					check(graph.getEdgeSource(edge).equals("cc"), "expected edge source");
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeSource(edge2).equals("bb"), "edge after removal");
					check(graph.getEdgeWeight(edge2) == 10, "edge weight after removal");
				}

				it.remove();
				check(!it.hasNext(), "no incoming edges left");

				it = graph.getEdges().iterator();
				it.next();
				it.remove();

				check(!graph.getEdges().iterator().hasNext(), "graph empty");
			}

			//now the graph should be empty
			check(!graph.getEdges().iterator().hasNext(), "graph empty");

			{
				check(true, "=== outgoing edges iterator ===");

				//test addition of nodes and edges
				graph.addVertex("aa");
				graph.addEdge("aa", "bb", 10);
				graph.addEdge("bb", "aa", 5);
				graph.addEdge("aa", "cc", 7);

				check(graph.getNumberOfVertices() == 3, "number of vertices");
				check(graph.containsEdge("bb", "aa"), "edge presence");
				check(graph.containsEdge("aa", "bb"), "edge presence");
				check(graph.getEdgeWeight("bb", "aa") == 5, "edge weight");
				check(graph.getEdgeWeight("aa", "bb") == 10, "edge weight");
				check(graph.getEdgeWeight("aa", "cc") == 7, "edge weight");
				check(graph.getEdgeWeight("bx", "aa") == 0, "not-existing edge weight = 0");

				it = graph.getOutgoingEdgesOf("aa").iterator();
				edge = it.next();
				if (graph.getEdgeTarget(edge).equals("bb")) {
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeTarget(edge2).equals("cc"), "edge after removal");
					check(graph.getEdgeWeight(edge2) == 7, "edge weight after removal");
				} else {
					check(graph.getEdgeTarget(edge).equals("cc"), "expected edge source");
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeTarget(edge2).equals("bb"), "edge after removal");
					check(graph.getEdgeWeight(edge2) == 10, "edge weight after removal");
				}

				it.remove();
				check(!it.hasNext(), "no outgoing edges left");

				it = graph.getEdges().iterator();
				it.next();
				it.remove();

				check(!graph.getEdges().iterator().hasNext(), "graph empty");
			}
		}
		return s;
	}

	public static boolean check(boolean value, String explanation) {
		if (!s && stopAtError) {
			return false;
		}
		if (value) {
			System.out.println(" succesful: " + explanation);
		} else {
			System.out.println(" not succesful: " + explanation);
			s = false;
		}
		return value;
	}
}
