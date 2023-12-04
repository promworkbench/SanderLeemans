package caise2020isextension;

import java.io.File;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.DistributionType;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.ExecutionPolicy;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.TimeUnit;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import gnu.trove.set.hash.THashSet;

/**
 * A flower model in which each transition is equally likely, as well as
 * stopping.
 * 
 * @author sander
 *
 */
public class AlgorithmFlowerNaive implements Algorithm {

	public String getName() {
		return "flower - naive";
	}

	public String getAbbreviation() {
		return "flwn";
	}
	
	public String getLatexName() {
		return "FMN";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {

		double probability;
		Set<String> activities;
		{
			activities = new THashSet<>();
			for (XTrace trace : log) {
				for (XEvent event : trace) {
					String activity = XConceptExtension.instance().extractName(event);
					activities.add(activity);
				}
			}
			probability = 1 / (activities.size() + 1.0);
		}

		StochasticNet net = new StochasticNetImpl(getName());
		net.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
		net.setTimeUnit(TimeUnit.HOURS);
		Place source = net.addPlace("source");
		Marking marking = new Marking();
		marking.add(source);
		TimedTransition start = net.addTimedTransition("tau start", 1, DistributionType.UNIFORM, 0.0, 200.0);
		start.setInvisible(true);
		net.addArc(source, start);
		Place heart = net.addPlace("heart");
		net.addArc(start, heart);

		for (String activity : activities) {
			Transition transition = net.addTimedTransition(activity, probability, DistributionType.UNIFORM, 0.0,
					200.0);
			net.addArc(heart, transition);
			net.addArc(transition, heart);
		}

		TimedTransition stop = net.addTimedTransition("tau stop", probability, DistributionType.UNIFORM, 0.0, 200.0);
		stop.setInvisible(true);
		net.addArc(heart, stop);
		Place sink = net.addPlace("sink");
		net.addArc(stop, sink);

		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking, new FakeGraphLayoutConnection(net));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

}
