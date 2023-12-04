package batch.infrequency3;

import java.util.Collection;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.processtree.ProcessTree;

import batch.infrequency2.GenerateLog2;

public class GenerateLog3 {

	@Plugin(name = "Generate log from process tree 3", returnLabels = { "Log" }, returnTypes = { XLog.class }, parameterLabels = { "Process Tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Generate log from process tree, default", requiredParameterLabels = { 0 })
	public XLog generateLog(UIPluginContext context, ProcessTree tree) throws Exception {
		return generateLog(tree, Infrequency3.numberOfDeviatingTraces[0]).getA();
	}

	public static Quadruple<XLog, Integer, Collection<XEventClass>, XEventClassifier> generateLog(ProcessTree tree, int numberOfDeviatingTraces) throws Exception {
		Pair<XLog, Integer> p = GenerateLog2.generateLog(tree, Infrequency3.logSize, Infrequency3.logSeed, Infrequency3.deviationsSeed, Infrequency3.probabilityOfDeviation, numberOfDeviatingTraces);
		XEventClassifier classifier = new XEventNameClassifier();
		Collection<XEventClass> eventClasses = XLogInfoFactory.createLogInfo(p.getA(), classifier).getEventClasses().getClasses();
		return Quadruple.of(p.getA(), p.getB(), eventClasses, classifier);
	}
	
	public static Quadruple<XLog, Integer, Collection<XEventClass>, XEventClassifier> fromLog(XLog log) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
		return Quadruple.of(log, 0, logInfo.getEventClasses().getClasses(), classifier);
	}
}