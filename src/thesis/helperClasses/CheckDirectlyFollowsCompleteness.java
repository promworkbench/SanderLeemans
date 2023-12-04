package thesis.helperClasses;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.processtree.ProcessTree;

import p2015sosym.helperclasses.GenerateCompleteDfg;

@Plugin(name = "Assess completeness", returnLabels = { "Completeness result" }, returnTypes = {
		HTMLToString.class }, parameterLabels = { "Process tree", "log", "Efficient tree" }, userAccessible = true)
public class CheckDirectlyFollowsCompleteness {

	@PluginVariant(variantLabel = "Assess completeness", requiredParameterLabels = { 0, 1 })
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	public HTMLToString check(PluginContext context, final ProcessTree tree, final XLog log)
			throws UnknownTreeNodeException {
		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return "" + isDirectlyFollowsComplete(log, ProcessTree2EfficientTree.convert(tree));
			}
		};
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Assess completeness2", requiredParameterLabels = { 2, 1 })
	public HTMLToString check(PluginContext context, final EfficientTree tree, final XLog log)
			throws UnknownTreeNodeException {
		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				return "" + isDirectlyFollowsComplete(log, tree);
			}
		};
	}

	public boolean isDirectlyFollowsComplete(XLog log, EfficientTree tree) {
		Dfg dfgTree = GenerateCompleteDfg.tree2dfg(tree, tree.getRoot());

		Dfg dfgLog = IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(log, MiningParameters.getDefaultClassifier(),
				MiningParameters.getDefaultLifeCycleClassifier())).getDfg();

		return covers(dfgLog, dfgTree) && covers(dfgTree, dfgLog);
	}

	public boolean covers(Dfg a, Dfg b) {
		for (long edge : a.getDirectlyFollowsEdges()) {
			XEventClass source = a.getDirectlyFollowsEdgeSource(edge);
			XEventClass target = a.getDirectlyFollowsEdgeTarget(edge);
			if (!b.containsDirectlyFollowsEdge(source, target)) {
				return false;
			}
		}

		for (XEventClass activity : a.getStartActivities()) {
			if (b.getStartActivityCardinality(activity) == 0) {
				return false;
			}
		}

		for (XEventClass activity : a.getEndActivities()) {
			if (b.getEndActivityCardinality(activity) == 0) {
				return false;
			}
		}

		return true;
	}
}
