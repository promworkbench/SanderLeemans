package graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;


/**
 * 
 * @author xlu
 *
 */
public class Graph extends AbstractDirectedGraph<GraphNode, GraphEdge> {

	private Set<GraphNode> nodes;
	private Set<GraphEdge> edges;
	private String label;
	
	
	public Graph(String label) {
		this.label = label;
		initLayout();
		
		
		nodes = new HashSet<GraphNode>();
		edges = new HashSet<GraphEdge>();
	}

	private void initLayout() {
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);		
	}

	public Set<GraphNode> getNodes() {
		return nodes;
	}
	

	public Set<GraphEdge> getEdges() {
		return edges;
	}

	/**
	 * Adds a node to the graph.
	 * 
	 * @param node
	 *            The node to add.
	 */
	public void addNode(GraphNode node) {
		nodes.add(node);
		graphElementAdded(node);
	}

	/**
	 * Adds an edge to this graph.
	 * 
	 * @param edge
	 *            The edge to add.
	 */
	public void addEdge(GraphEdge edge) {
		edges.add(edge);
		graphElementAdded(edge);
	}
	
	public void removeNode(DirectedGraphNode node) {
		if (node instanceof GraphNode){
			removeNodeFromCollection(nodes, (GraphNode) node);
			nodes.remove(node);
		}
		
	}

	protected AbstractDirectedGraph<GraphNode, GraphEdge> getEmptyClone() {
		return new Graph(label);
	}

	protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
			DirectedGraph<GraphNode, GraphEdge> graph) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof GraphEdge) {
			removeNodeFromCollection(edges, (GraphEdge) edge);
			edges.remove(edge);
		}
		
	}

}
