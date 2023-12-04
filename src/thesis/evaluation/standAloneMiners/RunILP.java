package thesis.evaluation.standAloneMiners;

import java.io.File;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.hybridilpminer.parameters.XLogHybridILPMinerParametersImpl;
import org.processmining.hybridilpminer.plugins.HybridILPMinerPlugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.ilpminer.ILPMiner;

import gnu.trove.set.hash.THashSet;
import thesis.helperClasses.FakeContext;

public class RunILP {
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
			System.out.println("Usage: RunILP.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}

		XLog log = RunInductiveMiner.loadLog(file);
		AcceptingPetriNet petrinet = mine(log);
		System.out.println(petrinet);
	}

	public static AcceptingPetriNet mine(XLog log) {
		final XEventClassifier classifier = MiningParameters.getDefaultClassifier();
		Set<XEventClassifier> classifiers = new THashSet<>();
		classifiers.add(classifier);
		XLogInfo logInfo = new XLogInfoImpl(log, classifier, classifiers);

		ILPMiner miner = new ILPMiner();
		XLogHybridILPMinerParametersImpl parameters = new XLogHybridILPMinerParametersImpl(null, log, classifier);
		Object[] a = HybridILPMinerPlugin.applyParams(new FakeContext(), log, parameters);

		Petrinet petrinet = (Petrinet) a[0];
		Marking initialMarking = (Marking) a[1];
		//TransEvClassMapping mapping = RunHeuristicsMiner.getTransEvClassMapping(classifier, logInfo, petrinet);

		return new AcceptingPetriNetImpl(petrinet, initialMarking, new Marking());
	}
}
