package ecis2018;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.etm.ETM;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.NAryTreeToProcessTree;
import org.processmining.plugins.etm.parameters.ETMParam;
import org.processmining.plugins.etm.parameters.ETMParamFactory;
import org.processmining.processtree.ProcessTree;
import org.uncommonseditedbyjoosbuijs.watchmaker.framework.PopulationData;
import org.uncommonseditedbyjoosbuijs.watchmaker.framework.TerminationCondition;

public class AlgorithmETM implements Algorithm {

	public String getName() {
		return "ETM";
	}
	
	public String getAbbreviation() {
		return "ETM";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		int nrPopsize = 10;

		ETMParam etmParam = ETMParamFactory.buildStandardParam(log); //start with the standard
		etmParam.getCentralRegistry().getEventClasses().harmonizeIndices();

		etmParam.addTerminationCondition(new TerminationCondition() {
			public boolean shouldTerminate(PopulationData<?> populationData) {
				return false;
			}
		});

		//Run
		ETM etm = new ETM(etmParam);
		etm.run();
		NAryTree bestTree = etm.getResult();

		ProcessTree pTree = NAryTreeToProcessTree.convert(bestTree, etmParam.getCentralRegistry().getEventClasses());
		EfficientTree tree = ProcessTree2EfficientTree.convert(pTree);

		//remove +complete
		String[] a = tree.getInt2activity();
		for (int i = 0; i < a.length; i++) {
			if (a[i].toLowerCase().endsWith("+complete")) {
				a[i] = a[i].substring(0, a[i].lastIndexOf("+"));
			}
		}

		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}
