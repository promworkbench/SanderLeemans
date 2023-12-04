package p2015sosym;

import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import gnu.trove.set.hash.THashSet;

public class DfgStatistics {
	@Plugin(name = "Get directly-follows graph statistics", returnLabels = { "directly-follows graph statistics" }, returnTypes = { HTMLToString.class }, parameterLabels = { "Directly-follows graph" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Compare process trees by hashes, default", requiredParameterLabels = { 0 })
	public HTMLToString getStatistics(PluginContext context, final Dfg dfg) {

		final long traces = dfg.getNumberOfStartActivities();

		final Set<XEventClass> activities = new THashSet<>(ImmutableSet.copyOf(dfg.getActivities()));

		long events = traces;
		long distinctEdges = 0;
		for (long edgeIndex : dfg.getDirectlyFollowsGraph().getEdges()) {
			events += dfg.getDirectlyFollowsGraph().getEdgeWeight(edgeIndex);
			activities.add(dfg.getDirectlyFollowsGraph().getEdgeSource(edgeIndex));
			activities.add(dfg.getDirectlyFollowsGraph().getEdgeTarget(edgeIndex));
			distinctEdges++;
		}
		final long events2 = events;
		final long distinctEdges2 = distinctEdges;

		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return "<html>" + "<table><tr><td>traces</td><td>" + traces
						+ "</td></tr><tr><td>start activities</td><td>" + dfg.getNumberOfStartActivities()
						+ "</td></tr><tr><td></td><td>" + Joiner.on(", ").join(dfg.getStartActivities())
						+ "</td></tr><tr><td>end activities</td><td>" + dfg.getNumberOfEndActivities()
						+ "</td></tr><tr><td></td><td>" + Joiner.on(", ").join(dfg.getEndActivities())
						+ "</td></tr><tr><td>distinct edges</td><td>" + distinctEdges2
						+ "</td></tr><tr><td>events</td><td>" + events2 + "</td></tr><tr><td>activities</td><td>"
						+ activities.size() + "</td></tr></table>" + "</html>";
			}
		};
	}
}
