package batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.alignetc.AlignETCPlugin;
import org.processmining.plugins.alignetc.AlignETCSettings;
import org.processmining.plugins.alignetc.result.AlignETCResult;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.astar.petrinet.PrefixBasedPetrinetReplayer;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGen;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGenRes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import batch.miners.Miner;

public class CompareMinersReplay implements Callable<Boolean> {
	
	private PluginContext context;
	private File file;
	private Miner miner;
	private XLog log;
	private XLogInfo logInfo;
	private Petrinet petrinet;
	private Marking initialMarking;
	private Marking finalMarking;
	private TransEvClassMapping mapping;
	private CompareMinersResult result;
	private boolean useFinalMarking;
	private File fileReplayedLog;
	private XEventClassifier classifier;
	
	public CompareMinersReplay(PluginContext context,
			File file,
			Miner miner,
			XLog log,
			XLogInfo logInfo, 
			Petrinet petrinet,
			Marking initialMarking,
			Marking finalMarking,
			TransEvClassMapping mapping,
			CompareMinersResult result,
			boolean useFinalMarking,
			File fileReplayedLog,
			XEventClassifier classifier) {
		this.context = context;
		this.file = file;
		this.miner = miner;
		this.log = log;
		this.logInfo = logInfo;
		this.petrinet = petrinet;
		this.initialMarking = initialMarking;
		this.finalMarking = finalMarking;
		this.mapping = mapping;
		this.result = result;
		this.useFinalMarking = useFinalMarking;
		this.fileReplayedLog = fileReplayedLog;
		this.classifier = classifier;
	}
	
	public Boolean call() throws Exception {
		
		double costOfEpsilon = 0;
		if (!useFinalMarking) {
			costOfEpsilon = findReplayCostEpsilon.find(context, file, petrinet, initialMarking, finalMarking, mapping, classifier, logInfo);
		}
		
		debug("Start replay " + file);
		PNLogReplayer replayer = new PNLogReplayer();
		Collection<XEventClass> activities = logInfo.getEventClasses().getClasses();
		XEventClass dummy = mapping.getDummyEventClass();
		CostBasedCompleteParam replayParameters = new CostBasedCompleteParam(activities, dummy, petrinet.getTransitions(), 1, 1);
		replayParameters.setInitialMarking(initialMarking);
		replayParameters.setMaxNumOfStates(Integer.MAX_VALUE);
		IPNReplayAlgorithm algorithm;
		if (!useFinalMarking) {
			algorithm = new PrefixBasedPetrinetReplayer();
		} else {
			algorithm = new PetrinetReplayerWithILP();
			replayParameters.setFinalMarkings(new Marking[] {finalMarking});
		}		
		replayParameters.setCreateConn(false);
		replayParameters.setGUIMode(false);
		PNRepResult replayed = null;
		try {
			replayed = replayer.replayLog(context, petrinet, log, mapping, algorithm, replayParameters);
		} catch (Error e) {
			e.printStackTrace();
			result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;Log replay failed.<br>");
			return null;
		}
		
		//compute fitness
		double numberOfTraces = 0;
		double summedTraceFitness = 0;
		for (SyncReplayResult result : replayed) {
			double traceCost = result.getInfo().get(PNRepResult.RAWFITNESSCOST);
			double traceLength = result.getInfo().get(PNRepResult.ORIGTRACELENGTH);
			int multiplicity = result.getTraceIndex().size();
			
			double traceFitness = (1 - (traceCost / (traceLength + costOfEpsilon)));
			summedTraceFitness = summedTraceFitness + (traceFitness * multiplicity);
			numberOfTraces = numberOfTraces + result.getTraceIndex().size();
		}
		
		//debug("number of traces in log " + log.size());
		//debug("number of replayed traces " + numberOfTraces);
		
		double fitness;
		if (useFinalMarking) {
			fitness = (Double) replayed.getInfo().get(PNRepResult.TRACEFITNESS);
		} else {
			fitness = (summedTraceFitness / numberOfTraces);
		}
		debug("self-computed trace fitness over log " + fitness + " with cost epsilon of " + costOfEpsilon);
		result.reportFitness(file, miner, useFinalMarking, fitness);
		debug("replay finished");
		
		//store replay result
		//StorePetrinet.storeReplayResult(context, replayed, replayParameters, petrinet, initialMarking, log, mapping, logInfo.getEventClasses(), fileReplayedLog);
		
		//measure precision
		AlignETCPlugin precisionMeasurer = new AlignETCPlugin();
		AlignETCResult precisionResult = new AlignETCResult();
		AlignETCSettings precisionSettings = new AlignETCSettings(precisionResult);
		//Convert to n-alignments object
		Collection<AllSyncReplayResult> col = new ArrayList<AllSyncReplayResult>();
		for (SyncReplayResult rep : replayed) {

			//Get all the attributes of the 1-alignment result
			List<List<Object>> nodes = new ArrayList<List<Object>>();
			nodes.add(rep.getNodeInstance());

			List<List<StepTypes>> types = new ArrayList<List<StepTypes>>();
			types.add(rep.getStepTypes());

			SortedSet<Integer> traces = rep.getTraceIndex();
			boolean rel = rep.isReliable();

			//Create a n-alignment result with this attributes
			AllSyncReplayResult allRep = new AllSyncReplayResult(nodes, types, -1, rel);
			allRep.setTraceIndex(traces);//The creator not allow add the set directly
			col.add(allRep);
		}
		PNMatchInstancesRepResult alignments = new PNMatchInstancesRepResult(col);
		AlignETCResult precisionResult2 = precisionMeasurer.checkGenericAlignETC(context, log, petrinet, initialMarking, alignments, precisionResult, precisionSettings);
		
		debug("precision " + precisionResult2.ap);
		result.reportPrecision(file, miner, useFinalMarking, precisionResult2.ap);
    	
    	
		
    	//measure precision/generalisation
    	AlignmentPrecGen precisionMeasurer2 = new AlignmentPrecGen();
    	AlignmentPrecGenRes precisionGeneralisation;
    	precisionGeneralisation = precisionMeasurer2.measureConformanceAssumingCorrectAlignment(context, mapping, replayed, petrinet, initialMarking, true);
    	
    	debug("precision2 " + precisionGeneralisation.getPrecision());
    	result.reportPrecision(file, miner, useFinalMarking, precisionGeneralisation.getPrecision());
    	
    	result.reportReplay(
    			file, 
    			miner, 
    			useFinalMarking, 
    			//(0.5 * (Double) replayed.getInfo().get("Move-Model Fitness")) + (0.5 * (Double) replayed.getInfo().get("Move-Log Fitness")),
    			fitness,
    			precisionGeneralisation.getPrecision(), 
    			precisionGeneralisation.getGeneralization());
    	
    	
    	return true;
    	
	}
	
	protected void debug(String x) {
		System.out.println(x);
	}
}
