package batch.test;

import generation.CompareTrees;
import generation.GenerateLog;
import generation.GenerateLogParameters;
import generation.GenerateTree;
import generation.GenerateTreeParameters;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.conversion.ReduceTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.processtree.ProcessTree;

import batch.incompleteness.AbstractBatchRediscoverability;
import batch.incompleteness.miners.MinerIvM;
import batch.incompleteness.miners.MinerOption;

public class Test {

	@Plugin(name = "Batch test algorithms 5", returnLabels = { "Batch test result" }, returnTypes = { HTMLToString.class }, parameterLabels = { }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch test algorithms 5, default", requiredParameterLabels = {})
	public HTMLToString test(PluginContext context) throws Exception {
		MinerOption miner = new MinerIvM();

		int numberOfTraces = 20000;
		int numberOfActivities = 15;
		
		final int seeds = 50;
		int start = 0;
		final boolean results[] = new boolean[seeds];

		for (int seed = start; seed < seeds; seed++) {
			System.out.println("start " + seed);
			
			//tree
			GenerateTreeParameters treeParameters = new GenerateTreeParameters(seed, true, numberOfActivities, 3);
			ProcessTree tree = (new GenerateTree()).generateTree(treeParameters);
			ReduceTree.reduceTree(tree, new EfficientTreeReduceParameters(false));
			System.out.println(" " + CompareTrees.hash(tree.getRoot()));

			//log from tree
			GenerateLogParameters logParameters = new GenerateLogParameters(numberOfTraces, seed);
			XLog log = (new GenerateLog()).generateLog(tree, logParameters);

			//check rediscovery
			ProcessTree discoveredTree = AbstractBatchRediscoverability.discover(context, log,
					miner.getMiningParameters(), miner.getDfgMiningParameters());
			ReduceTree.reduceTree(discoveredTree, new EfficientTreeReduceParameters(false));
			System.out.println(" " + CompareTrees.hash(discoveredTree.getRoot()));
			results[seed] = CompareTrees.isLanguageEqual(tree, discoveredTree);
			System.out.println(" equal: " + results[seed]);
		}
		
		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				StringBuilder result = new StringBuilder();
				for (int seed = 0; seed < seeds; seed++) {
					result.append(seed);
					result.append(' ');
					result.append(results[seed]);
					result.append("<br>");
				}
				return result.toString();
			}
		};
	}

}
