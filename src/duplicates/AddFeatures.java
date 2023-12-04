package duplicates;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class AddFeatures {

	@Plugin(name = "Add trace features (in place)", returnLabels = { "log with trace features" }, returnTypes = {
			XLog.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public static XLog addFeatures(PluginContext context, XLog log) throws Exception {
		Pattern p = Pattern.compile("(.*)_(\\d+)\\z");

		Map<String, Map<String, TDoubleList>> doubleAttributes = new THashMap<>(); // attribute, cluster, value

		long splitEvents = 0;

		for (XTrace trace : log) {
			double length = trace.size();
			TObjectIntMap<String> occurrences = new TObjectIntHashMap<>();

			Date lastCompleteTime = null;

			int eventNr = 0;
			for (XEvent event : trace) {

				//time
				if (XLifecycleExtension.instance().extractTransition(event).equalsIgnoreCase("complete")) {
					Date newCompleteTime = XTimeExtension.instance().extractTimestamp(event);
					if (newCompleteTime != null && lastCompleteTime != null) {
						event.getAttributes().put("timeSinceLastComplete", new XAttributeDiscreteImpl(
								"timeSinceLastComplete", newCompleteTime.getTime() - lastCompleteTime.getTime()));
					}
					if (newCompleteTime != null) {
						lastCompleteTime = newCompleteTime;
					}
				}

				//has been split
				Matcher matcher = p.matcher(XConceptExtension.instance().extractName(event));
				if (matcher.matches()) {
					String name = matcher.group(1);
					String cluster = matcher.group(2);

					//trace position
					double tracePosition = eventNr / (length - 1);
					event.getAttributes().put("tracePosition",
							new XAttributeContinuousImpl("tracePosition", tracePosition));

					event.getAttributes().put("splitActivity", new XAttributeBooleanImpl("splitActivity", true));
					splitEvents += 1;

					//occurrence
					int occurrence = occurrences.adjustOrPutValue(name, 1, 0);
					event.getAttributes().put("occurrence", new XAttributeDiscreteImpl("occurrence", occurrence));

					//other data
					for (Entry<String, XAttribute> attribute : event.getAttributes().entrySet()) {
						if (attribute.getValue() instanceof XAttributeDiscrete
								|| attribute.getValue() instanceof XAttributeContinuous) {
							String attributeName = attribute.getKey();
							Map<String, TDoubleList> map = doubleAttributes.get(attributeName);
							if (map == null) {
								map = new THashMap<String, TDoubleList>();
								doubleAttributes.put(attributeName, map);
							}
							TDoubleList list = map.get(cluster);
							if (list == null) {
								list = new TDoubleArrayList();
								map.put(cluster, list);
							}
							if (attribute.getValue() instanceof XAttributeDiscrete) {
								list.add(((XAttributeDiscrete) attribute.getValue()).getValue());
							} else if (attribute.getValue() instanceof XAttributeContinuous) {
								list.add(((XAttributeContinuous) attribute.getValue()).getValue());
							}
						}
					}

				} else {
					event.getAttributes().put("splitActivity", new XAttributeBooleanImpl("splitActivity", false));
					event.getAttributes().put("occurrence", new XAttributeDiscreteImpl("occurrence",
							occurrences.adjustOrPutValue(XConceptExtension.instance().extractName(event), 1, 0)));
				}

				eventNr++;
			}
		}
		System.out.println("number of split events " + splitEvents);

		for (Entry<String, Map<String, TDoubleList>> entry : doubleAttributes.entrySet()) {
			System.out.println("f-score of " + entry.getKey() + ": " + getF(entry.getValue()));
		}

		return log;
	}

	public static double getF(Map<? extends Object, TDoubleList> map) {
		List<double[]> categoryData = new ArrayList<>();
		for (TDoubleList list : map.values()) {
			categoryData.add(list.toArray());
		}
		return new OneWayAnova().anovaFValue(categoryData);
	}
}
