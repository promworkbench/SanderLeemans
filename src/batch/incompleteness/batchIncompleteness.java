package batch.incompleteness;

import generation.GenerateLog;
import generation.GenerateLogParameters;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.processtree.ProcessTree;

@Plugin(name = "Batch test incompleteness", returnLabels = { "Batch incompleteness result" }, returnTypes = { BatchRediscoverabilityResult.class }, parameterLabels = { "Process tree" }, userAccessible = true)
public class batchIncompleteness extends AbstractBatchRediscoverability {

	private static String title = "Batch test incompleteness.";
	
	/**
	 * Test all logs given the parameters.
	 * 
	 * @param context
	 * @return
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness, default", requiredParameterLabels = {})
	public BatchRediscoverabilityResult batch(final PluginContext context) {
		return super.batch(context, title);
	}

	/**
	 * Test a single tree.
	 */
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness, default", requiredParameterLabels = { 0 })
	public BatchRediscoverabilityResult findLargestNonrediscoverableLog(PluginContext context, ProcessTree tree) {
		return super.findLargestNonrediscoverableLog(context, tree, title);
	}

	/**
	 * Generate a log.
	 */
	protected XLog generateLog(ProcessTree tree, long logSeed, int logSize) {
		try {
			return (new GenerateLog()).generateLog(tree, new GenerateLogParameters(logSize, logSeed));
		} catch (Exception e) {
			return null;
		}
	}

}
