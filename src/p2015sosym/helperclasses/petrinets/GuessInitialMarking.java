package p2015sosym.helperclasses.petrinets;

import gnu.trove.set.hash.THashSet;

import java.util.Set;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;

public class GuessInitialMarking {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@Plugin(name = "Guess initial marking", parameterLabels = { "Petri net" }, returnLabels = { "Guessed initial marking" }, returnTypes = { Marking.class }, userAccessible = true, help = "create a marking; add all places that have no incoming arcs")
	public Marking guess(PluginContext context, Petrinet net) {
		return guess(net);
	}
	
	public static Marking guess(Petrinet net) {
		Set<Place> result = new THashSet<>();
		for (Place p : net.getPlaces()) {
			if (net.getInEdges(p).isEmpty()) {
				result.add(p);
			}
		}
		return new Marking(result);
	}
}
