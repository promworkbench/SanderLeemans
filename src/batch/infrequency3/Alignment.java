package batch.infrequency3;

import java.util.Random;

import nl.tue.astar.AStarThread.Canceller;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.fitness.BehaviorCounter;
import org.processmining.plugins.etm.fitness.metrics.FitnessReplay;
import org.processmining.plugins.etm.fitness.metrics.Generalization;
import org.processmining.plugins.etm.fitness.metrics.PrecisionEscEdges;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.ProcessTreeToNAryTree;
import org.processmining.plugins.inductiveVisualMiner.alignment.ETMAlignment;
import org.processmining.plugins.inductiveVisualMiner.alignment.IvMEventClasses;
import org.processmining.processtree.ProcessTree;

public class Alignment {
	public static Triple<Double, CentralRegistry, NAryTree> alignTree(ProcessTree tree, XEventClassifier classifier, XLog log, Canceller canceller) {

		CentralRegistry registry = new CentralRegistry(log, classifier, new Random());

		//add the event classes of the tree manually
		ETMAlignment.addAllLeavesAsEventClasses(new IvMEventClasses(registry.getEventClasses()), tree);

		ProcessTreeToNAryTree pt2nt = new ProcessTreeToNAryTree(registry.getEventClasses());
		NAryTree nTree = pt2nt.convert(tree);

		registry.updateLogDerived();

		FitnessReplay fr = new FitnessReplay(registry, canceller);
		fr.setNrThreads(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
		fr.setDetailedAlignmentInfoEnabled(true);
		double fitness = fr.getFitness(nTree, null);
		BehaviorCounter behC = registry.getFitness(nTree).behaviorCounter;
		
		System.out.println("alignment done");

		//compute precision
//		System.out.println("Fitness" + fitness);
//		System.out.println("Precision " + computePrecision(registry, nTree));
//		System.out.println("Generalisation " + computeGeneralisation(registry, nTree));

		return Triple.of(fitness, registry, nTree);
	}
	
	public static double computePrecision(Triple<Double, CentralRegistry, NAryTree> t) {
		PrecisionEscEdges precisionReplayTestLog = new PrecisionEscEdges(t.getB());
		return precisionReplayTestLog.getFitness(t.getC(), null);
	}

	public static double computeGeneralisation(Triple<Double, CentralRegistry, NAryTree> t) {
		Generalization generalisationReplay = new Generalization(t.getB());
		return generalisationReplay.getFitness(t.getC(), null);
	}
}
