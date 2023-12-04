package graph;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.shapes.Decorated;

public class GraphNode  extends AbstractDirectedGraphNode implements Decorated {
	
	protected Graph graph;	
	
	protected Color color;
	
	
//	public abstract String getTNodeType();
//	
//	public final static String NODE_EVENT = "NODE_EVENT";
//	public final static String NODE_L = "NODE_LOGMOVE";
//	public final static String NODE_MINV = "NODE_MODELMOVE_INVISIBLE";
//	public final static String NODE_MREAL = "NODE_MODELMOVE_VISIBLE";
//	public final static String NODE_SYNCM = "NODE_SYNCHRONIZE_MOVE";

	public GraphNode(Graph graph, String label) {
		this.graph = graph;
		
		getAttributeMap().put(AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.AUTOSIZE, true);
		getAttributeMap().put(AttributeMap.TOOLTIP, null);
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.MOVEABLE, true);
	}

	
	// TODO XXQ: no documentation?		
	public void decorate(Graphics2D g2d, double x, double y, double width, double height){
		
	}


	public AbstractDirectedGraph<?, ?> getGraph() {
		return graph;
	}
	
	
//	public int hashCode() {
//		return event.hashCode();
//	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(color != null){
			this.color = color;
			getAttributeMap().put(AttributeMap.FILLCOLOR, this.color);
		}
	}

	
	

}
