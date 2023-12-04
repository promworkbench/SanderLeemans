package thesis.testMiners.testGraphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntGraph;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntGraphImplQuadratic;
import org.processmining.plugins.inductiveminer2.helperclasses.graphs.IntStronglyConnectedComponents;

import gnu.trove.set.TIntSet;

public class testIntGraphs {
	public static final List<IntGraph> graphs;
	static {
		graphs = new ArrayList<>();
		graphs.add(new IntGraphImplQuadratic());
	}

	private static boolean s = true;
	private static boolean stopAtError;

	public static boolean test(boolean stopAtError) throws Exception {
		s = true;
		testIntGraphs.stopAtError = stopAtError;
		for (IntGraph graph : graphs) {

			System.out.println("test int graph class: " + graph.getClass());

			//test addition of nodes and edges
			graph.addNode(10);
			graph.addNode(10);
			graph.addNode(0);
			graph.addNode(20);
			
			graph.addEdge(10, 0, 10);
			graph.addEdge(0, 10, 5);

			check(graph.containsEdge(0, 10), "edge presence");
			check(graph.containsEdge(10, 0), "edge presence");
			check(graph.getEdgeWeight(0, 10) == 5, "edge weight");
			check(graph.getEdgeWeight(10, 0) == 10, "edge weight");
			check(graph.getEdgeWeight(0, 100) == 0, "not-existing edge weight = 0");

			//test removal of edges
			Iterator<Long> it = graph.getEdges().iterator();
			assert (it.hasNext());
			long edge = it.next();
			if (graph.getEdgeSource(edge) == 0) {
				it.remove();
				check(it.hasNext(), "has edges after removal");
				long edge2 = it.next();
				check(graph.getEdgeSource(edge2) == 10, "remaining edge");
				check(graph.getWeightOfHeaviestEdge() == 10, "heaviest edge");
			} else {
				it.remove();
				check(it.hasNext(), "has edges after removal");
				long edge2 = it.next();
				check(graph.getEdgeSource(edge2) == 0, "remaining edge");
				check(graph.getWeightOfHeaviestEdge() == 5, "heaviest edge");
			}

			assert (!graph.containsEdge(0, 1) || !graph.containsEdge(1, 0));

			it.remove();

			//now the graph should be empty
			check(!graph.getEdges().iterator().hasNext(), "graph empty");

			{
				check(true, "=== incoming edges iterator ===");

				//test addition of nodes and edges
				graph.addEdge(10, 0, 10);
				graph.addEdge(0, 10, 5);
				graph.addEdge(20, 0, 7);

				check(graph.containsEdge(0, 10), "edge presence");
				check(graph.containsEdge(10, 0), "edge presence");
				check(graph.getEdgeWeight(0, 10) == 5, "edge weight");
				check(graph.getEdgeWeight(10, 0) == 10, "edge weight");
				check(graph.getEdgeWeight(20, 0) == 7, "edge weight");
				check(graph.getEdgeWeight(0, 100) == 0, "not-existing edge weight = 0");

				it = graph.getIncomingEdgesOf(0).iterator();
				edge = it.next();
				if (graph.getEdgeSource(edge) == 10) {
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeSource(edge2) == 20, "edge after removal");
					check(graph.getEdgeWeight(edge2) == 7, "edge weight after removal");
				} else {
					check(graph.getEdgeSource(edge) == 20, "expected edge source");
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeSource(edge2) == 10, "edge after removal");
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
				graph.addEdge(0, 10, 10);
				graph.addEdge(10, 0, 5);
				graph.addEdge(0, 20, 7);

				check(graph.containsEdge(10, 0), "edge presence");
				check(graph.containsEdge(0, 10), "edge presence");
				check(graph.getEdgeWeight(10, 0) == 5, "edge weight");
				check(graph.getEdgeWeight(0, 10) == 10, "edge weight");
				check(graph.getEdgeWeight(0, 20) == 7, "edge weight");
				check(graph.getEdgeWeight(100, 0) == 0, "not-existing edge weight = 0");

				it = graph.getOutgoingEdgesOf(0).iterator();
				edge = it.next();
				if (graph.getEdgeTarget(edge) == 10) {
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeTarget(edge2) == 20, "edge after removal");
					check(graph.getEdgeWeight(edge2) == 7, "edge weight after removal");
				} else {
					check(graph.getEdgeTarget(edge) == 20, "expected edge source");
					it.remove();
					check(it.hasNext(), "has edges after removal");
					long edge2 = it.next();
					check(graph.getEdgeTarget(edge2) == 10, "edge after removal");
					check(graph.getEdgeWeight(edge2) == 10, "edge weight after removal");
				}

				it.remove();
				check(!it.hasNext(), "no outgoing edges left");

				it = graph.getEdges().iterator();
				it.next();
				it.remove();

				check(!graph.getEdges().iterator().hasNext(), "graph empty");
			}
			
			//strongly connected components
			check(true, "=== strongly connected components ===");
			{
				IntStronglyConnectedComponents.compute(graph);
				check(true, "empty graph");
				
				graph.addEdge(0, 10, 1);
				graph.addEdge(10, 0, 1);
				graph.addEdge(0, 20, 1);
				
				Set<TIntSet> scc = IntStronglyConnectedComponents.compute(graph);
				check(scc.size() == 2, "number of components");
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
