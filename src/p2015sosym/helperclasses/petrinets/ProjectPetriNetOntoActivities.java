package p2015sosym.helperclasses.petrinets;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

public class ProjectPetriNetOntoActivities {
	public static Petrinet project(Petrinet net, String... names) {
		Petrinet result = new PetrinetImpl("projected Petri net");

		Map<PetrinetNode, PetrinetNode> net2reduced = new THashMap<>();

		//copy places
		for (Place p : net.getPlaces()) {
			net2reduced.put(p, result.addPlace(p.getLabel()));
		}

		//copy transitions
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible() || !ArrayUtils.contains(names, t.getLabel()) || t.getLabel().equals("tau")) {
				//copy as invisible
				Transition reducedT = result.addTransition("tau");
				reducedT.setInvisible(true);
				net2reduced.put(t, reducedT);
			} else {
				//copy normally
				for (int i = 0; i < names.length; i++) {
					if (names[i].equals(t.getLabel())) {
						net2reduced.put(t, result.addTransition(((char) i) + ""));
						break;
					}
				}
			}
		}

		//copy edges
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : net.getEdges()) {
			if (e.getSource() instanceof Place) {
				result.addArc((Place) net2reduced.get(e.getSource()), (Transition) net2reduced.get(e.getTarget()));
			} else {
				result.addArc((Transition) net2reduced.get(e.getSource()), (Place) net2reduced.get(e.getTarget()));
			}
		}

		return result;
	}
}
