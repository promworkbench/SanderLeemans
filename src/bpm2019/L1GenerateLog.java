package bpm2019;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

import com.raffaeleconforti.efficientlog.XLogImpl;

public class L1GenerateLog {
	@Plugin(name = "BPM2019 L1 create log", returnLabels = { "Event log L1" }, returnTypes = {
			XLog.class }, parameterLabels = {}, userAccessible = true, help = "Convert log to stochastic deterministic finite automaton.", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = {})
	public XLog convert(final PluginContext context) {
		String[][] traces = { { "a", "b", "c", "e" }, { "a", "c", "b", "e" }, { "a", "d", "e" } };
		int[] probabilities = { 25, 25, 50 };

		XLog result = new XLogImpl(new XAttributeMapImpl());
		long time = 0;
		for (int t = 0; t < traces.length; t++) {
			int repetitions = probabilities[t];

			for (int i = 0; i < repetitions; i++) {
				XTrace trace = new XTraceImpl(new XAttributeMapImpl());
				for (String activity : traces[t]) {
					XEvent event = new XEventImpl();
					XTimeExtension.instance().assignTimestamp(event, time);
					XConceptExtension.instance().assignName(event, activity);
					trace.add(event);
					time++;
				}
				result.add(trace);
			}
		}

		return result;
	}
}
