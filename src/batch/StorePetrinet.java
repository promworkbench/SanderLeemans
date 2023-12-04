package batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;


public class StorePetrinet {
	
	private static String INVISIBLE_LABEL = "!!invisible!!";
	private static String SINK_LABEL = "!!sink!!";
	
	/*
	 * Store a Petri net and its initial marking in file
	 */
	public static void store(Petrinet petrinet, Marking initialMarking, Marking finalMarking, TransEvClassMapping mapping, File file) {
		
		//copy the Petri net TO CHANGE THE LABELS :(
		Petrinet petrinet2 = new PetrinetImpl(petrinet.getLabel());
		HashMap<PetrinetNode, PetrinetNode> mapNode2Node = new HashMap<PetrinetNode, PetrinetNode>();
		//copy transitions
		for (Transition t : petrinet.getTransitions()) {
			String label;
			if (t.isInvisible()) {
				label = INVISIBLE_LABEL;
			} else {
				label = mapping.get(t).toString();
			}
			Transition t2 = petrinet2.addTransition(label);
			mapNode2Node.put(t, t2);
		}
		//copy places
		for (Place p : petrinet.getPlaces()) {
			String label;
			if (finalMarking != null && finalMarking.contains(p)) {
				label = SINK_LABEL;
			} else {
				label = "";
			}
			Place p2 = petrinet2.addPlace(label);
			mapNode2Node.put(p, p2);
			
			//copy all arcs from/to this place
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : petrinet.getInEdges(p)) {
				petrinet2.addArc((Transition) mapNode2Node.get(e.getSource()), p2);
			}
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : petrinet.getOutEdges(p)) {
				petrinet2.addArc(p2, (Transition) mapNode2Node.get(e.getTarget()));
			}
		}
		//copy initial marking
		Marking initialMarking2 = new Marking();
		for (Place p : initialMarking) {
			initialMarking2.add((Place) mapNode2Node.get(p));
		}
		
		//make a layout
		/*
		ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();
		ProMGraphModel model = new ProMGraphModel(petrinet2);
		ProMJGraph jGraph = null;
		*/
		GraphLayoutConnection layoutConnection = new GraphLayoutConnection(petrinet2);
		/*jGraph = new ProMJGraph(model, map, layoutConnection);
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);
		layout.setOrientation(map.get(petrinet2, AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));
		JGraphFacade facade = new JGraphFacade(jGraph);
		facade.setOrdered(false);
		facade.setEdgePromotion(true);
		facade.setIgnoresCellsInGroups(false);
		facade.setIgnoresHiddenCells(false);
		facade.setIgnoresUnconnectedCells(false);
		facade.setDirected(true);
		facade.resetControlPoints();
		facade.run(layout, true);
		java.util.Map<?, ?> nested = facade.createNestedMap(true, true);
		jGraph.getGraphLayoutCache().edit(nested);
		jGraph.setUpdateLayout(layout);*/
		Pnml pnml = new Pnml().convertFromNet(petrinet2, initialMarking2, layoutConnection);
		pnml.setType(Pnml.PnmlType.PNML);
		String text = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + pnml.exportElement(pnml);
		stringToFile(text, file);
	}
	
	/*
	 * Retrieve the stored Petri net with initial and final marking
	 */
	public static Triple<Petrinet, Marking, Marking> loadStoredPetrinet(PluginContext context, File file) throws Exception {
		//load the petrinet and initial marking
		PnmlImportUtils utils = new PnmlImportUtils();
		InputStream input = new FileInputStream(file);
		Pnml pnml = utils.importPnmlFromStream(context, input, "", 0);
		
		Petrinet petrinet = new PetrinetImpl("");
		Marking initialMarking = new Marking();
		GraphLayoutConnection layout = new GraphLayoutConnection(petrinet);
		pnml.convertToNet(petrinet, initialMarking, layout);
		
		//set invisibility of transitions
		for (Transition t : petrinet.getTransitions()) {
			if (t.getLabel().equals(INVISIBLE_LABEL)) {
				t.setInvisible(true);
			}
		}
		
		//reconstruct final marking
		Marking finalMarking = new Marking();
		for (Place p : petrinet.getPlaces()) {
			if (p.getLabel().equals(SINK_LABEL)) {
				finalMarking.add(p);
			}
		}
		
		return new Triple<Petrinet, Marking, Marking>(petrinet, initialMarking, finalMarking);
	}
	
	public static void stringToFile(String string, File file) {
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);
			out.write(string);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void storeReplayResult(
			PluginContext context,
			PNRepResult replayResult,
			IPNReplayParameter parameters,
			Petrinet petrinet,
			Marking initialMarking,
			XLog log,
			TransEvClassMapping mapping,
			XEventClasses eventClasses,
			File file) throws IOException {
		/*
		//debug(replayResult.toString());
		//ReplayLogCreator logCreator = new ReplayLogCreator();
		NaiveLogCreator logCreator = new NaiveLogCreator();
		LogCreatorParam parameters = new LogCreatorParam(mapping);
		XLog newLog = logCreator.extractLog(context, replayResult, log, parameters);
		
		ExportLogXes exporter = new ExportLogXes();
		exporter.export(null, newLog, file);
		*/
		
		//ExportPNRepResultDetail exporter = new ExportPNRepResultDetail();
		//exporter.exportRepResult2File(context, replayResult, file);
		//StoreReplayClass exporter = new StoreReplayClass();
		//exporter.exportRepResult2File(context, replayResult, file, petrinet, log, mapping, initialMarking, parameters, eventClasses);
	}
	
	private static void debug(String s) {
		System.out.println(s);
	}
}
