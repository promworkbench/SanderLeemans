package svn53longdistancedependenciesresample;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.flowerMiner.TraceMiner;

import thesis.helperClasses.FakeContext;

public class AlgorithmTraceModel implements Algorithm {

	public String getName() {
		return "TM";
	}

	public String getLatexName() {
		return "TM";
	}

	public String getAbbreviation() {
		return "TM";
	}

	public String getFileExtension() {
		return ".apnml";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();
		EfficientTree tree = TraceMiner.mineTraceModel(log, classifier);
		AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);
		FakeContext context = new FakeContext();
		net.exportToFile(context, modelFile);
	}

}
