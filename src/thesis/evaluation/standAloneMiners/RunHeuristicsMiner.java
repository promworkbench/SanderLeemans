package thesis.evaluation.standAloneMiners;

import gnu.trove.set.hash.THashSet;

import java.io.File;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.heuristicsnet.miner.heuristics.converter.HeuristicsNetToPetriNetConverter;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import thesis.helperClasses.FakeContext;

public class RunHeuristicsMiner {

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
			System.out.println("Usage: HeuristcsMiner.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}

		XLog log = RunInductiveMiner.loadLog(file);
		
		mine(log);
	}
	
	public static AcceptingPetriNet mine(XLog log) throws unmappableTransitionException {
		final XEventClassifier classifier = MiningParameters.getDefaultClassifier();
		Set<XEventClassifier> classifiers = new THashSet<>();
		classifiers.add(classifier);
		
		XLogInfo logInfo = new XLogInfoImpl(log, classifier, classifiers);

		HeuristicsMinerSettings settings = new HeuristicsMinerSettings();
		settings.setClassifier(classifier);
		HeuristicsMiner miner = new HeuristicsMiner(new FakeContext(), log, logInfo, settings);
		HeuristicsNet net = miner.mine();

		Object[] arr = HeuristicsNetToPetriNetConverter.converter(new FakeContext(), net);
		Petrinet petrinet = (Petrinet) arr[0];
		Marking initialMarking = (Marking) arr[1];

		TransEvClassMapping mapping = getTransEvClassMapping(classifier, logInfo, petrinet);
		
		return AcceptingPetriNetFactory.createAcceptingPetriNet(petrinet, initialMarking);
	}

	public static TransEvClassMapping getTransEvClassMapping(XEventClassifier classifier, XLogInfo logInfo, Petrinet petrinet)
			throws unmappableTransitionException {
		//create mapping
		//debug("Map transitions to XEventClasses");
		XEventClass dummy = new XEventClass("", 1);
		TransEvClassMapping mapping = new TransEvClassMapping(classifier, dummy);
		XEventClasses activities = logInfo.getEventClasses();

		for (Transition t : petrinet.getTransitions()) {
			if (t.isInvisible()) {
				mapping.put(t, dummy);
			} else {
				mapping.put(t, mapTransitionToEventClass(t, activities));
			}
		}
		return mapping;
	}

	public static class unmappableTransitionException extends Exception {
		private static final long serialVersionUID = 7492151078447630466L;

		public unmappableTransitionException(Transition t) {
			super("no XEventClass found for transition " + t);
		}
	}

	protected static XEventClass mapTransitionToEventClass(Transition t, XEventClasses activities)
			throws unmappableTransitionException {
		//find the event class with the same label as the transition
		for (XEventClass activity : activities.getClasses()) {
			if (t.getLabel().equals(activity.toString()) || activity.toString().equals(t.getLabel() + "+complete")) {
				return activity;
			}
		}
		throw new unmappableTransitionException(t);
	}
}
