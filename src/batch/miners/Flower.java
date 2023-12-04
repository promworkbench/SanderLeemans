package batch.miners;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

public class Flower extends Miner {

	public String getIdentification() {
		return "Flower";
	}
	public String getIdentificationShort() {
		return "FM";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(
			PluginContext context,
			XLog log,
			XEventClassifier classifier,
			XLogInfo logInfo,
			Integer maxMiningTime) throws Exception {
		
		Petrinet net = new PetrinetImpl("flower");
		Place source = net.addPlace("source");
		Place sink = net.addPlace("sink");
		Place stigma = net.addPlace("stigma");
		XEventClass dummy = new XEventClass("", 1);
		TransEvClassMapping mapping = new TransEvClassMapping(classifier, dummy);
		
		Transition start = net.addTransition("start");
		start.setInvisible(true);
		net.addArc(source, start);
		net.addArc(start, stigma);
		mapping.put(start, dummy);
		
		Transition end = net.addTransition("end");
		end.setInvisible(true);
		net.addArc(stigma, end);
		net.addArc(end, sink);
		mapping.put(end, dummy);
		
		for (XEventClass activity : logInfo.getEventClasses().getClasses()) {
			Transition t = net.addTransition(activity.toString());
			net.addArc(stigma, t);
			net.addArc(t, stigma);
			mapping.put(t, activity);
		}
		
		Marking initialMarking = new Marking();
		initialMarking.add(source);
		Marking finalMarking = new Marking();
		finalMarking.add(sink);
		
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(net, initialMarking, finalMarking, mapping);
	}

}
