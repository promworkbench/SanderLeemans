package batch.miners;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParameters;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersPartialOrder;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.IMdPetriNet;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.PetrinetWithMarkings;

public class IMd extends Miner {

	public String getIdentification() {
		return "Inductive Miner-dfg only";
	}

	public String getIdentificationShort() {
		return "IMd";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(PluginContext context, XLog log,
			XEventClassifier classifier, XLogInfo logInfo, Integer maxMiningTime) throws Exception {

		DfgMiningParameters parameters = new DfgMiningParametersPartialOrder();
		Dfg dfg = IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(log, MiningParameters.getDefaultClassifier(),
				MiningParameters.getDefaultLifeCycleClassifier())).getDfg();
		PetrinetWithMarkings petrinet = IMdPetriNet.minePetriNet(dfg, parameters);

		TransEvClassMapping mapping = getTransEvClassMapping(classifier, logInfo, petrinet.petrinet);

		return Quadruple.of(petrinet.petrinet, petrinet.initialMarking, petrinet.finalMarking, mapping);
	}
}
