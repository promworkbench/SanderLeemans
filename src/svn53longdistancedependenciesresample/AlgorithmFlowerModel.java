package svn53longdistancedependenciesresample;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.flowerMiner.FlowerMiner;

import thesis.helperClasses.FakeContext;

public class AlgorithmFlowerModel implements Algorithm {

	public String getName() {
		return "FM";
	}

	public String getLatexName() {
		return "FM";
	}

	public String getAbbreviation() {
		return "FM";
	}

	public String getFileExtension() {
		return ".apnml";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
		EfficientTree tree = FlowerMiner.mine(logInfo);
		AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);
		FakeContext context = new FakeContext();
		net.exportToFile(context, modelFile);
	}

}
