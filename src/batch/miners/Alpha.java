package batch.miners;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.mining.alphaminer.AlphaMiner;

public class Alpha extends Miner {

	public String getIdentification() {
		return "alpha algorithm";
	}

	public String getIdentificationShort() {
		return "a";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(PluginContext context, XLog log,
			XEventClassifier classifier, XLogInfo logInfo, Integer maxMiningTime) throws Exception {
		
		AlphaMiner miner = new AlphaMiner();
		Object[] a = miner.doMining(context, log, logInfo);
		Petrinet petrinet = (Petrinet) a[0];
		Marking initialMarking = (Marking) a[1];
		
		TransEvClassMapping mapping = this.getTransEvClassMapping(classifier, logInfo, petrinet);
		
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(petrinet, initialMarking, null, mapping);
	}

}
