package caise2020;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.processmining.plugins.stochasticpetrinet.miner.StochasticMinerPlugin;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import thesis.helperClasses.FakeContext;

public class AlgorithmAndreas implements Algorithm {

	public String getName() {
		return "Andreas";
	}

	public String getAbbreviation() {
		return "ARS";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		Object[] objects = StochasticMinerPlugin.discoverStochNetModel(new FakeContext(), log);
		StochasticNet net = (StochasticNet) objects[0];
		Marking marking = (Marking) objects[1];

		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking,
				new FakeGraphLayoutConnection(net));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

}
