package generation;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.cuts.ExhaustiveKSuccessor.UpToKSuccessor;
import org.processmining.plugins.InductiveMiner.mining.cuts.ExhaustiveKSuccessor.UpToKSuccessorMatrix;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.processtree.ProcessTree;

public class Entropy implements HTMLToString {

	@Plugin(name = "Assess completeness", returnLabels = { "Entropy result" }, returnTypes = {
			EntropyResult.class }, parameterLabels = { "Process tree", "log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Assess completeness", requiredParameterLabels = { 0, 1 })
	public EntropyResult check(PluginContext context, ProcessTree tree, XLog log) throws UnknownTreeNodeException {
		return check(tree, log);
	}

	public static EntropyResult check(ProcessTree tree, XLog log) throws UnknownTreeNodeException {

		IMLog fLog = new IMLogImpl(log, MiningParameters.getDefaultClassifier(),
				MiningParameters.getDefaultLifeCycleClassifier());
		UpToKSuccessorMatrix mLog = UpToKSuccessor.fromLog(fLog, (new IMLog2IMLogInfoDefault()).createLogInfo(fLog));

		UpToKSuccessorMatrix mModel = UpToKSuccessor.fromNode(tree.getRoot());

		EntropyResult result = new EntropyResult();

		for (String a : mModel.getActivities()) {
			for (String b : mModel.getActivities()) {
				if (a != b) {

					if (isOne(mModel.getKSuccessor(a, b))) {
						result.dfgEdgesInModel++;

						if (!isOne(mLog.getKSuccessor(a, b))) {
							result.missingDfgEdges.add(new Pair<String, String>(a, b));
						}
					}

					if (mModel.getKSuccessor(a, b) != null) {
						result.efgEdgesInModel++;

						if (mLog.getKSuccessor(a, b) == null) {
							result.missingEfgEdges.add(new Pair<String, String>(a, b));
						}
					}

					if (!isBigger(mModel.getKSuccessor(a, a), mLog.getKSuccessor(a, a))) {
						result.missingMsdEdges.add(new Pair<String, String>(a, a));
					}

				}
			}
		}

		return result;
	}

	private static boolean isOne(Integer i) {
		if (i == null) {
			return false;
		} else if (i == 1) {
			return true;
		}
		return false;
	}

	private static boolean isBigger(Integer m, Integer l) {
		if (m == null && l == null) {
			return true;
		} else if (m == null) {
			return true;
		} else if (l == null) {
			return false;
		} else {
			return m >= l;
		}
	}

	public String toHTMLString(boolean includeHTMLTags) {
		return toString();
	}
}
