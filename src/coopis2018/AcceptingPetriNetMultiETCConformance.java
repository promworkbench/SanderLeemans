package coopis2018;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.multietc.automaton.Automaton;
import org.processmining.plugins.multietc.automaton.AutomatonFactory;
import org.processmining.plugins.multietc.automaton.AutomatonNode;
import org.processmining.plugins.multietc.reflected.ReflectedLog;
import org.processmining.plugins.multietc.reflected.ReflectedTrace;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.multietc.sett.MultiETCSettings;
import org.processmining.plugins.petrinet.replayer.matchinstances.InfoObjectConst;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * 
 * Ported MultiETConformance code from Petri net class to Accepting Petri Net
 * class, also removed dependency on the PluginContext
 * 
 * @author Niek Tax
 */
public class AcceptingPetriNetMultiETCConformance {

	public Object[] checkMultiETCAlign1(XLog log, AcceptingPetriNet net, MultiETCSettings sett, PNRepResult repResult) {
		//Create a Reflected Log from a 1-Alignment 
		ReflectedLog refLog = new ReflectedLog();

		for (SyncReplayResult rep : repResult) {
			ReflectedTrace t = new ReflectedTrace();

			//Check the Alignments that are not Movements on the Log only
			Iterator<Object> itTask = rep.getNodeInstance().iterator();
			Iterator<StepTypes> itType = rep.getStepTypes().iterator();
			while (itTask.hasNext()) {

				StepTypes type = itType.next();

				//If it is a log move, just skip
				if (type == StepTypes.L) {
					itTask.next();//Skip the task
				}

				else { //It is a PetriNet Transition
					Transition trans = ((Transition) itTask.next());
					t.add(trans);
				}
			}

			//Avoid adding empty traces
			if (!t.isEmpty()) {
				//Compute Weight: num of cases represented by the alignment
				int cases = rep.getTraceIndex().size();
				t.putWeight(cases);
				//Add trace
				refLog.add(t);
			}
		}

		return checkMultiETC(refLog, net, sett);
	}

	@SuppressWarnings("unchecked")
	public Object[] checkMultiETCAlignAll(XLog log, AcceptingPetriNet net, MultiETCSettings sett,
			PNMatchInstancesRepResult allAlignments) {
		ReflectedLog refLog = new ReflectedLog();

		for (AllSyncReplayResult caseAlignments : allAlignments) {

			//Check if Sample or Not
			List<Integer> sampleReps = null;
			if (caseAlignments.getInfoObject() != null) {
				if (caseAlignments.getInfoObject().get(InfoObjectConst.NUMREPRESENTEDALIGNMENT) != null) {
					sampleReps = (List<Integer>) caseAlignments.getInfoObject()
							.get(InfoObjectConst.NUMREPRESENTEDALIGNMENT);
				}
			}

			//Compute the number of alignments (if sampling, the ones represented by the samples)
			int nAlign = 0;
			for (int i = 0; i < caseAlignments.getNodeInstanceLst().size(); i++) {
				//Sample
				if (sampleReps != null) {
					nAlign += sampleReps.get(i);
				}
				//Not Sample
				else {
					nAlign++;
				}
			}

			//Compute the increment of weight assigned to each alignment
			int nCases = caseAlignments.getTraceIndex().size();
			double weightPerAlign = (double) nCases / (double) nAlign;

			//For each alignment in this set
			for (int i = 0; i < caseAlignments.getNodeInstanceLst().size(); i++) {
				ReflectedTrace t = new ReflectedTrace();

				//Check the Alignments that are not Movements on the Log only
				Iterator<Object> itTask = caseAlignments.getNodeInstanceLst().get(i).iterator();
				Iterator<StepTypes> itType = caseAlignments.getStepTypesLst().get(i).iterator();
				while (itTask.hasNext()) {
					StepTypes type = itType.next();

					//If it is a log move, just skip
					if (type == StepTypes.L) {
						itTask.next();//Skip the task
					}

					else { //It is a PetriNet Transition
						Transition trans = ((Transition) itTask.next());
						t.add(trans);
					}
				}

				//Avoid adding empty traces
				if (!t.isEmpty()) {
					//SetWeight: num of Cases / num Alignments found for those cases
					int represented = (sampleReps != null) ? sampleReps.get(i) : 1;
					t.putWeight(represented * weightPerAlign);
					//Add trace
					refLog.add(t);
				}
			}
		}
		return checkMultiETC(refLog, net, sett);
	}

