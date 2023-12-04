package thesis.evaluation.standAloneMiners;

import java.io.File;
import java.util.Arrays;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2HumanReadableString;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMa;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMc;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMfa;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMflc;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMlc;
import org.processmining.plugins.InductiveMiner.plugins.IMTree;
import org.processmining.plugins.log.OpenNaiveLogFilePlugin;
import org.processmining.xeslite.plugin.OpenLogFileDiskImplWithoutCachePlugin;

import thesis.helperClasses.FakeContext;

public class RunInductiveMiner {

	public static enum Variant {
		im(new MiningParametersIM()), imf(new MiningParametersIMf()), ima(new MiningParametersIMa()), imfa(
				new MiningParametersIMfa()), imc(new MiningParametersIMc()), imlc(
						new MiningParametersIMlc()), imflc(new MiningParametersIMflc());

		public final MiningParameters miningParameters;

		private Variant(MiningParameters miningParameters) {
			this.miningParameters = miningParameters;
		}

		public MiningParameters getParameters() {
			return miningParameters;
		}
	}

	public static void main(String[] args) throws Exception {
		boolean help = false;
		Variant algorithm = null;
		File file = null;
		if (args.length != 2) {
			help = true;
		} else {
			try {
				algorithm = Variant.valueOf(args[0].toLowerCase());
			} catch (IllegalArgumentException e) {
				help = true;
			}

			file = new File(args[1]);
			help = help || !file.exists();
		}

		if (help) {
			System.out.println("Usage: InductiveMiner.jar algorithm logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			System.out.println(" Algorithm can be: " + Arrays.toString(Variant.values()) + ".");
			return;
		}

		XLog log = loadLog(file);

		EfficientTree tree = IMTree.mineTree(log, algorithm.miningParameters);
		System.out.println(EfficientTree2HumanReadableString.toMachineString(tree));
	}

	public static XLog loadLog(File file) throws Exception {
		return (XLog) new OpenLogFileDiskImplWithoutCachePlugin().importFile(new FakeContext(), file);
	}

	public static XLog loadLogNaive(File file) throws Exception {
		return (XLog) new OpenNaiveLogFilePlugin().importFile(new FakeContext(), file);
	}
}
