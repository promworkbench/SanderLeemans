package thesis.evaluation.scalability;

import java.io.File;
import java.util.Random;

import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.DfgExportPlugin;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2processTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.processtree.ptml.exporting.PtmlExportTree;

import generation.GenerateTree;
import generation.GenerateTreeParameters;
import p2015sosym.efficienttree.generatebehaviour.GenerateLog;
import thesis.helperClasses.XLogWriterIncremental;

public class GenerateTreeAndLog {

	public static void main(String[] args) throws Exception {
		int maxRound = 10;
		int maxTreeSeed = 10;

		for (int treeSeed = 9; treeSeed <= maxTreeSeed; treeSeed++) {

			for (int round = 10; round <= maxRound; round++) {

				//generate tree
				int treeSize = (int) Math.pow(2, round);
				GenerateTreeParameters parameters = new GenerateTreeParameters(treeSeed, true, treeSize, 5);
				EfficientTree tree = ProcessTree2EfficientTree.convert(new GenerateTree().generateTree(parameters));
				EfficientTreeReduce.reduce(tree);
				System.out.println(tree);

				//write to file
				new PtmlExportTree().exportDefault(null, EfficientTree2processTree.convert(tree),
						new File(
								"C:\\Users\\sander\\Documents\\svn\\00 - the beast\\experiments\\scalability\\data\\round "
										+ round + " treeSeed " + treeSeed + ".ptml"));

				//System.out.println(EfficientTree2Latex.latex(tree, true));

				long logSize = (long) Math.pow(4, round);
				long randomSeed = 1;
				Random random = new Random(randomSeed);

				//generate log
				if (1 == 1) {
					XLogWriterIncremental logWriter = new XLogWriterIncremental(new File(
							"C:\\Users\\sander\\Documents\\svn\\00 - the beast\\experiments\\scalability\\data\\round "
									+ round + " treeSeed " + treeSeed + ".xes.gz"));
					for (int[] trace : GenerateLog.generateTraces(tree, logSize, random, false)) {
						logWriter.writeTrace(trace, tree.getInt2activity());
					}
					logWriter.close();
				}

				//generate dfg
				if (1 == 2) {
					Dfg dfg = GenerateLog.generateDfg(tree, logSize, randomSeed);
					DfgExportPlugin.export(dfg,
							new File(
									"C:\\Users\\sander\\Documents\\svn\\00 - the beast\\experiments\\scalability\\data\\round "
											+ round + " treeSeed " + treeSeed + ".dfg"));
				}
			}
		}
	}
}
