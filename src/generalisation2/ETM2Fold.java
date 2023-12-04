package generalisation2;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.etm.ETMPareto;
import org.processmining.plugins.etm.fitness.TreeFitnessAbstract;
import org.processmining.plugins.etm.fitness.metrics.MultiThreadedFitnessEvaluator;
import org.processmining.plugins.etm.fitness.metrics.OverallFitness;
import org.processmining.plugins.etm.ui.plugins.ETMParetoLivePlugin;

public class ETM2Fold {
	@Plugin(
			name = "Apply ETMd with 2-fold",
				parameterLabels = { "Training Log", "Test Log", "Complete Log" },
				returnLabels = { "Running ETMd Live Pareto instance" },
				returnTypes = { ETMPareto.class },
				userAccessible = true,
				help = "Mine Pareto front with ETMd in Live mode",
				handlesCancel = true)
	@UITopiaVariant(
			uiLabel = "Apply ETMd with 2-fold",
				affiliation = "Eindhoven University of Technology",
				author = "J.C.A.M.Buijs",
				email = "j.c.a.m.buijs@tue.nl",
				pack = "EvolutionaryTreeMiner")
	public static ETMPareto etmParetoLiveNoSeed(final UIPluginContext context, XLog trainingsLog, XLog testLog, XLog completeLog) {
		ETMPareto pareto = ETMParetoLivePlugin.etmParetoLiveWithSeed(context, trainingsLog);
		
		MultiThreadedFitnessEvaluator evaluator = (MultiThreadedFitnessEvaluator) pareto.getParams().getFitnessEvaluator();
		for (TreeFitnessAbstract eval: evaluator.getEvaluators()) {
			OverallFitness evaluator2 = (OverallFitness) eval;
			for (TreeFitnessAbstract eval2 : evaluator2.getEvaluators().keySet()) {
				if (eval2 instanceof Generalisation2) {
					((Generalisation2) eval2).setTestLog(testLog);
				} else if (eval2 instanceof Precision2) {
					((Precision2) eval2).setTestLog(testLog);
				}  else if (eval2 instanceof Fitness2) {
					((Fitness2) eval2).setCompleteLog(testLog);
				}
			}
		}
		
		return pareto;
	}
}
