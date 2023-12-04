package thesis.evaluation.rediscoverability;

import java.io.File;
import java.util.Random;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.ptml.importing.PtmlImportTree;

import p2015sosym.efficienttree.generatebehaviour.GenerateLog;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationConformance;
import thesis.helperClasses.XLogWriterIncremental;

public class IncompletenessGenerateLogs {

	public static void main(String[] args) throws Exception {

		for (File modelFile : RediscoverabilitySettings.generatedModelDirectory.listFiles()) {

			if (modelFile.getName().endsWith(".ptml")) {
				ProcessTree ptree = (ProcessTree) (new PtmlImportTree()).importFile(null, modelFile);
				EfficientTree tree = ProcessTree2EfficientTree.convert(ptree);
				EfficientTreeExportPlugin.export(tree, new File(RediscoverabilitySettings.generatedModelDirectory,
						modelFile.getName() + ".tree"));
				continue;
			}

			EfficientTree tree = kFoldCrossValidationConformance.loadEfficientTree(modelFile);

			for (int round = 1; round <= RediscoverabilitySettings.logRounds; round++) {
				File logFile = new File(RediscoverabilitySettings.logDirectory, modelFile.getName() + "-logRound"
						+ round + ".xes.gz");

				if (!logFile.exists()) {
					long logSize = (long) Math.pow(RediscoverabilitySettings.logIncreaseFactor, round);
					Random random = new Random(1);
					XLogWriterIncremental logWriter = new XLogWriterIncremental(logFile);
					for (int[] trace : GenerateLog.generateTraces(tree, logSize, random, false)) {
						logWriter.writeTrace(trace, tree.getInt2activity());
					}
					logWriter.close();
				}
			}
		}
	}
}
