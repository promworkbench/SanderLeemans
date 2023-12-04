package coopis2018;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.inductiveVisualMiner.alignment.IvMEventClasses;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGen;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGenRes;

import nl.tue.astar.AStarException;
import thesis.helperClasses.FakeContext;

public class MeasureArya implements Measure {

	public String getTitle() {
		return "Arya";
	}

	public String[] getMeasureNames() {
		return new String[] { "fitness", "precision", "generalisation" };
	}

	public boolean isSupportsTrees() {
		return false;
	}

	public int getNumberOfMeasures() {
		return 3;
	}

	public double[] compute(XLog log, AcceptingPetriNet aNet) throws AStarException {
		PluginContext context = new FakeContext();
		Triple<Double, TransEvClassMapping, PNRepResult> t = AryaFitness(aNet, log);
		double fitness = t.getA();
		TransEvClassMapping mapping = t.getB();
		PNRepResult result = t.getC();

		AlignmentPrecGen precisionMeasurer2 = new AlignmentPrecGen();
		AlignmentPrecGenRes precisionGeneralisation = precisionMeasurer2.measureConformanceAssumingCorrectAlignment(
				context, mapping, result, aNet.getNet(), aNet.getInitialMarking(), true);
		double precision = precisionGeneralisation.getPrecision();
		double generalisation = precisionGeneralisation.getGeneralization();

		return new double[] { fitness, precision, generalisation };
	}

	public double[] compute(XLog log, EfficientTree tree) {
		return null;
	}

	public static Triple<IvMEventClasses, XEventClass, TransEvClassMapping> getMapping(AcceptingPetriNet aNet,
			XLog log) {
		XLogInfo myLogInfo = XLogInfoImpl.create(log, XLogInfoImpl.NAME_CLASSIFIER);
		IvMEventClasses eventClasses = new IvMEventClasses(myLogInfo.getEventClasses());
		XEventClass dummy = new XEventClass("", 1);
		eventClasses.harmonizeIndices();

		TransEvClassMapping mapping;
		{
			mapping = new TransEvClassMapping(eventClasses.getClassifier(), dummy);

			for (Transition t : aNet.getNet().getTransitions()) {
				if (t.isInvisible()) {
					mapping.put(t, dummy);
				} else {
					XEventClass e = eventClasses.getByIdentity(t.getLabel());
					if (e == null) {
						eventClasses.register(t.getLabel());
						e = eventClasses.getByIdentity(t.getLabel());
					}
					mapping.put(t, e);
				}
			}
		}
		return Triple.of(eventClasses, dummy, mapping);
	}

	public static Triple<Double, TransEvClassMapping, PNRepResult> AryaFitness(AcceptingPetriNet aNet, XLog log)
			throws AStarException {
		Triple<IvMEventClasses, XEventClass, TransEvClassMapping> t = getMapping(aNet, log);
		IvMEventClasses eventClasses = t.getA();
		XEventClass dummy = t.getB();
		TransEvClassMapping mapping = t.getC();

		PNLogReplayer replayer = new PNLogReplayer();
		CostBasedCompleteParam replayParameters = new CostBasedCompleteParam(eventClasses.getClasses(), dummy,
				aNet.getNet().getTransitions(), 1, 1);
		replayParameters.setInitialMarking(aNet.getInitialMarking());
		replayParameters.setMaxNumOfStates(Integer.MAX_VALUE);
		IPNReplayAlgorithm algorithm1 = new PetrinetReplayerWithILP();
		Marking[] finalMarkings = new Marking[aNet.getFinalMarkings().size()];
		replayParameters.setFinalMarkings(aNet.getFinalMarkings().toArray(finalMarkings));
		replayParameters.setCreateConn(false);
		replayParameters.setGUIMode(false);

		PNRepResult result = replayer.replayLog(null, aNet.getNet(), log, mapping, algorithm1, replayParameters);
		double fitness = (double) result.getInfo().get("Trace Fitness");

		return Triple.of(fitness, mapping, result);
	}

	public int getCombinedMeasures() {
		return 1;
	}
}
