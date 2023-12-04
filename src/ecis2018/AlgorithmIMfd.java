package ecis2018;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParameters;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMfd;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.IMdProcessTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.processtree.ProcessTree;

public class AlgorithmIMfd implements Algorithm {

	public String getName() {
		return "IMdf";
	}

	public String getAbbreviation() {
		return "IMfd";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		DfgMiningParameters parameters = new DfgMiningParametersIMfd();
		parameters.setDebug(false);

		//build Dfg
		Dfg dfg = IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(log, MiningParameters.getDefaultClassifier(),
				MiningParameters.getDefaultLifeCycleClassifier())).getDfg();

		ProcessTree tree = IMdProcessTree.mineProcessTree(dfg, parameters);
		EfficientTree tree2 = ProcessTree2EfficientTree.convert(tree);

		EfficientTreeExportPlugin.export(tree2, modelFile);
	}

}
