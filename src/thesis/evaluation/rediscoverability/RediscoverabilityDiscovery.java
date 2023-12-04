package thesis.evaluation.rediscoverability;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;

import thesis.evaluation.rediscoverability.RediscoverabilitySettings.Algorithm;
import thesis.evaluation.standAloneMiners.RunInductiveMiner;

public class RediscoverabilityDiscovery {
	public static void main(String[] args) throws Exception {
		System.out.println(RediscoverabilitySettings.logDirectory);
		for (File logFile : RediscoverabilitySettings.logDirectory.listFiles()) {
			if (1 == 1 || logFile.getName().contains("logRound4") && logFile.getName().contains("treeSeed 7")) {
				System.out.println("loading log " + logFile);

				XLog log = RunInductiveMiner.loadLog(logFile);
				System.out.println(" log loaded");

				for (Algorithm algorithm : RediscoverabilitySettings.algorithms) {
					if (1 == 2 || algorithm == Algorithm.ima) {
						//IM variant
						File outFile = new File(RediscoverabilitySettings.discoveredModelDirectory,
								logFile.getName() + "-" + algorithm.name() + ".tree");
						if (!outFile.exists()) {
							System.out.println(" running " + algorithm.name());
							EfficientTree tree = algorithm.mine(log);
							System.out.println("  writing result " + outFile);
							EfficientTreeExportPlugin.export(tree, outFile);
						}
					}
				}
			}
		}
		System.out.println("done");
	}
}
