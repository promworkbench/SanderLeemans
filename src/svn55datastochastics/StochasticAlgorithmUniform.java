package svn55datastochastics;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeightsImpl;
import org.processmining.stochasticlabelledpetrinets.plugins.StochasticLabelledPetriNetExportPlugin;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class StochasticAlgorithmUniform implements StochasticAlgorithm {

	public String getName() {
		return "uni";
	}

	public String getAbbreviation() {
		return "uni";
	}

	public String getLatexName() {
		return "BUC";
	}

	public boolean createsDataModels() {
		return false;
	};

	public void run(File logFile, XLog log, AcceptingPetriNet anet, File modelFile) throws Exception {
		StochasticLabelledPetriNetSimpleWeightsImpl result = new StochasticLabelledPetriNetSimpleWeightsImpl();

		//places
		TObjectIntMap<Place> place2p = new TObjectIntHashMap<>();
		for (Place place : anet.getNet().getPlaces()) {
			place2p.put(place, result.addPlace());
		}

		//initial marking
		for (Place place : anet.getInitialMarking()) {
			int p = place2p.get(place);
			int occurrences = anet.getInitialMarking().occurrences(place);

			result.addPlaceToInitialMarking(p, occurrences);
		}

		//transitions
		TObjectIntMap<Transition> transition2t = new TObjectIntHashMap<>();
		for (Transition transition : anet.getNet().getTransitions()) {
			if (transition.isInvisible()) {
				transition2t.put(transition, result.addTransition(1));
			} else {
				transition2t.put(transition, result.addTransition(transition.getLabel(), 1));
			}
		}

		//arcs
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arc : anet.getNet().getEdges()) {
			if (arc.getSource() instanceof Place) {
				int source = place2p.get(arc.getSource());
				int target = transition2t.get(arc.getTarget());
				result.addPlaceTransitionArc(source, target);
			} else {
				int source = transition2t.get(arc.getSource());
				int target = place2p.get(arc.getTarget());
				result.addTransitionPlaceArc(source, target);
			}
		}

		//store
		StochasticLabelledPetriNetExportPlugin.export(result, modelFile);
	}

	public String getFileExtension() {
		return ".slpn";
	}

}
