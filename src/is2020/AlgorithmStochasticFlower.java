package is2020;

import java.io.File;

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

import caise2020.FakeGraphLayoutConnection;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * A flower model in which each transition is equally likely, as well as
 * stopping.
 * 
 * @author sander
 *
 */
public class AlgorithmStochasticFlower implements Algorithm {

	public String getName() {
		return "flower - naive";
	}

	public String getAbbreviation() {
		return "ARS";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {

		TObjectIntMap<String> activity2occurrences = new TObjectIntHashMap<>(10, 0.5f, 0);
		int sum = 0;
		{
			for (XTrace trace : log) {
				for (XEvent event : trace) {
					String activity = XConceptExtension.instance().extractName(event);
					activity2occurrences.adjustOrPutValue(activity, 1, 1);
					sum++;
				}
				sum++;
			}
		}

		//limit to top 10
		for (TObjectIntIterator<String> it = activity2occurrences.iterator(); it.hasNext();) {
			
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

		for (TObjectIntIterator<String> it = activity2occurrences.iterator(); it.hasNext();) {
			it.advance();
			String activity = it.key();
			int occurrences = it.value();
			Transition transition = net.addTimedTransition(activity, occurrences / (sum * 1.0),
					DistributionType.UNIFORM, 0.0, 200.0);
			net.addArc(heart, transition);
			net.addArc(transition, heart);
		}

		TimedTransition stop = net.addTimedTransition("tau stop", log.size() / (sum * 1.0), DistributionType.UNIFORM,
				0.0, 200.0);
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
