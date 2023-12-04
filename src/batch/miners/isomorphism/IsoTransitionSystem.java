package batch.miners.isomorphism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.InductiveMiner.Pair;

import batch.stability.batchStability.PetrinetWithInitialMarking;

public class IsoTransitionSystem implements Isomorphic {

	HashSet<Pair<Place, Place>> visited;

	public boolean isIsomorphic(PetrinetWithInitialMarking a, PetrinetWithInitialMarking b) {
		visited = new HashSet<Pair<Place, Place>>();

		return map(a.petrinet, a.initialMarking.iterator().next(), b.petrinet, b.initialMarking.iterator().next());
	}

	private boolean map(Petrinet pA, Place ppA, Petrinet pB, Place ppB) {

		if (!visited.contains(new Pair<Place, Place>(ppA, ppB))) {
			visited.add(new Pair<Place, Place>(ppA, ppB));
		} else {
			return true;
		}

		for (Transition tA : getPostSet(pA, ppA)) {
			boolean found = false;
			for (Transition tB : getPostSet(pB, ppB)) {
				if (hash(tA).equals(hash(tB))) {
					found = true;
					if (!map(pA, getPutPlace(pA, tA), pB, getPutPlace(pB, tB))) {
						return false;
					}
				}
			}
			if (!found) {
				return false;
			}
		}
		
		for (Transition tB : getPostSet(pB, ppB)) {
			boolean found = false;
			for (Transition tA : getPostSet(pA, ppA)) {
				if (hash(tB).equals(hash(tA))) {
					found = true;
					if (!map(pA, getPutPlace(pA, tA), pB, getPutPlace(pB, tB))) {
						return false;
					}
				}
			}
			if (!found) {
				return false;
			}
		}
		
		return true;
	}

	private static Place getPutPlace(Petrinet p, Transition t) {
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outgoingEdges = p.getOutEdges(t);
		return (Place) outgoingEdges.iterator().next().getTarget();
	}

	private static List<Transition> getPostSet(Petrinet a, Place pA) {
		List<Transition> result = new ArrayList<Transition>();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outgoingEdges = a.getOutEdges(pA);
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outgoingEdges) {
			result.add((Transition) edge.getTarget());
		}
		return result;
	}

	private static String hash(Transition t) {
		return t.getLabel();
	}

}
