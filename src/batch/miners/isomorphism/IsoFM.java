package batch.miners.isomorphism;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import batch.stability.batchStability.PetrinetWithInitialMarking;

public class IsoFM implements Isomorphic {

	public boolean isIsomorphic(PetrinetWithInitialMarking a, PetrinetWithInitialMarking b) {
		for (Transition tA : a.petrinet.getTransitions()) {
			boolean found = false;
			for (Transition tB : b.petrinet.getTransitions()) {
				if (tA.getLabel().equals(tB.getLabel())) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		
		for (Transition tB : b.petrinet.getTransitions()) {
			boolean found = false;
			for (Transition tA : a.petrinet.getTransitions()) {
				if (tA.getLabel().equals(tB.getLabel())) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

}
