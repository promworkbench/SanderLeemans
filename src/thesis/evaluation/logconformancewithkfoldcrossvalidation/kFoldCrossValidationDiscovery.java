package thesis.evaluation.logconformancewithkfoldcrossvalidation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.commons.io.FilenameUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiner;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.InductiveMiner.plugins.IMTree;
import org.processmining.plugins.bpmn.plugins.BpmnExportPlugin;
import org.processmining.plugins.flowerMiner.TauMiner;

import com.raffaeleconforti.context.FakePluginContext;

import thesis.evaluation.standAloneMiners.RunAlphaMiner;
import thesis.evaluation.standAloneMiners.RunEvolutionaryTreeMiner;
import thesis.evaluation.standAloneMiners.RunFlowerMiner;
import thesis.evaluation.standAloneMiners.RunFodina;
import thesis.evaluation.standAloneMiners.RunHeuristicsMiner;
import thesis.evaluation.standAloneMiners.RunILP;
import thesis.evaluation.standAloneMiners.RunInductiveMiner;
import thesis.evaluation.standAloneMiners.RunInductiveMiner.Variant;
import thesis.evaluation.standAloneMiners.RunInductiveMinerDirectlyFollows;
import thesis.evaluation.standAloneMiners.RunLog2dfg;
import thesis.evaluation.standAloneMiners.RunStructuredMiner;
import thesis.evaluation.standAloneMiners.RunTraceMiner;
import thesis.evaluation.standAloneMiners.RunTsinghuaAlphaMiner;
import thesis.helperClasses.FakeContext;

public class kFoldCrossValidationDiscovery {

	public static final File inputDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality\\data-discovery");
	public static final File outputDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality\\discoveredModels");
	public static final Variant algorithm = Variant.imf;
	public static final RunInductiveMinerDirectlyFollows.Variant algorithmDfg = RunInductiveMinerDirectlyFollows.Variant.imcd;

	public static void main(String[] args) throws Exception {
		for (final File file : inputDirectory.listFiles()) {
			if (1 == 1 || file.getName().contains("Road")) {
				if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("xes")
						|| FilenameUtils.getExtension(file.getAbsolutePath()).equals("gz")) {
					System.out.println("discovering " + file);

					//IM variant
					EfficientTree tree;
					File outFile;
					if (1 == 1) {
						outFile = new File(outputDirectory, file.getName() + "-" + algorithm.name() + ".tree");
						if (!outFile.exists()) {
							XLog log = RunInductiveMiner.loadLog(file);
							tree = IMTree.mineTree(log, algorithm.miningParameters);
							System.out.println("writing result " + outFile);
							EfficientTreeExportPlugin.export(tree, outFile);

							//						//hack: write error file
							//						FileWriter writer = new FileWriter(outFile);
							//						writer.write("error\n");
							//						writer.close();
						}
					}

					//IMd variant
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-" + algorithmDfg.name() + ".tree");
						if (!outFile.exists()) {
							tree = ProcessTree2EfficientTree.convert(DfgMiner.mine(RunLog2dfg.loadLogAsDfg(file),
									algorithmDfg.getParameters(), new Canceller() {
										public boolean isCancelled() {
											return false;
										}
									}));
							System.out.println("writing result " + outFile);
							EfficientTreeExportPlugin.export(tree, outFile);

							//						//hack: write error file
							//						FileWriter writer = new FileWriter(outFile);
							//						writer.write("error\n");
							//						writer.close();
						}
					}

					//ETM
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-etm.tree");
						if (!outFile.exists()) {
							XLog log = RunInductiveMiner.loadLog(file);
							System.out.println("running ETM");

							EfficientTree tree2 = RunEvolutionaryTreeMiner.mine(log, 1);
							EfficientTreeExportPlugin.export(tree2, outFile);
						}
					}

					//alpha
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-alpha.pnml");
						if (!outFile.exists()) {
							XLog log = RunInductiveMiner.loadLog(file);
							System.out.println("running alpha");
							AcceptingPetriNet net = RunAlphaMiner.mine(log);
							net.exportToFile(new FakeContext(), outFile);
						}
					}

					//TsinghuaAlpha
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-talpha.pnml");
						if (!outFile.exists()) {
							XLog log = RunInductiveMiner.loadLog(file);
							System.out.println("running tAlpha");
							AcceptingPetriNet net = RunTsinghuaAlphaMiner.mine(log);
							net.exportToFile(new FakeContext(), outFile);
						}
					}

					//ILP
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-ilp.pnml");
						if (!outFile.exists()) {
							XLog log = RunInductiveMiner.loadLog(file);
							System.out.println(" running ILP");
							AcceptingPetriNet net = RunILP.mine(log);
							net.exportToFile(new FakeContext(), outFile);
						}
					}

					//HM
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-hm.pnml");
						if (!outFile.exists()) {
							XLog log = RunInductiveMiner.loadLog(file);
							System.out.println("running HM");
							AcceptingPetriNet net = RunHeuristicsMiner.mine(log);
							net.exportToFile(new FakeContext(), outFile);
						}
					}

					//fodina
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-fo.pnml");
						if (!outFile.exists()) {
							XLog log = RunInductiveMiner.loadLog(file);
							AcceptingPetriNet net = RunFodina.mine(log);
							net.exportToFile(new FakeContext(), outFile);
						}
					}

					//structured miner
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-sm.bpmn");
						if (!outFile.exists()) {
							try {
								System.out.println("running SM");
								BPMNDiagram diagram = RunStructuredMiner.mine(file);
								new BpmnExportPlugin().export(new FakePluginContext(), diagram, outFile);
							} catch (Exception e) {
								FileWriter writer = new FileWriter(outFile);
								writer.write("error\n");
								e.printStackTrace(new PrintWriter(writer, true));
								writer.close();
							}

						}
					}

					//flower miner
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-fm.tree");
						if (!outFile.exists()) {
							tree = RunFlowerMiner.mine(file);
							System.out.println("writing result " + outFile);
							EfficientTreeExportPlugin.export(tree, outFile);
						}
					}

					//trace miner
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-tm.tree");
						if (!outFile.exists()) {
							tree = RunTraceMiner.mine(file);
							System.out.println("writing result " + outFile);
							EfficientTreeExportPlugin.export(tree, outFile);
						}
					}
					
					//tau miner
					if (1 == 2) {
						outFile = new File(outputDirectory, file.getName() + "-tam.tree");
						if (!outFile.exists()) {
							tree = TauMiner.mine();
							System.out.println("writing result " + outFile);
							EfficientTreeExportPlugin.export(tree, outFile);
						}
					}
				}
			}
		}
	}
}
