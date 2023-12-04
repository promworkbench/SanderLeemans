package svn45crimes;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;

import nl.tue.alignment.Progress;
import nl.tue.alignment.Replayer;
import nl.tue.alignment.ReplayerParameters;
import nl.tue.alignment.algorithms.ReplayAlgorithm;
import nl.tue.astar.AStarException;

public class MeasureAlignment implements Measure {

	public String getTitle() {
		return "align";
	}

	public String getLatexTitle() {
		return "alignments";
	}

	public String[] getMeasureNames() {
		return new String[] { "fitness" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "fitness" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return true;
	}

	public boolean isSupportsTrees() {
		return false;
	}

	public double[] compute(File logFile, XLog log, AcceptingPetriNet model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		XEventClassifier eventClassifier = new XEventNameClassifier();
		XEventClass dummyEvClass = new XEventClass("DUMMY", 99999);
		TransEvClassMapping mapping = constructMapping(model.getNet(), log, dummyEvClass, eventClassifier);
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);
		XEventClasses classes = summary.getEventClasses();

		ReplayerParameters parameters = new ReplayerParameters.AStar();
		doReplay(model.getNet(), model.getInitialMarking(), model.getFinalMarkings().iterator().next(), log, mapping,
				classes, parameters);

		return null;
	}

	private TransEvClassMapping constructMapping(Petrinet net, XLog log, XEventClass dummyEvClass,
			XEventClassifier eventClassifier) {
		TransEvClassMapping mapping = new TransEvClassMapping(eventClassifier, dummyEvClass);

		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

		for (Transition t : net.getTransitions()) {
			boolean mapped = false;
			for (XEventClass evClass : summary.getEventClasses().getClasses()) {
				String id = evClass.getId();

				if (t.getLabel().equals(id)) {
					mapping.put(t, evClass);
					mapped = true;
					break;
				} else if (id.equals(t.getLabel() + "+complete")) {
					mapping.put(t, evClass);
					mapped = true;
					break;
				} else if (id.equals(t.getLabel() + "+")) {
					mapping.put(t, evClass);
					mapped = true;
					break;
				}
			}

			if (!mapped && !t.isInvisible()) {
				mapping.put(t, dummyEvClass);
			}

		}

		return mapping;
	}

	private static void doReplay(PetrinetGraph net, Marking initialMarking, Marking finalMarking, XLog log,
			TransEvClassMapping mapping, XEventClasses classes, ReplayerParameters parameters)
			throws InterruptedException, ExecutionException {
		ReplayAlgorithm.Debug.setOutputStream(System.out);

		long start = System.nanoTime();
		Replayer replayer = new Replayer(parameters, (Petrinet) net, initialMarking, finalMarking, classes, mapping,
				true);

		PNRepResult result = replayer.computePNRepResult(Progress.INVISIBLE, log);//, SINGLETRACE);
		long end = System.nanoTime();

		int cost = (int) Double.parseDouble((String) result.getInfo().get(Replayer.MAXMODELMOVECOST));
		int timeout = 0;
		double time = 0;
		int mem = 0;
		int lps = 0;
		double pretime = 0;
		for (SyncReplayResult res : result) {
			cost += res.getTraceIndex().size() * res.getInfo().get(PNRepResult.RAWFITNESSCOST);
			timeout += res.getTraceIndex().size() * (res.getInfo().get(Replayer.TRACEEXITCODE).intValue() != 1 ? 1 : 0);
			time += res.getInfo().get(PNRepResult.TIME);
			pretime += res.getInfo().get(Replayer.PREPROCESSTIME);
			lps += res.getInfo().get(Replayer.HEURISTICSCOMPUTED);
			mem = Math.max(mem, res.getInfo().get(Replayer.MEMORYUSED).intValue());
		}

		// number timeouts
		System.out.print(timeout);
		// clocktime
		System.out.print(String.format("%.3f", (end - start) / 1000000.0));
		// cpu time
		System.out.print(String.format("%.3f", time));
		// preprocess time
		System.out.print(String.format("%.3f", pretime));
		// max memory
		System.out.print(mem);
		// solves lps.
		System.out.print(lps);
		// total cost.
		System.out.print(cost);

		System.out.flush();
	}

	public double[] compute(File logFile, XLog log, EfficientTree model, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		return null;
	}

}
