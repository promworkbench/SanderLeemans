import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.attributes.AttributeUtils;

public class MakeTracesRelative {
	@Plugin(name = "Make traces relative", returnLabels = { "Log with relative timestamps" }, returnTypes = {
			XLog.class }, parameterLabels = {
					"Log" }, userAccessible = true, level = PluginLevel.Regular, categories = {
							PluginCategory.Enhancement }, help = "Make every trace start at the same time.")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Align efficient tree, default", requiredParameterLabels = { 0 })
	public XLog align(final UIPluginContext context, XLog log) throws Exception {
		XFactoryNaiveImpl factory = new XFactoryNaiveImpl();
		XLog result = factory.createLog();
		result.setAttributes((XAttributeMap) log.getAttributes().clone());

		for (XTrace trace : log) {
			XTrace newTrace = factory.createTrace();
			newTrace.setAttributes((XAttributeMap) newTrace.getAttributes().clone());

			long time = 0;
			for (XEvent event : trace) {
				XEvent newEvent = factory.createEvent((XAttributeMap) event.getAttributes().clone());

				XAttribute t = event.getAttributes().get("time:timestamp");
				if (t != null && t instanceof XAttributeTimestamp) {
					long eventTime = AttributeUtils.parseTimeFast(t);

					if (time < 0) {
						time = eventTime;
					}

					eventTime = eventTime - time;

					newEvent.getAttributes().put("time:timestamp", newEvent.getAttributes().get("time:timestamp"));
				}
				
				newTrace.add(newEvent);
			}
			result.add(newTrace);
		}

		return result;
	}
}
