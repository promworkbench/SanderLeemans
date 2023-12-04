package batch.miners;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

public class IM extends Miner {

	public String getIdentification() {
		return "Inductive Miner-basic";
	}
	
	public String getIdentificationShort() {
		return "IM-basic";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(
			PluginContext context,
			XLog log,
			XEventClassifier classifier,
			XLogInfo logInfo,
			Integer maxMiningTime) throws Exception {
		
		MiningParameters mineParameters = new MiningParametersIM();
		mineParameters.setClassifier(classifier);
		
		Object[] arr = IMPetriNet.minePetriNet(log, mineParameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});
		Petrinet petrinet = (Petrinet) arr[0];
		Marking initialMarking = (Marking) arr[1];
		Marking finalMarking = (Marking) arr[2];
		
		TransEvClassMapping mapping = getTransEvClassMapping(classifier, logInfo, petrinet);
		
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(petrinet, initialMarking, finalMarking, mapping);
	}
}
