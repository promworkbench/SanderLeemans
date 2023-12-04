package caise2020isextension;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.processmining.plugins.stochasticpetrinet.StochasticNetUtils;
import org.processmining.plugins.stochasticpetrinet.enricher.PerformanceEnricherConfig;
import org.processmining.plugins.stochasticpetrinet.enricher.PerformanceEnricherPlugin;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import thesis.helperClasses.FakeContext;

public class AlgorithmAndreas implements Algorithm {

	public String getName() {
		return "Andreas";
	}

	public String getAbbreviation() {
		return "ARS";
	}
	
	public String getLatexName() {
		return "ARS";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		PluginContext context = new FakeContext();

		Object[] objects;
		{
			Petrinet net = getFittingPetrinetWithChoicesModeledAsImmediateTransitions(context, log);
			PerformanceEnricherConfig config = new PerformanceEnricherConfig(
					StochasticNet.DistributionType.GAUSSIAN_KERNEL, StochasticNet.TimeUnit.HOURS,
					StochasticNet.ExecutionPolicy.RACE_ENABLING_MEMORY, null);
			Manifest manifest = (Manifest) StochasticNetUtils.replayLog(context, net, log, true, true);
			objects = PerformanceEnricherPlugin.transform(context, manifest, config);
		}

		StochasticNet net = (StochasticNet) objects[0];
		Marking marking = (Marking) objects[1];

		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking, new FakeGraphLayoutConnection(net));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

	public static Petrinet getFittingPetrinetWithChoicesModeledAsImmediateTransitions(PluginContext context, XLog log) {
		MiningParameters params = new MiningParametersIMf();
		params.setNoiseThreshold(0.0f); // to guarantee perfect fitness
		Object[] result = IMPetriNet.minePetriNet(context, log, params);
		// the model is the first parameter, second is initial marking, third is final marking
		Petrinet net = (Petrinet) result[0];

		int newPlaceCount = 0;

		ArrayList<Place> places = new ArrayList<>(net.getPlaces());

		// alter structure of net: We need to represent all choices by conflicting "immediate" transitions
		for (Place place : places) {
			// check all places for outgoing arcs and see whether their transitions are visible or invisible
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = net.getOutEdges(place);
			boolean inConflict = edges.size() > 1;
			if (inConflict) {
				// check whether we have mixed transition types:
				Set<Transition> immediateTransitions = new HashSet<>();
				Set<Transition> visibleTransitions = new HashSet<>();

				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
					Transition transition = (Transition) edge.getTarget();
					if (transition.isInvisible()) {
						immediateTransitions.add(transition);
					} else {
						visibleTransitions.add(transition);
					}
				}
				if (visibleTransitions.size() > 0) {
					// add immediate transitions before the visible ones to allow for a probabilistic decision
					// even when using race condition semantics.
					for (Transition t : visibleTransitions) {
						net.removeArc(place, t);
						// this operation should be soundness preserving, as it is one of inverted Murata's reduction rules
						Place choicePlace = net.addPlace("newPlace" + newPlaceCount);
						Transition choiceTransition = net.addTransition("tau choice" + newPlaceCount++);
						choiceTransition.setInvisible(true);

						net.addArc(place, choiceTransition);
						net.addArc(choiceTransition, choicePlace);
						net.addArc(choicePlace, t);
					}
				}
			}
		}
		return net;
	}
}
