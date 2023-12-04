package thesis.evaluation.standAloneMiners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiner;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParameters;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMcd;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMd;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersIMfd;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.DfgImportPlugin;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2HumanReadableString;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;

public class RunInductiveMinerDirectlyFollows {
	public static enum Variant {
		imd(new DfgMiningParametersIMd()), imfd(new DfgMiningParametersIMfd()), imcd(new DfgMiningParametersIMcd());

		private final DfgMiningParameters miningParameters;

		private Variant(DfgMiningParameters miningParameters) {
			this.miningParameters = miningParameters;
		}

		public DfgMiningParameters getParameters() {
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
			System.out.println("Usage: InductiveMinerDirectlyFollows.jar algorithm dfgfile");
			System.out.println(" Algorithm can be: " + Arrays.toString(Variant.values()) + ".");
			return;
		}

		System.out.println(EfficientTree2HumanReadableString.toMachineString(mineFromDfg(algorithm, file)));
	}

	public static EfficientTree mineFromDfg(Variant algorithm, File file) throws FileNotFoundException, Exception {

		Dfg dfg = new DfgImportPlugin().importFromStream(null, new FileInputStream(file), null, 0);

		EfficientTree tree = ProcessTree2EfficientTree.convert(DfgMiner.mine(dfg, algorithm.miningParameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		}));

		return tree;
	}

}
