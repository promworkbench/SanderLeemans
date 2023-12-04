package icpm2020stochasticdiscovery;

import java.util.Iterator;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.DistributionType;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.ExecutionPolicy;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.TimeUnit;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.inductiveVisualMiner.alignment.AcceptingPetriNetAlignment;
import org.processmining.plugins.inductiveVisualMiner.alignment.IvMEventClasses;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import nl.tue.astar.AStarException;

public class AlignmentEstimator {

	@Plugin(name = "Convert Petri net to stochastic Petri net using alignments", level = PluginLevel.Regular, returnLabels = {
			"Stochastic Petri net" }, returnTypes = { StochasticNet.class }, parameterLabels = { "Event Log",
					"Petri net" }, userAccessible = true, help = "pn2spn")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Compute stochastic alignment, default", requiredParameterLabels = { 0, 1 })
	public StochasticNet convert(final PluginContext context, XLog log, AcceptingPetriNet net) throws AStarException {
		return estimate(log, new XEventNameClassifier(), net);
	}

	public static StochasticNet estimate(XLog log, XEventClassifier classifier, AcceptingPetriNet inputNet)
			throws AStarException {
		XLogInfo xLogInfo = XLogInfoFactory.createLogInfo(log, classifier);
		IvMEventClasses eventClasses = new IvMEventClasses(xLogInfo.getEventClasses());
		AcceptingPetriNetAlignment.addAllLeavesAsPerformanceEventClasses(eventClasses, inputNet);

		TObjectIntMap<Transition> transition2occurrence = new TObjectIntHashMap<Transition>(10, 0.5f, 0);

		//create mapping transition -> eventclass
		XEventClass dummy = new XEventClass("", 1);
		TransEvClassMapping mapping;
		{
			mapping = new TransEvClassMapping(eventClasses.getClassifier(), dummy);

			for (Transition t : inputNet.getNet().getTransitions()) {
				if (t.isInvisible()) {
					mapping.put(t, dummy);
				} else {
					mapping.put(t, eventClasses.getByIdentity(t.getLabel()));
				}
			}
		}

		PNLogReplayer replayer = new PNLogReplayer();
		CostBasedCompleteParam replayParameters = new CostBasedCompleteParam(eventClasses.getClasses(), dummy,
				inputNet.getNet().getTransitions(), 1, 1);
		replayParameters.setInitialMarking(inputNet.getInitialMarking());
		replayParameters.setMaxNumOfStates(Integer.MAX_VALUE);
		IPNReplayAlgorithm algorithm = new PetrinetReplayerWithILP();
		Marking[] finalMarkings = new Marking[inputNet.getFinalMarkings().size()];
		replayParameters.setFinalMarkings(inputNet.getFinalMarkings().toArray(finalMarkings));
		replayParameters.setCreateConn(false);
		replayParameters.setGUIMode(false);

		PNRepResult replayResult = replayer.replayLog(null, inputNet.getNet(), log, mapping, algorithm,
				replayParameters);

		for (SyncReplayResult aTrace : replayResult) {
			for (Integer traceIndex : aTrace.getTraceIndex()) {
				XTrace xTrace = log.get(traceIndex);

				Iterator<StepTypes> itType = aTrace.getStepTypes().iterator();
				Iterator<Object> itNode = aTrace.getNodeInstance().iterator();
				int eventIndex = 0;
				int previousModelNode = -1;
				int moveIndex = 0;

				while (itType.hasNext()) {
					StepTypes type = itType.next();
					Object node = itNode.next();

					if (type == StepTypes.MREAL || type == StepTypes.LMGOOD) {
						assert (node instanceof Transition);
						transition2occurrence.adjustOrPutValue((Transition) node, 1, 1);
					}
				}
			}
		}

		//copy net
		StochasticNet result = new StochasticNetImpl(inputNet.getNet().getLabel());
		result.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
		result.setTimeUnit(TimeUnit.HOURS);
		Map<PetrinetNode, PetrinetNode> input2result = new THashMap<>();
		for (Place inputPlace : inputNet.getNet().getPlaces()) {
			Place resultPlace = result.addPlace(inputPlace.getLabel());
			input2result.put(inputPlace, resultPlace);
		}

		for (Transition inputTransition : inputNet.getNet().getTransitions()) {
			Transition resultTransition = result.addTimedTransition(inputTransition.getLabel(),
					transition2occurrence.get(inputTransition), DistributionType.UNIFORM, 0.0, 200.0);

			resultTransition.setInvisible(inputTransition.isInvisible());
			input2result.put(inputTransition, resultTransition);
		}

		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inputNet.getNet().getEdges()) {
			PetrinetNode resultSource = input2result.get(edge.getSource());
			PetrinetNode resultTarget = input2result.get(edge.getTarget());
			if (resultSource instanceof Place) {
				result.addArc((Place) resultSource, (Transition) resultTarget);
			} else {
				result.addArc((Transition) resultSource, (Place) resultTarget);
			}
		}

		return result;
	}
}