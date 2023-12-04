package graph;

import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;


public class GraphEdge extends AbstractDirectedGraphEdge<GraphNode, GraphNode> {
	
	private GraphNode source;
	private GraphNode target;
	private String label;
	
	private static Color edgeColour = new Color(169,169,169);
	
	/**
	 * Create a POedge according to the model relation
	 * @param source Predecessor node 
	 * @param target Successor node
	 */
	public GraphEdge(GraphNode source, GraphNode target, String label) {
		// model dependency
		super(source, target);
		this.source = source;
		this.target = target;
		
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.EDGECOLOR, edgeColour);
		getAttributeMap().put(AttributeMap.LINEWIDTH, (float) 1.5);
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
	}

	public GraphNode getTarget() {
		return target;
	}
	
	public GraphNode getSource() {
		return source;
	}
}
