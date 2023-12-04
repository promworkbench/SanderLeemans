package batch.infrequency3;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.conversion.ReduceTree;
import org.processmining.plugins.InductiveMiner.dfgOnly.log2logInfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.IMdProcessTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.jobList.JobList;
import org.processmining.plugins.InductiveMiner.jobList.JobListConcurrent;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.termination.ProMCancelTerminationCondition;
import org.processmining.processtree.ProcessTree;

import batch.ThreadPoolSingleton;
import batch.incompleteness.miners.MinerIM;
import batch.incompleteness.miners.MinerIMd;
import batch.incompleteness.miners.MinerIMi01;
import batch.incompleteness.miners.MinerIMi05;
import batch.incompleteness.miners.MinerIMi20;
import batch.incompleteness.miners.MinerIMi80;
import batch.incompleteness.miners.MinerIMiD01;
import batch.incompleteness.miners.MinerIMiD05;
import batch.incompleteness.miners.MinerIMiD20;
import batch.incompleteness.miners.MinerIMiD80;
import batch.incompleteness.miners.MinerIMin;
import batch.incompleteness.miners.MinerIMinD;
import batch.incompleteness.miners.MinerOption;
import batch.infrequency2.Infrequency2;
import generation.GenerateTreeParameters;

@Plugin(name = "Batch test infrequency3", returnLabels = { "Batch infrequency3 result" }, returnTypes = {
		Infrequency3Result.class }, parameterLabels = { "Event log" }, userAccessible = true)
public class Infrequency3 {
	public static MinerOption[] as = new MinerOption[] { new MinerIM(), new MinerIMd(), new MinerIMi01(),
			new MinerIMiD01(), new MinerIMi05(), new MinerIMiD05(), new MinerIMi20(), new MinerIMiD20(),
			new MinerIMi80(), new MinerIMiD80(), new MinerIMin(), new MinerIMinD() };
	//		public static MinerOption[] as = new MinerOption[] { new MinerIMinD() };
	public static GenerateTreeParameters ts = new GenerateTreeParameters(1052, true, 40, 5);
	public static int numberOfDeviatingTraces[] = new int[] { 100 };

	public static long deviationsSeed = 12345;
	public static long logSeed = 54696330;
	public static int logSize = 1000;
	public static double probabilityOfDeviation = 0.2;

