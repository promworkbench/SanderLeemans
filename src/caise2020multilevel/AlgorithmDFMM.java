package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiningParametersAbstract;
import org.processmining.directlyfollowsmodelminer.mining.plugins.DfmExportPlugin;
import org.processmining.directlyfollowsmodelminer.mining.plugins.DirectlyFollowsModelMinerPlugin;
import org.processmining.directlyfollowsmodelminer.mining.variants.DFMMiningParametersDefault;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.inductiveminer2.helperclasses.XLifeCycleClassifierIgnore;

public class AlgorithmDFMM extends AlgorithmFlatAbstract {

	public String getName() {
		return "dfmm";
	}

	public String getAbbreviation() {
		return "df";
	}

	public String getFileExtension() {
		return ".dfm";
	}

	public String getFlattenedFileExtension() {
		return ".dfm";
	}

	public void run(File logFile, XLog log, File modelFile, XEventClassifier classifier) throws Exception {
		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		DFMMiningParametersAbstract parameters = new DFMMiningParametersDefault();
		parameters.setClassifier(classifier);
		parameters.setLifeCycleClassifier(new XLifeCycleClassifierIgnore());
		DirectlyFollowsModel dfm = DirectlyFollowsModelMinerPlugin.mine(log, parameters, canceller);

		DfmExportPlugin.export(dfm, modelFile);
	}
}