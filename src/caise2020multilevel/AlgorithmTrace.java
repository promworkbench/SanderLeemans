package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.flowerMiner.TraceMiner;

public class AlgorithmTrace extends AlgorithmFlatAbstract {

	public String getName() {
		return "trace";
	}

	public String getAbbreviation() {
		return "T";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public String getFlattenedFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile, XEventClassifier classifier) throws Exception {
		EfficientTree tree = TraceMiner.mineTraceModel(log, classifier);
		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}