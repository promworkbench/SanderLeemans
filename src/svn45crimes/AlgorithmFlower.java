package svn45crimes;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
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

	public String getLatexName() {
		return "flower";
	}

	public String getAbbreviation() {
		return "flower";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();

		EfficientTree tree = FlowerMiner.mine(new XLogInfoFactory().createLogInfo(log, classifier));
		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}
