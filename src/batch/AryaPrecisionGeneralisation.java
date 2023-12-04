package batch;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Quintuple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGen;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGenRes;

public class AryaPrecisionGeneralisation {

	public static Pair<Double, Double> measure(PluginContext context,
			Quintuple<PNRepResult, Petrinet, Marking, Double, TransEvClassMapping> alignmentResult, XLog log) {
		//		AlignETCPlugin precisionMeasurer = new AlignETCPlugin();
		//		AlignETCResult precisionResult = new AlignETCResult();
		//		AlignETCSettings precisionSettings = new AlignETCSettings(precisionResult);
		//		//Convert to n-alignments object
		//		Collection<AllSyncReplayResult> col = new ArrayList<AllSyncReplayResult>();
		//		for (SyncReplayResult rep : alignmentResult.getA()) {
		//
		//			//Get all the attributes of the 1-alignment result
		//			List<List<Object>> nodes = new ArrayList<List<Object>>();
		//			nodes.add(rep.getNodeInstance());
		//
		//			List<List<StepTypes>> types = new ArrayList<List<StepTypes>>();
		//			types.add(rep.getStepTypes());
		//
		//			SortedSet<Integer> traces = rep.getTraceIndex();
		//			boolean rel = rep.isReliable();
		//
		//			//Create a n-alignment result with this attributes
		//			AllSyncReplayResult allRep = new AllSyncReplayResult(nodes, types, -1, rel);
		//			allRep.setTraceIndex(traces);//The creator not allow add the set directly
		//			col.add(allRep);
		//		}
		//		PNMatchInstancesRepResult alignments = new PNMatchInstancesRepResult(col);
		//		AlignETCResult precisionResult2 = precisionMeasurer.checkGenericAlignETC(context, log, alignmentResult.getB(),
		//				alignmentResult.getC(), alignments, precisionResult, precisionSettings);
		//		return precisionResult2.ap;

		AlignmentPrecGen precisionMeasurer2 = new AlignmentPrecGen();
		AlignmentPrecGenRes precisionGeneralisation = precisionMeasurer2.measureConformanceAssumingCorrectAlignment(
				context, alignmentResult.getE(), alignmentResult.getA(), alignmentResult.getB(),
				alignmentResult.getC(), true);

		System.out.println("precision done");
		return Pair.of(precisionGeneralisation.getPrecision(), precisionGeneralisation.getGeneralization());
	}
	
	public static double getPrecision(Pair<Double, Double> precisionGeneralisationResult) {
		return precisionGeneralisationResult.getA();
	}
	
	public static double getGeneralisation(Pair<Double, Double> precisionGeneralisationResult) {
		return precisionGeneralisationResult.getB();
	}
}
