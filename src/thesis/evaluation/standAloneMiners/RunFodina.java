package thesis.evaluation.standAloneMiners;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.converter.CausalNetToPetrinet;
import org.processmining.plugins.bpmnminer.plugins.FodinaMinerPlugin;
import org.processmining.plugins.bpmnminer.types.MinerSettings;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class RunFodina {

	public static void main(String[] args) throws Exception {
		boolean help = false;
		File file = null;
		if (args.length != 1) {
			help = true;
		} else {
			file = new File(args[0]);
			help = help || !file.exists();
		}

		if (help) {
			System.out.println("Usage: Fodina.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}

		XLog log = RunInductiveMiner.loadLog(file);
		AcceptingPetriNet net = mine(log);
	}

	public static AcceptingPetriNet mine(XLog log) {
		MinerSettings settings = new MinerSettings();
		settings.useUniqueStartEndTasks = true;
		settings.classifier = MiningParameters.getDefaultClassifier();

		Object[] result = FodinaMinerPlugin.runMiner(null, log, settings);
		CausalNet net = (CausalNet) result[0];
		Object[] result2 = CausalNetToPetrinet.convert(null, net);
		AcceptingPetriNet aNet1 = AcceptingPetriNetFactory.createAcceptingPetriNet((Petrinet) result2[0],
				(Marking) result2[1]);

		//as Fodina introduces unique start and end transitions, we need to project them away
		String[] names = FluentIterable.from(aNet1.getNet().getTransitions())
				.transform(new Function<Transition, String>() {
					public String apply(Transition arg0) {
						return arg0.getLabel();
					}
				}).filter(new Predicate<String>() {
					public boolean apply(String arg0) {
						return !arg0.equals("__SOURCE__") && !arg0.equals("__SINK__");
					}
				}).toArray(String.class);
		AcceptingPetriNet projectedNet = project(aNet1, names);
		return projectedNet;
	}

	public static AcceptingPetriNet project(AcceptingPetriNet net, String... names) {
		Petrinet newPetriNet = new PetrinetImpl("projected Petri net");

		Map<PetrinetNode, PetrinetNode> net2reduced = new THashMap<>();

		//copy places
		for (Place p : net.getNet().getPlaces()) {
			net2reduced.put(p, newPetriNet.addPlace(p.getLabel()));
		}

		//copy transitions
		for (Transition t : net.getNet().getTransitions()) {
			if (t.isInvisible() || !ArrayUtils.contains(names, t.getLabel()) || t.getLabel().equals("tau")) {
				//copy as invisible
				Transition reducedT = newPetriNet.addTransition("tau");
				reducedT.setInvisible(true);
				net2reduced.put(t, reducedT);
			} else {
				//copy normally
				for (int i = 0; i < names.length; i++) {
					if (names[i].equals(t.getLabel())) {
						net2reduced.put(t, newPetriNet.addTransition(t.getLabel()));
						break;
					}
				}
			}
		}

		//copy edges
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : net.getNet().getEdges()) {
			if (e.getSource() instanceof Place) {
				newPetriNet.addArc((Place) net2reduced.get(e.getSource()), (Transition) net2reduced.get(e.getTarget()));
			} else {
				newPetriNet.addArc((Transition) net2reduced.get(e.getSource()), (Place) net2reduced.get(e.getTarget()));
			}
		}

		//copy initial marking
		Marking newInitialMarking = new Marking();
		for (Place p : net.getInitialMarking()) {
			newInitialMarking.add((Place) net2reduced.get(p), net.getInitialMarking().occurrences(p));
		}

		//copy final markings
		Set<Marking> newFinalMarkings = new THashSet<>();
		for (Marking finalMarking : net.getFinalMarkings()) {
			Marking newFinalMarking = new Marking();
			for (Place p : finalMarking) {
				newFinalMarking.add((Place) net2reduced.get(p), finalMarking.occurrences(p));
			}
			newFinalMarkings.add(newFinalMarking);
		}

		return AcceptingPetriNetFactory.createAcceptingPetriNet(newPetriNet, newInitialMarking, newFinalMarkings);
	}
}
