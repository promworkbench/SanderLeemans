package p2015sosym;

import java.util.Random;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import p2015sosym.efficienttree.generatebehaviour.GenerateLog;

public class MeasureAverageTraceLength {
	@Plugin(name = "Measure average trace length", returnLabels = { "average trace lengths" }, returnTypes = { HTMLToString.class }, parameterLabels = { "Efficient tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Compare process trees by hashes, default", requiredParameterLabels = { 0 })
	public HTMLToString measure(PluginContext context, final EfficientTree tree) throws Exception {
		return measure(tree);
	}
	
	public static HTMLToString measure(EfficientTree tree) throws Exception {

		final int powerOfTen = 4;
		final long randomSeed = 0;
		
		final long[] numberOfEvents = new long[powerOfTen + 1];
		long events = 0;
		Random randomGenerator = new Random(randomSeed);
		int trace = 0;
		int power = 0;
		while (power <= powerOfTen) {
			while (trace < Math.pow(10, power)) {
				events += GenerateLog.generateTrace(tree, 0, randomGenerator, false).length;
				trace++;
			}
			numberOfEvents[power] = events;
			System.out.println(trace);
			power++;
		}

		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				StringBuilder result = new StringBuilder();
				result.append("<html>");
				result.append("<table>");
				result.append("<tr><td>#traces</td><td>#events</td></tr>");
				for (int p = 0; p <= powerOfTen; p++) {
					result.append("<tr>");
					result.append("<td>");
					result.append((long) Math.pow(10, p));
					result.append("</td>");
					result.append("<td>");
					result.append(numberOfEvents[p]);
					result.append("</td>");
					result.append("</tr>");
				}
				result.append("</table>");
				result.append("</html>");
				return result.toString();
			}
		};
	}
}
