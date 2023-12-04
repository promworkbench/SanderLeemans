package thesis.evaluation.standAloneMiners;

import gnu.trove.set.hash.THashSet;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.log.logabstraction.BasicLogRelations;
import org.processmining.plugins.petrinet.mining.alphaminer.AlphaMiner;

import thesis.evaluation.standAloneMiners.RunHeuristicsMiner.unmappableTransitionException;
import thesis.helperClasses.FakeContext;

public class RunAlphaMiner {
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
			System.out.println("Usage: AlphaMiner.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}
		
		XLog log = RunInductiveMiner.loadLog(file);
		mine(log);
		
		ExecutorService e = (ExecutorService) new FakeContext().getExecutor();
		e.shutdown();
	}

	public static AcceptingPetriNet mine(XLog log) throws InterruptedException, ExecutionException, unmappableTransitionException {

		final XEventClassifier classifier = MiningParameters.getDefaultClassifier();
		Set<XEventClassifier> classifiers = new THashSet<>();
		classifiers.add(classifier);

		XLogInfo logInfo = new XLogInfoImpl(log, classifier, classifiers);
		BasicLogRelations logRelations = new BasicLogRelations(log, logInfo);

		AlphaMiner miner = new AlphaMiner();
		Object[] a = miner.doMining(new FakeContext(), logInfo, logRelations);
		Petrinet petrinet = (Petrinet) a[0];
		Marking initialMarking = (Marking) a[1];

		TransEvClassMapping mapping = RunHeuristicsMiner.getTransEvClassMapping(classifier, logInfo, petrinet);

		return AcceptingPetriNetFactory.createAcceptingPetriNet(petrinet, initialMarking);
	}
}
