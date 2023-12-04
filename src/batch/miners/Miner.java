package batch.miners;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.heuristicsnet.miner.heuristics.converter.HeuristicsNetToPetriNetConverter;

public abstract class Miner {
	
	public Object lastResult;
	public abstract String getIdentification();
	public abstract String getIdentificationShort();
	
	public abstract Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(
			PluginContext context, 
			XLog log, 
			XEventClassifier classifier, 
			XLogInfo logInfo,
			Integer maxMiningTime) throws Exception;
	
	protected Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> convertHeuristicsnet2Petrinet(
			PluginContext context,
			HeuristicsNet net,
			XEventClassifier classifier,
			XLogInfo logInfo) throws unmappableTransitionException {
		Object[] arr = HeuristicsNetToPetriNetConverter.converter(context, net);
		Petrinet petrinet = (Petrinet) arr[0];
		Marking initialMarking = (Marking) arr[1];
		
		TransEvClassMapping mapping;
		mapping = getTransEvClassMapping(classifier, logInfo, petrinet);
		
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(petrinet, initialMarking, null, mapping);
	}
	
	public TransEvClassMapping getTransEvClassMapping(XEventClassifier classifier, XLogInfo logInfo, Petrinet petrinet) throws unmappableTransitionException {
		//create mapping
		//debug("Map transitions to XEventClasses");
		XEventClass dummy = new XEventClass("", 1);
		TransEvClassMapping mapping = new TransEvClassMapping(classifier, dummy);
		XEventClasses activities = logInfo.getEventClasses();
		
		for (Transition t : petrinet.getTransitions()) {
			if (t.isInvisible()) {
				mapping.put(t, dummy);
			} else {
				mapping.put(t, mapTransitionToEventClass(t, activities));
			}
		}
		return mapping;
	}
	
	public class unmappableTransitionException extends Exception {
		private static final long serialVersionUID = 7492151078447630466L;
		
		public unmappableTransitionException(Transition t) {
			super("no XEventClass found for transition " + t);
		}
	}
	
	protected XEventClass mapTransitionToEventClass(Transition t, XEventClasses activities) throws unmappableTransitionException {
		//find the event class with the same label as the transition
		for (XEventClass activity : activities.getClasses()) {
			if (t.getLabel().equals(activity.toString()) || activity.toString().equals(t.getLabel() + "+complete")) {
				return activity;
			}
		}
		debug("no XEventClass found for transition " + t.getLabel() + " in " + activities.getClasses());
		throw new unmappableTransitionException(t);
	}
	
	protected static void debug(String x) {
		System.out.println(x);
	}
}
