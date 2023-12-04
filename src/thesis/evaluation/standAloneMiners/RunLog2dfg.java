package thesis.evaluation.standAloneMiners;

import gnu.trove.map.hash.THashMap;

import java.io.File;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgImpl;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.DfgExportPlugin;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

import thesis.helperClasses.XLogParserIncremental;

public class RunLog2dfg {

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
			System.out.println("Usage: Log2dfg.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}
		
		DfgExportPlugin.export(loadLogAsDfg(file), new File(file.getName() + ".dfg"));
	}

	public static Dfg loadLogAsDfg(File logFile) throws Exception { 
		final Activities activities = new Activities(MiningParameters.getDefaultClassifier());
		final Dfg dfg = new DfgImpl();
		final State state = new State();

		XLogParserIncremental.parseEvents(logFile, new Function<XEvent, Object>() {
			public Object call(XEvent input) throws Exception {
				XEventClass activity = activities.getActivity(input);
				if (state.lastEventClass == null) {
					dfg.addStartActivity(activity, 1);
				} else {
					dfg.addDirectlyFollowsEdge(state.lastEventClass, activity, 1);
				}
				state.lastEventClass = activity;
				return null;
			}
		}, new Runnable() {
			//we finished a trace
			public void run() {
				if (state.lastEventClass == null) {
					dfg.addEmptyTraces(1);
				} else {
					dfg.addEndActivity(state.lastEventClass, 1);
					dfg.addActivity(state.lastEventClass);
				}
				state.lastEventClass = null;
			}
		});
		
		return dfg;
	}

	public static class Activities {
		private final XEventClassifier classifier;
		private final THashMap<String, XEventClass> activity2eventClass;

		public Activities(XEventClassifier classifier) {
			this.classifier = classifier;
			activity2eventClass = new THashMap<>();
		}

		public XEventClass getActivity(XEvent event) {
			String activity = classifier.getClassIdentity(event);
			XEventClass newClass = new XEventClass(activity, activity2eventClass.size());
			XEventClass alreadyPresent = activity2eventClass.putIfAbsent(activity, newClass);
			if (alreadyPresent != null) {
				return alreadyPresent;
			} else {
				return newClass;
			}
		}
	}

	private static class State {
		XEventClass lastEventClass = null;
	}
}
