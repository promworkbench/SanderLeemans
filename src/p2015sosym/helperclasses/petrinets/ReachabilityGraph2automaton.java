package p2015sosym.helperclasses.petrinets;

import java.util.Map;

import org.processmining.models.graphbased.directed.transitionsystem.ReachabilityGraph;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.semantics.petrinet.Marking;

import dk.brics.automaton2.Automaton;
import gnu.trove.map.hash.THashMap;

public class ReachabilityGraph2automaton {
	public static Automaton convert(ReachabilityGraph reachabilityGraph, Marking initialMarking) {
		Automaton result = new Automaton();

		Map<State, dk.brics.automaton2.State> cover2auto = new THashMap<>();

		//add the states
		for (State state : reachabilityGraph.getNodes()) {
			dk.brics.automaton2.State autoState = new dk.brics.automaton2.State();
			if (reachabilityGraph.getOutEdges(state).isEmpty()) {
				autoState.setAccept(true);
			}
			cover2auto.put(state, autoState);
		}
		
		//overwrite the initial state
		{
			State state = reachabilityGraph.getNode(initialMarking);
			cover2auto.put(state, result.getInitialState());
			if (reachabilityGraph.getOutEdges(state).isEmpty()) {
				result.getInitialState().setAccept(true);
			}
		}

		//copy transitions
		for (Transition e : reachabilityGraph.getEdges()) {
			//find or add source
			dk.brics.automaton2.State autoSource = cover2auto.get(e.getSource());
			dk.brics.automaton2.State autoTarget = cover2auto.get(e.getTarget());

			//add edge
			autoSource.addTransition(new dk.brics.automaton2.Transition(e.getLabel().charAt(0), e.getLabel().charAt(0),
					autoTarget));
		}

		return result;
	}
}
