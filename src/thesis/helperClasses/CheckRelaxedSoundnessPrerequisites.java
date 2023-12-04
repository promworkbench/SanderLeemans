package thesis.helperClasses;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class CheckRelaxedSoundnessPrerequisites {
	public static boolean checkPrerequisites(AcceptingPetriNet net) {
		//first of all, the initial marking cannot be empty
		if (net.getInitialMarking().isEmpty()) {
			return false;
		}

		//check whether there are no token generators
		for (Transition t : net.getNet().getTransitions()) {
			if (net.getNet().getInEdges(t).isEmpty() && !net.getNet().getOutEdges(t).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
