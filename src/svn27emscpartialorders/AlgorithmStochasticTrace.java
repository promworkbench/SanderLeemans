package svn27emscpartialorders;

import java.io.File;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.algorithms.XLog2StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticTraceIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
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

import caise2020isextension.FakeGraphLayoutConnection;

public class AlgorithmStochasticTrace implements Algorithm {

	public String getName() {
		return "stochastic trace model";
	}

	public String getAbbreviation() {
		return "stoTra";
	}

	public String getLatexName() {
		return "STM";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		Activity2IndexKey activityKey = new Activity2IndexKey();
		StochasticLanguage<TotalOrder> language = XLog2StochasticLanguage.convert(log, new XEventNameClassifier(),
				activityKey, new ProMCanceller() {
					public boolean isCancelled() {
						return false;
					}
				});

		//make the Petri net
		StochasticNet net = new StochasticNetImpl(getName());
		net.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
		net.setTimeUnit(TimeUnit.HOURS);
		Place source = net.addPlace("source");
		Marking marking = new Marking();
		marking.add(source);

		for (StochasticTraceIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
			Place place = source;
			int[] trace = it.next();

			for (int activity : trace) {
				String act = activityKey.toString(activity);
				TimedTransition transition = net.addImmediateTransition(act, it.getProbability());
				net.addArc(place, transition);
				place = net.addPlace("place " + activity);
				net.addArc(transition, place);
			}
		}

		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking, new FakeGraphLayoutConnection(net));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

}