	public Object[] checkMultiETCEtc(XLog log, AcceptingPetriNet net, MultiETCSettings sett,
			Map<XEventClass, Transition> mapping, XEventClassifier classifier) {
		// Create the Reflected log from the Event Log (ASSUMING NO INVISIBLE OR DUPLICATES)
		ReflectedLog refLog = new ReflectedLog();

		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);

		for (XTrace trace : log) {
			//Weight of the trace
			ReflectedTrace refTrace = new ReflectedTrace();
			refTrace.putWeight(1);
			refLog.add(refTrace);

			for (XEvent event : trace) {
				XEventClass label = logInfo.getEventClasses().getClassOf(event);
				Transition trans = mapping.get(label);
				refTrace.add(trans);
			}
		}

		return checkMultiETC(refLog, net, sett);
	}

	public Object[] checkMultiETC(ReflectedLog refLog, AcceptingPetriNet net, MultiETCSettings sett) {
		Object[] forwards = checkMultiETCForwards(refLog, net, sett);
		MultiETCResult resFor = (MultiETCResult) forwards[0];
		Automaton autoFor = (Automaton) forwards[1];

		Object[] backwards = checkMultiETCBackwards(refLog, net, sett);
		MultiETCResult resBack = (MultiETCResult) backwards[0];
		Automaton autoBack = (Automaton) backwards[1];

		//Merge the results of the backwards conformance checking with the forwards ones
		mergeForwardsBackwardsResults(resFor, resBack);

		return new Object[] { resFor, autoFor, autoBack };
	}

	private void mergeForwardsBackwardsResults(MultiETCResult resFor, MultiETCResult resBack) {

		resFor.putAttribute(MultiETCResult.AUTO_STATES_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES));
		resFor.putAttribute(MultiETCResult.AUTO_STATES_IN_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES_IN));
		resFor.putAttribute(MultiETCResult.AUTO_STATES_OUT_BACK, resBack.getAttribute(MultiETCResult.AUTO_STATES_OUT));

		resFor.putAttribute(MultiETCResult.BACK_PRECISION, resBack.getAttribute(MultiETCResult.PRECISION));

		double balanced = ((Double) resFor.getAttribute(MultiETCResult.PRECISION)
				+ (Double) resBack.getAttribute(MultiETCResult.PRECISION)) / 2;
		resFor.putAttribute(MultiETCResult.BALANCED_PRECISION, balanced);
	}

	public Object[] checkMultiETCForwards(ReflectedLog refLog, AcceptingPetriNet net, MultiETCSettings sett) {
		//Force Forward automaton
		sett.setWindow(MultiETCSettings.Window.BACKWARDS);

		return checkMultiETC(refLog, net.getNet(), net.getInitialMarking(),
				net.getFinalMarkings().toArray(new Marking[net.getFinalMarkings().size()])[0], sett);
	}

	public Object[] checkMultiETCBackwards(ReflectedLog refLog, AcceptingPetriNet net, MultiETCSettings sett) {

		//Force Backward automaton
		sett.setWindow(MultiETCSettings.Window.FORWARDS);

		return checkMultiETC(refLog, net.getNet(), net.getInitialMarking(),
				net.getFinalMarkings().toArray(new Marking[net.getFinalMarkings().size()])[0], sett);
	}

	public Object[] checkMultiETC(ReflectedLog log, Petrinet net, Marking iniM, Marking endM,
			MultiETCSettings etcSett) {

		AutomatonFactory factory = new AutomatonFactory(etcSett);
		Automaton a = factory.createAutomaton();
		MultiETCResult res = new MultiETCResult();
		a.checkConformance(log, net, iniM, endM, res, etcSett);

		setSettingsInfoInResult(etcSett, res);
		setAutomatonInfoInResult(a, res);

		return new Object[] { res, a };
	}

	private void setAutomatonInfoInResult(Automaton a, MultiETCResult res) {
		int states = 0;
		int in = 0;
		int out = 0;
		for (AutomatonNode n : a.getJUNG().getVertices()) {
			states++;
			if (n.getMarking() == null)
				out++;
			else
				in++;
		}
		res.putAttribute(MultiETCResult.AUTO_STATES, states);
		res.putAttribute(MultiETCResult.AUTO_STATES_IN, in);
		res.putAttribute(MultiETCResult.AUTO_STATES_OUT, out);

	}

	private void setSettingsInfoInResult(MultiETCSettings etcSett, MultiETCResult res) {
		res.putAttribute(MultiETCResult.ETC_SETT, etcSett);
	}
}
