package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.inductiveminer2.helperclasses.XLifeCycleClassifierIgnore;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;
import org.processmining.plugins.inductiveminer2.plugins.InductiveMinerPlugin;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;

public class AlgorithmIMf extends AlgorithmFlatAbstract {

	public String getName() {
		return "IM - infrequent";
	}

	public String getAbbreviation() {
		return "IMf";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public String getFlattenedFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile, XEventClassifier classifier) throws Exception {
		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		MiningParametersAbstract parameters = new MiningParametersIMInfrequent();
		parameters.setClassifier(classifier);
		IMLog iLog = new IMLogImpl(log, classifier, new XLifeCycleClassifierIgnore());
		EfficientTree tree = InductiveMinerPlugin.mineTree(iLog, parameters, canceller);
		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}
