package thesis.evaluation.rediscoverability;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiner;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParameters;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMcd;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMd;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMfd;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMa;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMc;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMfa;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMflc;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMlc;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.InductiveMiner.plugins.IMTree;
import org.processmining.plugins.flowerMiner.FlowerMinerDfg;
import org.processmining.plugins.flowerMiner.TraceMiner;

public class RediscoverabilitySettings {
	public static final File baseDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//incompleteness");
	//public static final File baseDirectory = new File(".");

	public static final File generatedModelDirectory = new File(baseDirectory, "generatedModels");
	public static final File logDirectory = new File(baseDirectory, "generatedLogs");
	public static final File discoveredModelDirectory = new File(baseDirectory, "discoveredModels");
	public static final File measuresDirectory = new File(baseDirectory, "measures");

	public static final Algorithm[] algorithms = Algorithm.values();

	public static final int logRounds = 13;
	public static final int logIncreaseFactor = 2;

	public static enum Algorithm {
		im(new MiningParametersIM()), imf(new MiningParametersIMf()), ima(new MiningParametersIMa()), imfa(
				new MiningParametersIMfa()), fm(true), imd(
						new DfgMiningParametersIMd()), imfd(new DfgMiningParametersIMfd()), tm(false), imlc(
								new MiningParametersIMlc()), imflc(new MiningParametersIMflc()), imc(
										new MiningParametersIMc()), imcd(new DfgMiningParametersIMcd());

		private final MiningParameters miningParameters;
		private final DfgMiningParameters dfgMiningParameters;
		private final boolean flower;

		private Algorithm(MiningParameters miningParameters) {
			this.miningParameters = miningParameters;
			miningParameters.setDebug(true);
			this.dfgMiningParameters = null;
			this.flower = false;
		}

		private Algorithm(DfgMiningParameters dfgMiningParameters) {
			this.miningParameters = null;
			this.dfgMiningParameters = dfgMiningParameters;
			dfgMiningParameters.setDebug(true);
			this.flower = false;
		}

		private Algorithm(boolean flower) {
			this.miningParameters = null;
			this.dfgMiningParameters = null;
			this.flower = flower;
		}

		public EfficientTree mine(XLog log) {
			if (miningParameters != null) {
				return IMTree.mineTree(log, miningParameters);
			} else if (dfgMiningParameters != null) {
				IMLogInfo info = IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(log,
						MiningParameters.getDefaultClassifier(), MiningParameters.getDefaultLifeCycleClassifier()));
				return ProcessTree2EfficientTree.convert(DfgMiner.mine(info.getDfg(), dfgMiningParameters, new Canceller() {
					public boolean isCancelled() {
						return false;
					}
				}));
			} else if (flower) {
				IMLogInfo info = IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(log,
						MiningParameters.getDefaultClassifier(), MiningParameters.getDefaultLifeCycleClassifier()));
				return FlowerMinerDfg.mine(info.getDfg().getActivities());
			} else {
				return TraceMiner.mineTraceModel(log, miningParameters.getDefaultClassifier());
			}
		}
	}
}
