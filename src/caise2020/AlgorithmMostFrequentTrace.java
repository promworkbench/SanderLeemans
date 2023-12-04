package caise2020;

import java.io.File;
import java.util.Arrays;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.DistributionType;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.ExecutionPolicy;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.TimeUnit;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

public class AlgorithmMostFrequentTrace implements Algorithm {

	public String getName() {
		return "most frequent trace";
	}

	public String getAbbreviation() {
		return "mft";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {

		//extract the most frequent trace
		TObjectIntMap<String[]> map = new TObjectIntCustomHashMap<>(new HashingStrategy<String[]>() {

			public int computeHashCode(String[] object) {
				return Arrays.hashCode(object);
			}

			public boolean equals(String[] o1, String[] o2) {
				return Arrays.equals(o1, o2);
			}
		}, 10, 0.5f, 0);

		for (XTrace trace : log) {
			String[] t = new String[trace.size()];
			for (int i = 0; i < trace.size(); i++) {
				t[i] = XConceptExtension.instance().extractName(trace.get(i));
			}
			map.adjustOrPutValue(t, 1, 1);
		}

		TObjectIntIterator<String[]> it = map.iterator();
		int max = 0;
		String[] trace = null;
		while (it.hasNext()) {
			it.advance();
			if (it.value() > max) {
				max = it.value();
				trace = it.key();
			}
		}

		//make the Petri net
		StochasticNet net = new StochasticNetImpl(getName());
		net.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
		net.setTimeUnit(TimeUnit.HOURS);
		Place place = net.addPlace("source");
		Marking marking = new Marking();
		marking.add(place);
		for (String activity : trace) {
			TimedTransition transition = net.addTimedTransition(activity, 1, DistributionType.UNIFORM, 0.0, 200.0);
			net.addArc(place, transition);
			place = net.addPlace("place " + activity);
			net.addArc(transition, place);
		}

		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking, new FakeGraphLayoutConnection(net));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

}
