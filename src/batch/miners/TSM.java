package batch.miners;

import java.util.Arrays;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.classification.XEventResourceClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.transitionsystem.miner.TSMiner;
import org.processmining.plugins.transitionsystem.miner.TSMinerInput;
import org.processmining.plugins.transitionsystem.miner.TSMinerOutput;
import org.processmining.plugins.transitionsystem.miner.TSMinerTransitionSystem;
import org.processmining.plugins.transitionsystem.regions.TransitionSystem2Petrinet;

public class TSM extends Miner {

	public String getIdentification() {
		return "transition system miner";
	}

	public String getIdentificationShort() {
		return "tsm";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(PluginContext context, XLog log,
			XEventClassifier classifier, XLogInfo logInfo, Integer maxMiningTime) throws Exception {
		
		//mine transition system
		TSMiner miner = new TSMiner(context);
		
		XEventClassifier[] classifiers = new XEventClassifier[3];
		classifiers[0] = new XEventNameClassifier();
		classifiers[1] = new XEventResourceClassifier();
		classifiers[2] = new XEventLifeTransClassifier();
		
		TSMinerInput input = new TSMinerInput(context, log, Arrays.asList(classifiers), classifier);
		TSMinerOutput result = miner.mine(input);
		TSMinerTransitionSystem transitionSystem = result.getTransitionSystem();
		
		//convert to Petri net
		TransitionSystem2Petrinet converter = new TransitionSystem2Petrinet();
		Object[] a = converter.convertToPetrinet(context, transitionSystem);
		Petrinet petrinet = (Petrinet) a[0];
		Marking initialMarking = (Marking) a[1];
		
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(petrinet, initialMarking, null, null);
	}
	
}
