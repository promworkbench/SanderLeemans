package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiner;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiningParametersAbstract;
import org.processmining.directlyfollowsmodelminer.mining.variants.DFMMiningParametersDefault;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel2AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;

import thesis.helperClasses.FakeContext;

public class AlgorithmDFM implements Algorithm {

	public String getName() {
		return "directly follows miner";
	}

	public String getAbbreviation() {
		return "DFM";
	}

	public String getFileExtension() {
		return ".pnml";
	}

	public String getLatexName() {
		return "directly follows miner";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		DFMMiningParametersAbstract miningParameters = new DFMMiningParametersDefault();
		Canceller canceller2 = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		miningParameters.setNoiseThreshold(0.5);

		IMLogImpl log2 = new org.processmining.plugins.inductiveminer2.logs.IMLogImpl(log,
				miningParameters.getClassifier(), miningParameters.getLifeCycleClassifier());
		DirectlyFollowsModel dfg = DFMMiner.mine(log2, miningParameters, canceller2);

		AcceptingPetriNet net = DirectlyFollowsModel2AcceptingPetriNet.convert(dfg);
		FakeContext context = new FakeContext();
		net.exportToFile(context, modelFile);
	}

}
