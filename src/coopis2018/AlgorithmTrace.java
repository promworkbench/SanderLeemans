package coopis2018;

import java.io.File;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.flowerMiner.TraceMiner;

public class AlgorithmTrace implements Algorithm {

	public String getName() {
		return "trace";
	}
	
	public String getAbbreviation() {
		return "T";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		EfficientTree tree = TraceMiner.mineTraceModel(log, new XEventNameClassifier());
		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}
