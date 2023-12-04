package thesis.evaluation.standAloneMiners;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2HumanReadableString;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.etm.parameters.ETMParam;
import org.processmining.plugins.etm.parameters.ETMParamFactory;
import org.processmining.plugins.etm.ui.plugins.ETMwithoutGUI;
import org.processmining.processtree.ProcessTree;

public class RunEvolutionaryTreeMiner {

	public static void main(String[] args) throws Exception {
		boolean help = false;
		long seed = 0;
		File file = null;
		if (args.length != 2) {
			help = true;
		} else {
			try {
				seed = Long.valueOf(args[0]);
			} catch (IllegalArgumentException e) {
				help = true;
			}

			file = new File(args[1]);
			help = help || !file.exists();
		}

		if (help) {
			System.out.println("Usage: EvolutionaryTreeMiner.jar seed logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}

		XLog log = RunInductiveMiner.loadLog(file);

		EfficientTree tree = mine(log, seed);
		System.out.println(EfficientTree2HumanReadableString.toMachineString(tree));
	}
	
	public static EfficientTree mine(XLog log, long seed) {
		ETMParam param = ETMParamFactory.buildStandardParam(log, null);
		param.getCentralRegistry().getRandom().setSeed(seed);
		param.addTerminationConditionMaxDuration(1000 * 60 * 30);
		
		ProcessTree pt = ETMwithoutGUI.minePTWithParameters(null, log, MiningParameters.getDefaultClassifier(), param);
		return ProcessTree2EfficientTree.convert(pt);
	}

}
