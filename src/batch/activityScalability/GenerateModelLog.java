package batch.activityScalability;

import generation.GenerateLog;
import generation.GenerateLogParameters;
import generation.GenerateTree;
import generation.GenerateTreeParameters;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.processtree.ProcessTree;

public class GenerateModelLog {
	
	@Plugin(name = "Generate model of xx activities and a log from it", returnLabels = { "XLog", "Process tree" }, returnTypes = { XLog.class, ProcessTree.class }, parameterLabels = { }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Generate model and log", requiredParameterLabels = {})
	public Object[] generate(PluginContext context) throws Exception {
		long seed = 123;
		int numberOfActivities = 10000;
		int numberOfTraces = 1000000;
		
		GenerateTreeParameters treeParameters = new GenerateTreeParameters(seed, true, numberOfActivities, 5);
		ProcessTree tree = (new GenerateTree()).generateTree(treeParameters);
		
		GenerateLogParameters logParameters = new GenerateLogParameters(numberOfTraces, seed);
		return new Object[]{(new GenerateLog()).generateLog(tree, logParameters), tree};
	}
	
}
