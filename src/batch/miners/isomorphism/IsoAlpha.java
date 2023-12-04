package batch.miners.isomorphism;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import batch.stability.batchStability.PetrinetWithInitialMarking;

public class IsoAlpha implements Isomorphic {

	/*
	 * (non-Javadoc)
	 * 
	 * @see batch.miners.isomorphism.Isomorphic#isIsomorphic(batch.stability.
	 * batchStability.PetrinetWithInitialMarking,
	 * batch.stability.batchStability.PetrinetWithInitialMarking)
	 * 
	 * Assumption: no duplicate transition labels
	 */
	public boolean isIsomorphic(PetrinetWithInitialMarking a, PetrinetWithInitialMarking b) {
		if (a.petrinet == null) {
			debug("a null");
			return false;
		} else if (b.petrinet == null) {
			debug("b null");
			return false;
		}
		if (!(a.petrinet.getPlaces().size() == b.petrinet.getPlaces().size()
				&& a.petrinet.getTransitions().size() == b.petrinet.getTransitions().size() && a.initialMarking.size() == b.initialMarking
				.size())) {
			return false;
		} else {
			return hash(a).equals(hash(b));
		}
	}
	
	public static String hash(PetrinetWithInitialMarking p) {
		StringBuilder result = new StringBuilder();
		
		result.append(hash(p.petrinet));
		result.append("-m-");
		result.append(hash(p.petrinet, p.initialMarking.toList()));
		
		return result.toString();
	}
	
	private static String hash(Petrinet p) {
		StringBuilder result = new StringBuilder();
		
		result.append("-t-");
		
		result.append(hash(p.getTransitions()));
		
		result.append("-p-");
		
		result.append(hash(p, p.getPlaces()));
		
		return result.toString();
	}
	
	private static String hash(Collection<Transition> transitions) {
		List<String> l = new ArrayList<String>();
		for (Transition t : transitions) {
			l.add(hash(t));
		}
		Collections.sort(l);
		StringBuilder result =  new StringBuilder();
		result.append("{");
		for (String s : l) {
			result.append(s);
			result.append(",");
		}
		result.append("}");
		return result.toString();
	}
	
	private static String hash(Petrinet p, Collection<Place> places) {
		List<String> l = new ArrayList<String>();
		for (Place t : places) {
			l.add(hash(p, t));
		}
		Collections.sort(l);
		StringBuilder result =  new StringBuilder();
		result.append("{");
		for (String s : l) {
			result.append(s);
			result.append(",");
		}
		result.append("}");
		return result.toString();
	}
	
	private static String hash(Petrinet p, Place pp) {
		List<String> preSet = getPreSet(p, pp);
		Collections.sort(preSet);
		
		List<String> postSet = getPostSet(p, pp);
		Collections.sort(postSet);
		
		StringBuilder result = new StringBuilder();
		
		result.append("(");
		for (String s : preSet) {
			result.append(s);
			result.append(",");
		}
		result.append("|");
		for (String s : postSet) {
			result.append(s);
			result.append(",");
		}
		result.append(")");
		
		return result.toString();
	}
	
	private static List<String> getPostSet(Petrinet a, Place pA) {
		List<String> result = new ArrayList<String>();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outgoingEdges = a.getOutEdges(pA);
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outgoingEdges) {
			result.add(hash((Transition) edge.getTarget()));
		}
		return result;
	}
	
	private static List<String> getPreSet(Petrinet a, Place pA) {
		List<String> result = new ArrayList<String>();
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> incomingEdges = a.getInEdges(pA);
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : incomingEdges) {
			result.add(hash((Transition) edge.getSource()));
		}
		return result;
	}
	
	private static String hash(Transition t) {
		return t.getLabel();
	}

	private void debug(String x) {
		System.out.println(x);
	}

}
