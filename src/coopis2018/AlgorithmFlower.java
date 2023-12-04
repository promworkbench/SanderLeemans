package coopis2018;

import java.io.File;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.flowerMiner.FlowerMiner;

public class AlgorithmFlower implements Algorithm {

	public String getName() {
		return "flower";
	}
	
	public String getAbbreviation() {
		return "F";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		EfficientTree tree = FlowerMiner.mine(new XLogInfoFactory().createLogInfo(log, new XEventNameClassifier()));
		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}
