package batch.miners;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.ilpminer.ILPMiner;
import org.processmining.plugins.ilpminer.ILPMinerSettings;


public class ILP extends Miner {

	public String getIdentification() {
		return "ILP miner";
	}

	public String getIdentificationShort() {
		return "ILP";
	}

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(
			PluginContext context,
			XLog log,
			XEventClassifier classifier,
			XLogInfo logInfo,
			Integer maxMiningTime) throws Exception {
		
		//ILPMiner miner = new ILPMiner();
		//ILPMinerSettings settings = new ILPMinerSettings();
		//Object[] arr = miner.doILPMiningWithSettings(context, log, logInfo, settings);
		
		ComputeILP t = new ComputeILP(context, log, logInfo);
		//Object[] result = new TimeOut().runWithHardTimeOut(t, maxMiningTime);
		Object[] result = t.call();
		
		Petrinet petrinet = (Petrinet) result[0];
		Marking initialMarking = (Marking) result[1];
		
		TransEvClassMapping mapping;
		mapping = getTransEvClassMapping(classifier, logInfo, petrinet);
		
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(petrinet, initialMarking, null, mapping);
	}
	
	private class ComputeILP implements java.util.concurrent.Callable<Object[]> {
        private PluginContext context;
        private XLog log;
        private XLogInfo logInfo;
        
        ComputeILP(PluginContext context, XLog log, XLogInfo logInfo) {
            this.context = context;
            this.log = log;
            this.logInfo = logInfo;
        }

		public Object[] call() throws Exception {
			ILPMiner miner = new ILPMiner();
    		ILPMinerSettings settings = new ILPMinerSettings();
			return miner.doILPMiningWithSettings(context, log, logInfo, settings);
		}
    }
}

