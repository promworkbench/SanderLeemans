package thesis.evaluation.logconformancewithoutkfoldcrossvalidation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.bpmn.plugins.BpmnExportPlugin;

import com.raffaeleconforti.context.FakePluginContext;

import thesis.evaluation.rediscoverability.RediscoverabilitySettings;
import thesis.evaluation.rediscoverability.RediscoverabilitySettings.Algorithm;
import thesis.evaluation.standAloneMiners.RunAlphaMiner;
import thesis.evaluation.standAloneMiners.RunEvolutionaryTreeMiner;
import thesis.evaluation.standAloneMiners.RunFodina;
import thesis.evaluation.standAloneMiners.RunHeuristicsMiner;
import thesis.evaluation.standAloneMiners.RunILP;
import thesis.evaluation.standAloneMiners.RunInductiveMiner;
import thesis.evaluation.standAloneMiners.RunStructuredMiner;
import thesis.evaluation.standAloneMiners.RunTsinghuaAlphaMiner;
import thesis.helperClasses.FakeContext;

public class LogConformanceDiscovery {
	public static void main(String[] args) throws Exception {
		System.out.println(LogConformanceSettings.logDirectory);

		for (File logFile : LogConformanceSettings.logDirectory.listFiles()) {
			if (1 == 1 || !logFile.getName().contains("hospital_log")) {
				System.out.println("loading log " + logFile);

				XLog log = RunInductiveMiner.loadLog(logFile);
				System.out.println(" log loaded");

				//IM variant
				if (1 == 1) {
					for (Algorithm algorithm : RediscoverabilitySettings.algorithms) {
						if (algorithm == Algorithm.ima) {

							File outFile = new File(LogConformanceSettings.discoveredModelDirectory,
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

				//ETM
				if (1 == 2) {
					File outFile = new File(LogConformanceSettings.discoveredModelDirectory,
							logFile.getName() + "-etm.tree");
					if (!outFile.exists()) {
						System.out.println("  running ETM");
						EfficientTree tree = RunEvolutionaryTreeMiner.mine(log, 1);
						System.out.println("  writing result " + outFile);
						EfficientTreeExportPlugin.export(tree, outFile);
					}
				}

				//fodina
				if (1 == 2) {
					File outFile = new File(LogConformanceSettings.discoveredModelDirectory,
							logFile.getName() + "-fo.pnml");
					if (!outFile.exists()) {
						AcceptingPetriNet net = RunFodina.mine(log);
						net.exportToFile(new FakeContext(), outFile);
					}
				}

				//alpha
				if (1 == 2) {
					File outFile = new File(LogConformanceSettings.discoveredModelDirectory,
							logFile.getName() + "-alpha.pnml");
					if (!outFile.exists()) {
						System.out.println("running alpha");
						AcceptingPetriNet net = RunAlphaMiner.mine(log);
						net.exportToFile(new FakeContext(), outFile);
					}
				}

				//HM
				if (1 == 2) {
					File outFile = new File(LogConformanceSettings.discoveredModelDirectory,
							logFile.getName() + "-hm.pnml");
					if (!outFile.exists()) {
						System.out.println("running HM");
						AcceptingPetriNet net = RunHeuristicsMiner.mine(log);
						net.exportToFile(new FakeContext(), outFile);
					}
				}

				//structured miner
				if (1 == 2) {
					File outFile = new File(LogConformanceSettings.discoveredModelDirectory,
							logFile.getName() + "-sm.bpmn");
					if (!outFile.exists()) {
						try {
							BPMNDiagram diagram = RunStructuredMiner.mine(logFile);
							new BpmnExportPlugin().export(new FakePluginContext(), diagram, outFile);
						} catch (Exception e) {
							FileWriter writer = new FileWriter(outFile);
							writer.write("error\n");
							e.printStackTrace(new PrintWriter(writer, true));
							writer.close();
						}
					}
				}

				//ILP
				if (1 == 2) {
					File outFile = new File(LogConformanceSettings.discoveredModelDirectory,
							logFile.getName() + "-ilp.pnml");
					if (!outFile.exists()) {
						System.out.println("  running ILP");
						AcceptingPetriNet net = RunILP.mine(log);
						System.out.println("  writing result " + outFile);
						net.exportToFile(new FakeContext(), outFile);
					}
				}
				
				//TsinghuaAlpha
				if (1 == 2) {
					File outFile = new File(LogConformanceSettings.discoveredModelDirectory, logFile.getName() + "-talpha.pnml");
					if (!outFile.exists()) {
						System.out.println("running tAlpha");
						AcceptingPetriNet net = RunTsinghuaAlphaMiner.mine(log);
						net.exportToFile(new FakeContext(), outFile);
					}
				}
			}
		}
	}
}
