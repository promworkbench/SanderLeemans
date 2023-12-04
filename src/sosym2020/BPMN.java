package sosym2020;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import gnu.trove.map.hash.THashMap;

public class BPMN {

	@Plugin(name = "Graphviz BPMN diagram visualisation", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "BPMN diagram" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Display BPMN diagram", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, BPMNDiagram diagram) {
		return new DotPanel(visualiseBPMN(diagram));
	}

	public static Dot visualiseBPMN(BPMNDiagram diagram) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		dot.setOption("splines", "polyline");

		Map<BPMNNode, DotNode> bpmnNode2dotNode = new THashMap<>();

		for (Activity node : diagram.getActivities()) {
			DotNode dotNode = dot.addNode(node.getLabel());
			dotNode.setOption("shape", "box");
			dotNode.setOption("fontsize", "12");
			bpmnNode2dotNode.put(node, dotNode);
		}
		for (Gateway gateway : diagram.getGateways()) {
			DotNode node = dot.addNode("g");
			node.setOption("shape", "diamond");
			node.setOption("fixedsize", "true");
			node.setOption("height", "0.25");
			node.setOption("width", "0.27");
			switch (gateway.getGatewayType()) {
				case COMPLEX :
					node.setLabel("*");
					break;
				case DATABASED :
					node.setLabel("x");
					break;
				case EVENTBASED :
					node.setLabel("e");
					break;
				case INCLUSIVE :
					node.setLabel("o");
					break;
				case PARALLEL :
					node.setLabel("+");
					break;
				default :
					break;
			}
			bpmnNode2dotNode.put(gateway, node);
		}
		for (Event event : diagram.getEvents()) {
			DotNode node = dot.addNode("");
			node.setOption("shape", "circle");
			node.setOption("fixedsize", "true");
			node.setOption("height", "0.25");
			node.setOption("width", "0.27");
			switch (event.getEventType()) {
				case END :
					node.setOption("penwidth", "2");
					break;
				case INTERMEDIATE :
					break;
				case START :
					break;
				default :
					break;
			}
			bpmnNode2dotNode.put(event, node);
		}
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getEdges()) {
			dot.addEdge(bpmnNode2dotNode.get(edge.getSource()), bpmnNode2dotNode.get(edge.getTarget()));
		}
		return dot;
	}

	public static BPMNDiagram getDiagram(Bpmn bpmn) throws Exception {
		BPMNDiagram newDiagram = BPMNDiagramFactory.newBPMNDiagram("");
		Map<String, BPMNNode> id2node = new HashMap<String, BPMNNode>();
		Map<String, Swimlane> id2lane = new HashMap<String, Swimlane>();
		Collection<String> elements = bpmn.getDiagrams().iterator().next().getElements();
		bpmn.unmarshall(newDiagram, elements, id2node, id2lane);
		return newDiagram;
	}

	public static Bpmn importFile(File file) throws Exception {
		InputStream input = new BufferedInputStream(new FileInputStream(file));

		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh PNML object.
		 */
		Bpmn bpmn = new Bpmn();

		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to PNML start tag.
		 */
		if (xpp.getName().equals(bpmn.tag)) {
			/*
			 * Yes it does. Import the PNML element.
			 */
			bpmn.importElement(xpp, bpmn);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			throw new Exception("Expected " + bpmn.tag + ", got " + xpp.getName());
		}
		if (bpmn.hasErrors()) {
			throw new Exception("Invalid BPMN file.");
		}
		return bpmn;
	}
}
