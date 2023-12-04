package svn55datastochastics;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;

import thesis.helperClasses.FakeContext;

public class AlgorithmIMf implements Algorithm {

	public String getName() {
		return "IMf";
	}

	public String getLatexName() {
		return "IMf";
	}

	public String getAbbreviation() {
		return "IMf";
	}

	public String getFileExtension() {
		return ".apnml";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		MiningParametersAbstract parameters = new MiningParametersIMInfrequent() {
			@Override
			public EfficientTreeReduceParameters getReduceParameters() {
				return null;
			}
		};
		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		parameters.setDebug(false);

		EfficientTree tree = InductiveMiner.mineEfficientTree(parameters.getIMLog(log), parameters, canceller);

		AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);
		FakeContext context = new FakeContext();
		net.exportToFile(context, modelFile);
	}

}
