package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.flowerMiner.FlowerMiner;

public class AlgorithmFlower extends AlgorithmFlatAbstract {

	public String getName() {
		return "flower";
	}

	public String getAbbreviation() {
		return "F";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile, XEventClassifier classifier) throws Exception {
		EfficientTree tree = FlowerMiner.mine(new XLogInfoFactory().createLogInfo(log, classifier));
		EfficientTreeExportPlugin.export(tree, modelFile);
	}

	public String getFlattenedFileExtension() {
		return ".tree";
	}

}
