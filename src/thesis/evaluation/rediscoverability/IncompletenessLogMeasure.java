package thesis.evaluation.rediscoverability;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;

import p2015sosym.helperclasses.GenerateCompleteDfg;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationConformance;
import thesis.evaluation.standAloneMiners.RunInductiveMiner;

public class IncompletenessLogMeasure {
	public static void main(String[] args) throws Exception {

		PrintWriter resultsFile = new PrintWriter(
				new File(RediscoverabilitySettings.baseDirectory, "results-logCompleteness.txt"), "UTF-8");
		resultsFile.print("traces\tlog-completeness\tactivity-completeness");

		for (int round = 1; round <= RediscoverabilitySettings.logRounds; round++) {
			double sumDfg = 0;
			double sumAct = 0;
			for (File modelFile : RediscoverabilitySettings.generatedModelDirectory.listFiles()) {
				File logFile = new File(RediscoverabilitySettings.logDirectory,
						modelFile.getName() + "-logRound" + round + ".xes.gz");

				EfficientTree tree = kFoldCrossValidationConformance.loadEfficientTree(modelFile);

				Dfg dfgTree = GenerateCompleteDfg.tree2dfg(tree, tree.getRoot());

				Dfg dfgLog = IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(RunInductiveMiner.loadLog(logFile),
						MiningParameters.getDefaultClassifier(), MiningParameters.getDefaultLifeCycleClassifier()))
						.getDfg();

				sumDfg += covers(dfgTree, dfgLog);

				sumAct += dfgLog.getActivities().length / dfgTree.getActivities().length;
			}
			System.out.println("round " + round + " average dfg-completeness of log "
					+ sumDfg / RediscoverabilitySettings.generatedModelDirectory.listFiles().length);
			resultsFile.write("\n");
			resultsFile.write(((long) Math.pow(RediscoverabilitySettings.logIncreaseFactor, round)) + "\t");
			resultsFile.write(sumDfg / RediscoverabilitySettings.generatedModelDirectory.listFiles().length + "\t");
			resultsFile.write(sumAct / RediscoverabilitySettings.generatedModelDirectory.listFiles().length + "");
		}
		resultsFile.close();
	}

	public static double covers(Dfg a, Dfg b) {

		long count = 0;
		double correct = 0;

		for (long edge : a.getDirectlyFollowsEdges()) {
			XEventClass source = a.getDirectlyFollowsEdgeSource(edge);
			XEventClass target = a.getDirectlyFollowsEdgeTarget(edge);
			count++;
			if (b.containsDirectlyFollowsEdge(source, target)) {
				correct++;
			}
		}

		for (XEventClass activity : a.getStartActivities()) {
			count++;
			if (b.getStartActivityCardinality(activity) != 0) {
				correct++;
			}
		}

		for (XEventClass activity : a.getEndActivities()) {
			count++;
			if (b.getEndActivityCardinality(activity) != 0) {
				correct++;
			}
		}

		if (count == 0) {
			return 1;
		}

		return correct / count;
	}
}
