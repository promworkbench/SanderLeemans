package batch;

import java.util.Collection;
import java.util.Map;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quintuple;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.conversion.ProcessTree2Petrinet;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.InvalidProcessTreeException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.NotYetImplementedException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.PetrinetWithMarkings;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.UnfoldedNode;

public class AryaAlignment {
	public static Quintuple<PNRepResult, Petrinet, Marking, Double, TransEvClassMapping> alignment(PluginContext context, ProcessTree tree, XLog log,
			XEventClassifier classifier, Collection<XEventClass> activities) throws AStarException {
		PetrinetWithMarkings pnwm;
		try {
			pnwm = ProcessTree2Petrinet.convertKeepStructure(tree);
		} catch (NotYetImplementedException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidProcessTreeException e) {
			e.printStackTrace();
			return null;
		}
		Petrinet petrinet = pnwm.petrinet;
		Marking initialMarking = pnwm.initialMarking;
		Marking finalMarking = pnwm.finalMarking;
		Map<Transition, UnfoldedNode> mapTransition2Path = pnwm.mapTransition2Path;

		//create mapping transition -> eventclass
		XEventClass dummy = new XEventClass("", 1);
		TransEvClassMapping mapping = new TransEvClassMapping(classifier, dummy);
		for (Transition t : petrinet.getTransitions()) {
			if (t.isInvisible()) {
				mapping.put(t, dummy);
			} else {
				mapping.put(t, mapTransitionToEventClass(t, activities, mapTransition2Path));
			}
		}

		PNLogReplayer replayer = new PNLogReplayer();
		CostBasedCompleteParam replayParameters = new CostBasedCompleteParam(activities, dummy,
				petrinet.getTransitions(), 1, 1);
		replayParameters.setInitialMarking(initialMarking);
		replayParameters.setMaxNumOfStates(Integer.MAX_VALUE);
		IPNReplayAlgorithm algorithm = new PetrinetReplayerWithILP();
		replayParameters.setFinalMarkings(new Marking[] { finalMarking });
		replayParameters.setCreateConn(false);
		replayParameters.setGUIMode(false);

		PNRepResult result = replayer.replayLog(context, petrinet, log, mapping, algorithm, replayParameters);
		
		System.out.println("alignment done");
		return Quintuple.of(result, petrinet, initialMarking, (Double) result.getInfo().get(PNRepResult.TRACEFITNESS), mapping);
	}

	private static XEventClass mapTransitionToEventClass(Transition t, Iterable<XEventClass> activities,
			Map<Transition, UnfoldedNode> mapTransition2Path) {
		//find the event class with the same label as the transition
		for (XEventClass activity : activities) {
			UnfoldedNode unode = mapTransition2Path.get(t);
			if (unode.getNode().getName().equals(activity.toString())) {
				return activity;
			}
		}
		return null;
	}
	
	public static double getFitness(Quintuple<PNRepResult, Petrinet, Marking, Double, TransEvClassMapping> alignmentResult) {
		return alignmentResult.getD();
	}
}
