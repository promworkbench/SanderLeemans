package batch.miners;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

import batch.TimeOut;

public class Trace extends Miner {

	public String getIdentification() {
		return "Trace";
	}

	public String getIdentificationShort() {
		return "TM";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(PluginContext context, XLog log,
			XEventClassifier classifier, XLogInfo logInfo, Integer maxMiningTime) throws Exception {

		ComputeTrace t = new ComputeTrace(log, logInfo, classifier);
		return new TimeOut().runWithHardTimeOut(t, maxMiningTime);
	}

	private class ComputeTrace implements
			java.util.concurrent.Callable<Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>> {
		
		private XLog log;
		private XLogInfo logInfo;
		private XEventClassifier classifier;
		
		public ComputeTrace(
				XLog log,
				XLogInfo logInfo,
				XEventClassifier classifier) {
			this.log = log;
			this.logInfo = logInfo;
			this.classifier = classifier;
		}

		public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> call() throws Exception {
			//make a set of traces
			Set<List<XEventClass>> setLog = new HashSet<List<XEventClass>>();
			XEventClasses eventClasses = logInfo.getEventClasses();
			for (XTrace trace : log) {
				List<XEventClass> newTrace = new LinkedList<XEventClass>();
				for (XEvent event : trace) {
					newTrace.add(eventClasses.getClassOf(event));
				}
				setLog.add(newTrace);
			}
			
			debug("done making a set, construct a Petri net");

			//construct the Petri net
			Petrinet net = new PetrinetImpl("trace");
			Place source = net.addPlace("source");
			Place sink = net.addPlace("sink");

			XEventClass dummy = new XEventClass("", 1);
			TransEvClassMapping mapping = new TransEvClassMapping(classifier, dummy);

			for (List<XEventClass> trace : setLog) {
				Place lastPlace = source;
				Iterator<XEventClass> it = trace.iterator();
				while (it.hasNext()) {
					XEventClass e = it.next();
					Transition t = net.addTransition(e.getId());
					mapping.put(t, e);
					net.addArc(lastPlace, t);

					if (it.hasNext()) {
						lastPlace = net.addPlace("");
						net.addArc(t, lastPlace);
					} else {
						net.addArc(t, sink);
					}
				}
			}

			Marking initialMarking = new Marking();
			initialMarking.add(source);
			Marking finalMarking = new Marking();
			finalMarking.add(sink);

			return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(net, initialMarking, finalMarking,
					mapping);
		}
	}
}
