package batch.incompleteness;

import generation.GenerateLog;
import generation.GenerateLogParameters;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.processtree.ProcessTree;


@Plugin(name = "Batch test infrequency", returnLabels = { "Batch incompleteness result" }, returnTypes = { BatchRediscoverabilityResult.class }, parameterLabels = { "Process tree" }, userAccessible = true)
public class batchInfrequency extends AbstractBatchRediscoverability {
	
	private static String title = "Batch test of infrequency.";

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness, default", requiredParameterLabels = {})
	public BatchRediscoverabilityResult batch(final PluginContext context) {
		return super.batch(context, title);
	}
	
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness, default", requiredParameterLabels = { 0 })
	public BatchRediscoverabilityResult findLargestNonrediscoverableLog(PluginContext context, ProcessTree tree) {
		return super.findLargestNonrediscoverableLog(context, tree, title);
	}

	protected XLog generateLog(ProcessTree tree, long logSeed, int logSize) {
		try {
			return (new GenerateLog()).generateLog(tree, new GenerateLogParameters(logSize, logSeed, 1, 4));
		} catch (Exception e) {
			return null;
		}
	}

}
