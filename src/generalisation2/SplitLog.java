package generalisation2;

import java.util.BitSet;
import java.util.Random;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Pair;

public class SplitLog {

	@Plugin(name = "Split log into training and test logs", returnLabels = { "Training Log", "Test Log" }, returnTypes = {
			XLog.class, XLog.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public Object[] mineGuiProcessTree(UIPluginContext context, XLog log) {
		
		SplitLogDialog dialog = new SplitLogDialog();
		InteractionResult result = context.showWizard("Split log in training and test logs", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		
		Pair<XLog, XLog> p = splitLog(log, dialog.getValue());
		
		return new Object[]{p.getLeft(), p.getRight()};
	}
	
	public static Pair<XLog, XLog> splitLog(XLog log, float part) {
		
		if (part > 1 || 0 > part) {
			return null;
		}
		Random r = new Random();
		BitSet list = new BitSet(log.size());
		
		int i = 0;
		while (i < log.size() * part) {
			int j = r.nextInt(log.size());
			if (!list.get(j)) {
				list.set(j);
				i++;
			}
		}
		
		XLog trainingSet = XFactoryRegistry.instance().currentDefault().createLog();
		XLog testSet = XFactoryRegistry.instance().currentDefault().createLog();
		int j = 0;
		for (XTrace trace : log) {
			if (list.get(j)) {
				testSet.add(trace);
			} else {
				trainingSet.add(trace);
			}
			j++;
		}
		
		return Pair.of(trainingSet, testSet);
	}
}
