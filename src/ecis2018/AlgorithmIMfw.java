package ecis2018;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.inductiveminer2.plugins.InductiveMinerWithoutLogPlugin;
import org.processmining.plugins.inductiveminer2.withoutlog.MiningParametersWithoutLogAbstract;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.Log2DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.variants.MiningParametersIMInfrequentWithoutLog;

public class AlgorithmIMfw implements Algorithm {

	public String getName() {
		return "IMfw";
	}

	public String getAbbreviation() {
		return "IMfw";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		DfgMsd graph = Log2DfgMsd.convert(log, MiningParameters.getDefaultClassifier(),
				MiningParameters.getDefaultLifeCycleClassifier());

		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		MiningParametersWithoutLogAbstract parameters = new MiningParametersIMInfrequentWithoutLog();
		parameters.setDebug(false);

		EfficientTree tree = InductiveMinerWithoutLogPlugin.mineTree(graph, parameters, canceller);

		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}
