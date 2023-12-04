package p2015sosym;

import gnu.trove.set.hash.THashSet;

import java.util.Set;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.mining.interleaved.Interleaved;
import org.processmining.processtree.Block;
import org.processmining.processtree.Block.And;
import org.processmining.processtree.Block.Def;
import org.processmining.processtree.Block.DefLoop;
import org.processmining.processtree.Block.Seq;
import org.processmining.processtree.Block.Xor;
import org.processmining.processtree.Block.XorLoop;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.Task.Automatic;
import org.processmining.processtree.Task.Manual;

public class TreeStatistics {

	@Plugin(name = "Get process tree statistics", returnLabels = { "process tree statistics" }, returnTypes = { HTMLToString.class }, parameterLabels = { "Process Tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Compare process trees by hashes, default", requiredParameterLabels = { 0 })
	public HTMLToString getStatistics(PluginContext context, final ProcessTree tree) {
		numberOfTaus = 0;
		numberOfActivities = 0;
		numberOfLoops = 0;
		numberOfXors = 0;
		numberOfConcurrents = 0;
		numberOfSequences = 0;
		numberOfInterleaveds = 0;
		name = new StringBuilder();
		traverse(tree.getRoot());

		final Set<String> startActivities = getStartActivities(tree.getRoot());
		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return "<html>" + "Not counting loop exits." + "<table>" + "<tr><td>taus</td><td>" + numberOfTaus
						+ "</td></tr>" + "<tr><td>activities</td><td>" + numberOfActivities + "</td></tr>"
						+ "<tr><td>start activities</td><td>" + startActivities.size() + "</td></tr>"
						+ "<tr><td>xor</td><td>" + numberOfXors + "</td></tr>" + "<tr><td>sequence</td><td>"
						+ numberOfSequences + "</td></tr>" + "<tr><td>loop</td><td>" + numberOfLoops + "</td></tr>"
						+ "<tr><td>concurrency</td><td>" + numberOfConcurrents + "</td></tr>"
						+ "<tr><td>interleaved</td><td>" + numberOfInterleaveds + "</td></tr>" + "</table>"
						+ name.toString() + "</html>";
			}
		};
	}

	private int numberOfTaus;
	private int numberOfActivities;
	private int numberOfLoops;
	private int numberOfXors;
	private int numberOfConcurrents;
	private int numberOfSequences;
	private int numberOfInterleaveds;
	private StringBuilder name;

	private void traverse(Node node) {
		if (node instanceof Automatic) {
			name.append("\\tau");
			numberOfTaus++;
		} else if (node instanceof Manual) {
			name.append(node.getName());
			numberOfActivities++;
		} else if (node instanceof XorLoop || node instanceof DefLoop) {
			numberOfLoops++;
			name.append("\\loopOp(");
			traverse(((Block) node).getChildren().get(0));
			name.append(",\\allowbreak<br>\n\n");
			traverse(((Block) node).getChildren().get(1));
			name.append(")\\allowbreak <br>\n\n");
		} else if (node instanceof Block) {
			if (node instanceof Xor || node instanceof Def) {
				name.append("\\xorOp(");
				numberOfXors++;
			} else if (node instanceof Interleaved) {
				name.append("\\interleavedOp(");
				numberOfInterleaveds++;
			} else if (node instanceof And) {
				name.append("\\concurrentOp(");
				numberOfConcurrents++;
			} else if (node instanceof Seq) {
				name.append("\\sequenceOp(");
				numberOfSequences++;
			}
			for (int i = 0; i < ((Block) node).getChildren().size(); i++) {
				traverse(((Block) node).getChildren().get(i));
				if (i < ((Block) node).getChildren().size() - 1) {
					name.append(",\\allowbreak<br>\n\n");
				}
			}
			name.append(")\\allowbreak<br>\n\n");
		}
	}

	public static Set<String> getStartActivities(Node node) {
		if (node instanceof Automatic) {
			return new THashSet<>();
		} else if (node instanceof Manual) {
			Set<String> result = new THashSet<>();
			result.add(node.getName());
			return result;
		} else if (node instanceof Def || node instanceof Xor || node instanceof And || node instanceof Interleaved) {
			Set<String> result = new THashSet<>();
			for (Node child : ((Block) node).getChildren()) {
				result.addAll(getStartActivities(child));
			}
			return result;
		} else if (node instanceof Seq) {
			Set<String> result = new THashSet<>();
			for (Node child : ((Seq) node).getChildren()) {
				result.addAll(getStartActivities(child));
				return result;
			}
			return result;
		} else if (node instanceof DefLoop || node instanceof XorLoop) {
			return getStartActivities(((Block) node).getChildren().get(0));
		}
		return null;
	}
}
