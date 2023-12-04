package batch;

import java.io.File;
import java.util.Collection;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PrefixBasedPetrinetReplayer;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.log.OpenLogFilePlugin;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class findReplayCostEpsilon {
	
	public static Integer find(
			PluginContext context,
			File file,
			Petrinet petrinet,
			Marking initialMarking,
			Marking finalMarking,
			TransEvClassMapping mapping,
			XEventClassifier classifier,
			XLogInfo logInfo) {
		
		debug("Start replay to find cost of epsilon " + file);
		
		//load the dummy log
		XLog log;
		try {
			OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
			log = (XLog) logImporter.importFile(context, new File("D:\\output\\dummyLog.xes"));
		} catch (Exception e) {
			//debug("error encountered (log import)");
			e.printStackTrace();
			return null;
		}
		
		PNLogReplayer replayer = new PNLogReplayer();
		Collection<XEventClass> activities = logInfo.getEventClasses().getClasses();
		XEventClass dummy = mapping.getDummyEventClass();
		CostBasedCompleteParam replayParameters = new CostBasedCompleteParam(activities, dummy, petrinet.getTransitions(), 1, 10000);
		replayParameters.setInitialMarking(initialMarking);
		replayParameters.setMaxNumOfStates(Integer.MAX_VALUE);
		IPNReplayAlgorithm algorithm;		
		algorithm = new PrefixBasedPetrinetReplayer();
		replayParameters.setCreateConn(false);
		replayParameters.setGUIMode(false);
		PNRepResult replayed = null;
		try {
			replayed = replayer.replayLog(context, petrinet, log, mapping, algorithm, replayParameters);
		} catch (Error e) {
			e.printStackTrace();
			debug("&nbsp;&nbsp;&nbsp;&nbsp;Log replay failed.<br>");
			return null;
		} catch (AStarException e) {
			e.printStackTrace();
		}
		
		//debug(replayed.toString());
		//debug(replayed.getInfo().toString());
		int cost = ((Double) replayed.getInfo().get("Raw Fitness Cost")).intValue();
		cost = (cost + 2) % 10000;
		
		debug("cost of epsilon " + cost + " " + replayed.getInfo().get("Raw Fitness Cost"));
		
		return cost;
	}
	
	private static void debug(String s) {
		System.out.println(s);
	}
}
