package coopis2018;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.indulpetminer.mining.MiningParametersIndulpet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.inductiveminer2.plugins.InductiveMinerPlugin;

public class AlgorithmIndulpet implements Algorithm {

	public String getName() {
		return "Indulpet";
	}
	
	public String getAbbreviation() {
		return "IN";
	}

	public String getFileExtension() {
		return ".tree";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		MiningParametersIndulpet parameters = new MiningParametersIndulpet();
		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		parameters.setDebug(true);
		EfficientTree tree = InductiveMinerPlugin.mineTree(parameters.getIMLog(log), parameters, canceller);

		EfficientTreeExportPlugin.export(tree, modelFile);
	}

}