	//======

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness3, default", requiredParameterLabels = {})
	public Infrequency3Result test(PluginContext context) throws Exception {
		try {
			Infrequency3Result result = testAlgorithmsAndTrees(context, as, ts);
			result.writeToDisk(new File("d://output//infrequency3.txt"));
			return result;
		} catch (ExecutionException e) {
			return null;
		}
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch-test incompleteness3, default", requiredParameterLabels = { 0 })
	public Infrequency3Result test(PluginContext context, XLog log) throws Exception {
		JobList jobList = new JobListConcurrent(ThreadPoolSingleton.getInstance());
		final Infrequency3Result result = new Infrequency3Result(as, new int[1]);
		testAlgorithms(context, as, jobList, result, 0, GenerateLog3.fromLog(log));
		jobList.join();
		ThreadPoolSingleton.shutdown();
		result.writeToDisk(new File("d://output//infrequency3.txt"));
		return result;
	}

	public static Infrequency3Result testAlgorithmsAndTrees(final PluginContext context, final MinerOption[] algorithms,
			final GenerateTreeParameters treeParameters) throws Exception {
		JobList jobList = new JobListConcurrent(ThreadPoolSingleton.getInstance());

		final Infrequency3Result result = new Infrequency3Result(algorithms, numberOfDeviatingTraces);
		final ProcessTree tree = Infrequency2.generateTree(treeParameters);
		System.out.println(tree);
		for (int d = 0; d < numberOfDeviatingTraces.length; d++) {
			final int d2 = d;

			final Quadruple<XLog, Integer, Collection<XEventClass>, XEventClassifier> logp = GenerateLog3
					.generateLog(tree, numberOfDeviatingTraces[d]);
			result.setNumberOfDeviations(d2, logp.getB());

			testAlgorithms(context, algorithms, jobList, result, d2, logp);
		}

		jobList.join();
		ThreadPoolSingleton.shutdown();

		return result;
	}

	public static void testAlgorithms(final PluginContext context, final MinerOption[] algorithms, JobList jobList,
			final Infrequency3Result result, final int d,
			final Quadruple<XLog, Integer, Collection<XEventClass>, XEventClassifier> logp) {
		for (int a = 0; a < algorithms.length; a++) {
			final int a2 = a;
			jobList.addJob(new Runnable() {
				public void run() {
					try {
						test(context, algorithms[a2], logp.getA(), logp.getC(), logp.getD(), numberOfDeviatingTraces[d],
								result, a2, d);
						//							result.setFitness(a2, d2, r[0]);
						//							result.setPrecision(a2, d2, r[1]);
						//							result.setGeneralisation(a2, d2, r[2]);
					} catch (Exception e) {
						result.setFitness(a2, d, -999.999);
						result.setPrecision(a2, d, -999.999);
						result.setGeneralisation(a2, d, -999.999);
					}
				}
			});
		}
	}

	public static void test(PluginContext context, MinerOption minerOption, XLog log,
			Collection<XEventClass> activities, XEventClassifier classifier, int numberOfDeviatingTraces,
			Infrequency3Result result, int algorithmN, int deviationsN)
			throws UnknownTreeNodeException, ReductionFailedException {
		ProcessTree discoveredTree;
		if (minerOption.getMiningParameters() != null) {
			discoveredTree = IMProcessTree.mineProcessTree(log, minerOption.getMiningParameters());
		} else {
			discoveredTree = IMdProcessTree.mineProcessTree(
					IMLog2IMLogInfoDefault.log2logInfo(new IMLogImpl(log, MiningParameters.getDefaultClassifier(),
							MiningParameters.getDefaultLifeCycleClassifier())).getDfg(),
					minerOption.getDfgMiningParameters());
		}
		ReduceTree.reduceTree(discoveredTree, new EfficientTreeReduceParameters(false));

		System.out.println("mining done");

		//		Quintuple<PNRepResult, Petrinet, Marking, Double, TransEvClassMapping> alignmentResult = AryaAlignment
		//				.alignment(context, discoveredTree, log, classifier, activities);
		//		
		//		double fitnessA = AryaAlignment.getFitness(alignmentResult);
		//		result.setFitness(algorithmN, deviationsN, fitnessA);
		//
		//		Pair<Double, Double> precisionGeneralisationResult = AryaPrecisionGeneralisation.measure(context,
		//				alignmentResult, log);
		//		double precisionA = AryaPrecisionGeneralisation.getPrecision(precisionGeneralisationResult);
		//		result.setPrecision(algorithmN, deviationsN, precisionA);
		//		double generalisationA = AryaPrecisionGeneralisation.getGeneralisation(precisionGeneralisationResult);
		//		result.setGeneralisation(algorithmN, deviationsN, generalisationA);
		//
		//		System.out.println("Arya: f" + fitnessA + ", p" + precisionA + ", g" + generalisationA);
		//		return new double[] { fitnessA, precisionA, generalisationA };

		//ETM
		Triple<Double, CentralRegistry, NAryTree> t = Alignment.alignTree(discoveredTree, classifier, log,
				ProMCancelTerminationCondition.buildDummyCanceller());
		result.setFitness(algorithmN, deviationsN, t.getA());
		System.out.println("fitness done.");

		result.setPrecision(algorithmN, deviationsN, Alignment.computePrecision(t));
		System.out.println("precision done.");

		result.setGeneralisation(algorithmN, deviationsN, Alignment.computeGeneralisation(t));
		System.out.println("generalisation done.");
	}
}
