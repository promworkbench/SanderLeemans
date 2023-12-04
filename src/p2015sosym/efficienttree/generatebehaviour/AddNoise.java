package p2015sosym.efficienttree.generatebehaviour;

import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.processtree.ProcessTree;

/**
 * Take an existing log and a process tree, and add noisy traces: each added
 * trace will have one event missing or added, with equal probability.
 * 
 * @author sleemans
 * 
 */

public class AddNoise {

	@Plugin(name = "Add noise to log - 1 random trace", returnLabels = { "Log with added noise" }, returnTypes = {
			XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog0(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddNoise(system, log, 1);
	}

	@Plugin(name = "Add noise to log - 10 random traces", returnLabels = { "Log with added noise" }, returnTypes = {
			XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog1(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddNoise(system, log, 10);
	}

	@Plugin(name = "Add noise to log - 100 random traces", returnLabels = { "Log with added noise" }, returnTypes = {
			XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog2(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddNoise(system, log, 100);
	}

	@Plugin(name = "Add noise to log - 1,000 random traces", returnLabels = { "Log with added noise" }, returnTypes = {
			XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog3(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddNoise(system, log, 1000);
	}

	@Plugin(name = "Add noise to log - 10,000 random traces", returnLabels = { "Log with added noise" }, returnTypes = {
			XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog4(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddNoise(system, log, 10000);
	}

	@Plugin(name = "Add noise to log - 100,000 random traces", returnLabels = {
			"Log with added noise" }, returnTypes = {
					XLog.class }, parameterLabels = { "Process tree", "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0, 1 })
	public XLog generateLog5(UIPluginContext context, ProcessTree system, XLog log) throws Exception {
		return copyAndAddNoise(system, log, 100000);
	}

	public static XLog copyAndAddNoise(ProcessTree system, XLog log, int nrOfDeviatingTraces) throws Exception {
		XLog log2 = (XLog) log.clone();
		addNoise(ProcessTree2EfficientTree.convert(system), log2, nrOfDeviatingTraces);
		return log2;
	}

	public static void addNoise(EfficientTree tree, XLog log, int nrOfDeviatingTraces) throws Exception {
		Random randomGenerator = new Random(0);

		for (int[] trace : GenerateLog.generateTraces(tree, nrOfDeviatingTraces, randomGenerator, false)) {
			//add a deviation
			int[] trace2 = deviateTrace(tree, randomGenerator, trace);

			//add to log
			log.add(GenerateLog.trace2xTrace(tree.getInt2activity(), trace2));
		}

	}

	public static int[] deviateTrace(EfficientTree tree, Random randomGenerator, int[] trace) {
		if (randomGenerator.nextBoolean()) {
			//add event
			int activity = randomGenerator.nextInt(tree.getInt2activity().length);
			int position = randomGenerator.nextInt(trace.length + 1);

			int[] trace2 = new int[trace.length + 1];
			System.arraycopy(trace, 0, trace2, 0, position);
			System.arraycopy(trace, position, trace2, position + 1, trace.length - position);
			trace2[position] = activity;
			trace = trace2;
		} else {
			//remove event
			int remove = randomGenerator.nextInt(trace.length);
			int[] trace2 = new int[trace.length - 1];
			System.arraycopy(trace, 0, trace2, 0, remove);
			System.arraycopy(trace, remove + 1, trace2, remove, (trace.length - remove) - 1);
			trace = trace2;
		}
		return trace;
	}
}
