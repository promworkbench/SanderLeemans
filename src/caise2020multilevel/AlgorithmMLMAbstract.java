package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.multilevelminer.mining.CommonParametersImpl;
import org.processmining.multilevelminer.mining.MultiLevelMiner;
import org.processmining.multilevelminer.mining.MultiLevelMiningParametersDefault;
import org.processmining.multilevelminer.mining.SubModelMiner;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.multilevelminer.plugins.MultiLevelModelExportPlugin;

public abstract class AlgorithmMLMAbstract implements Algorithm {

	public abstract String getVariant();

	public String getName() {
		return "MLM" + getVariant();
	}

	public String getAbbreviation() {
		return "MLM" + getVariant();
	}

	public String getFileExtension() {
		return ".mlm";
	}

	public String getFlattenedFileExtension() {
		return ".apnml";
	}

	public abstract SubModelMiner<?, ?, ?>[] getMiners();

	public void run(File logFile, XLog log, File modelFile, XEventClassifier[] classifiers) throws Exception {
		MultiLevelMiningParametersDefault parameters = new MultiLevelMiningParametersDefault();

		SubModelMiner<?, ?, ?>[] miners = getMiners();

		CommonParametersImpl commonParameters1 = MultiLevelMiningParametersDefault.defaultCommonParameters.clone();
		commonParameters1.setClassifier(classifiers[0]);
		parameters.setCommonParameters(0, commonParameters1);
		parameters.setMiner(0, miners[0]);

		for (int level = 1; level < classifiers.length; level++) {
			CommonParametersImpl commonParameters2 = MultiLevelMiningParametersDefault.defaultCommonParameters.clone();
			commonParameters2.setClassifier(classifiers[level]);
			parameters.add(miners[level], commonParameters2);
		}

		MultiLevelModel model = MultiLevelMiner.mine(log, parameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});

		MultiLevelModelExportPlugin.export(model, modelFile);
	}
}