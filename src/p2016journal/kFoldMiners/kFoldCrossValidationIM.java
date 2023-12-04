package p2016journal.kFoldMiners;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.processtree.ProcessTree;

import p2016journal.kFoldCrossValidationProcessTree;
import p2016journal.kFoldCrossValidationResult;

public class kFoldCrossValidationIM extends kFoldCrossValidationProcessTree {

	private MiningParameters miningParameters;

	@Plugin(name = "Perform 10-fold cross validation on Inductive Miner", returnLabels = { "k-fold validation result" }, returnTypes = { kFoldCrossValidationResult.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public kFoldCrossValidationResult measure(UIPluginContext context, XLog log) throws Exception {

		int k = 10;
		
		//perform the validation
		IMMiningDialog dialog = new IMMiningDialog(log);
		InteractionResult result = context.showWizard("Mine using Inductive Miner", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}
		miningParameters = dialog.getMiningParameters();
		
		String name = XConceptExtension.instance().extractName(log);
		context.getFutureResult(0).setLabel(k + "-fold cross validation result of IM on " + name);

		return run(log, miningParameters.getClassifier(), k, 0);
	}

	public ProcessTree discover(XLog log) {
		return IMProcessTree.mineProcessTree(log, miningParameters);
	}

}
