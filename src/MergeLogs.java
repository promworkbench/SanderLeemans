import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class MergeLogs {
	@Plugin(name = "Merge event logs (copy traces)", level = PluginLevel.BulletProof, returnLabels = { "Event Log" }, returnTypes = {
			XLog.class }, parameterLabels = { "Log", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 0 })
	public XLog mineGuiProcessTree(final PluginContext context, final XLog log1, final XLog log2) {
		XFactory f = new XFactoryNaiveImpl();
		XLog result = f.createLog();

		for (XTrace trace : log1) {
			XTrace resultTrace = f.createTrace((XAttributeMap) trace.getAttributes().clone());
			for (XEvent event : trace) {
				resultTrace.add(f.createEvent((XAttributeMap) event.getAttributes().clone()));
			}
			result.add(resultTrace);
		}

		for (XTrace trace : log2) {
			XTrace resultTrace = f.createTrace((XAttributeMap) trace.getAttributes().clone());
			for (XEvent event : trace) {
				resultTrace.add(f.createEvent((XAttributeMap) event.getAttributes().clone()));
			}
			result.add(resultTrace);
		}

		return result;
	}
}
