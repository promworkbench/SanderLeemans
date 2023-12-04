package batch.miners;

import java.util.concurrent.Callable;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import batch.TimeOut;

public class Heuristics extends Miner {

	public String getIdentification() {
		return "HeuristicsMiner 2 heuristics net";
	}
	public String getIdentificationShort() {
		return "HM";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(PluginContext context, XLog log,
			XEventClassifier classifier, XLogInfo logInfo, Integer maxMiningTime) throws Exception {

		ComputeHeuristics t = new ComputeHeuristics(context, log, logInfo, classifier);
		return new TimeOut().runWithHardTimeOut(t, maxMiningTime);
	}
	
	private class ComputeHeuristics implements Callable<Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>> {
		
		private PluginContext context;
		private XLog log;
		private XLogInfo logInfo;
		private XEventClassifier classifier;
		
		public ComputeHeuristics(
			PluginContext context,
			XLog log,
			XLogInfo logInfo,
			XEventClassifier classifier) {
			this.context = context;
			this.log = log;
			this.logInfo = logInfo;
			this.classifier = classifier;
		}

		public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> call() throws Exception {
			HeuristicsMinerSettings settings = new HeuristicsMinerSettings();
			HeuristicsMiner miner = new HeuristicsMiner(context, log, logInfo, settings);
			HeuristicsNet net = miner.mine();
			
			return convertHeuristicsnet2Petrinet(context, net, classifier, logInfo);
			//return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(null, null, null, null);
		}
	}
}
