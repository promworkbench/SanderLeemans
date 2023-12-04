package generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.processtree.Block;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.impl.AbstractBlock;
import org.processmining.processtree.impl.AbstractBlock.And;
import org.processmining.processtree.impl.AbstractBlock.Seq;
import org.processmining.processtree.impl.AbstractBlock.Xor;
import org.processmining.processtree.impl.AbstractBlock.XorLoop;
import org.processmining.processtree.impl.AbstractTask.Automatic;
import org.processmining.processtree.impl.AbstractTask.Manual;
import org.processmining.xeslite.external.XFactoryExternalStore;

public class GenerateLog {

	private Random randomGenerator;
	private XAttributeMap traceMap;

	@Plugin(name = "Generate log from process tree", returnLabels = { "Log" }, returnTypes = { XLog.class }, parameterLabels = { "Process Tree" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Generate log from process tree, default", requiredParameterLabels = { 0 })
	public XLog generateLog(UIPluginContext context, ProcessTree tree) throws Exception {
		GenerateLogParameters parameters = new GenerateLogParameters();
		GenerateLogDialog dialog = new GenerateLogDialog(parameters);
		InteractionResult result = context.showWizard("Generate a log from a process tree", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return generateLog(tree, parameters);
	}

	/**
	 * Generate a trace that might be not replayable on the process tree.
	 * 
	 * @param tree
	 * @param noiseSeed
	 * @return
	 */
	public static XTrace generateNoisyTrace(ProcessTree tree, List<String> activities, long noiseSeed,
			int noiseEventsPerTrace) {
		Random random = new Random(noiseSeed);
		XAttributeMap traceMap = new XAttributeMapImpl();
		XTrace result = new XTraceImpl(traceMap);

		for (int i = 0; i < noiseEventsPerTrace; i++) {
			String activity = activities.get(random.nextInt(activities.size()));
			XAttributeMap attMap = new XAttributeMapImpl();
			GenerateLog.putLiteral(attMap, "concept:name", activity);
			GenerateLog.putLiteral(attMap, "lifecycle:transition", "complete");
			GenerateLog.putLiteral(attMap, "org:resource", "artificial");
			result.add(new GenerateLog.Event(attMap));
		}

		//construct trace name
		List<String> name = new LinkedList<String>();
		for (XEvent e : result) {
			name.add(e.getAttributes().get("concept:name").toString());
		}
		GenerateLog.putLiteral(traceMap, "concept:name", "noise " + noiseSeed + " " + name.toString());
		return result;
	}

	public XLog generateLog(ProcessTree tree, GenerateLogParameters parameters) throws Exception {
		XAttributeMap logMap = new XAttributeMapImpl();
		putLiteral(logMap, "concept:name", "generated log from process tree");
		randomGenerator = new Random(parameters.getSeed());
		XLog result = new XFactoryExternalStore.MapDBDiskImpl().createLog(logMap);
		
		//clean traces
		for (int t = 0; t < parameters.getNumberOfTraces(); t++) {
			traceMap = new XAttributeMapImpl();
			XTrace trace = generateTrace(tree.getRoot());

			//construct trace name
			List<String> name = new LinkedList<String>();
			for (XEvent e : trace) {
				name.add(e.getAttributes().get("concept:name").toString());
			}
			putLiteral(traceMap, "concept:name", t + " " + name.toString());

			result.add(trace);
		}

		//noisy traces
		List<String> activities = getActivities(tree.getRoot());
		for (int t = 0; t < parameters.getNoisyTraces(); t++) {
			result.add(generateNoisyTrace(tree, activities, parameters.getNoiseSeed() + t + parameters.getSeed(),
					parameters.getNoiseEventsPerTrace()));
		}

		return result;
	}

	private XTrace generateTrace(Node node) throws Exception {
		if (node instanceof Automatic) {
			return generateTraceTau((Automatic) node);
		} else if (node instanceof Manual) {
			return generateTraceActivity((Manual) node);
		} else if (node instanceof Xor) {
			return generateTraceXor((Xor) node);
		} else if (node instanceof Seq) {
			return generateTraceSeq((Seq) node);
		} else if (node instanceof XorLoop) {
			return generateTraceXorLoop((XorLoop) node);
		} else if (node instanceof And) {
			return generateTraceAnd((And) node);
		}
		throw new Exception("not implemented");
	}

	private XTrace generateTraceTau(Automatic tau) {
		XTrace result = new XTraceImpl(traceMap);
		return result;
	}

	private XTrace generateTraceActivity(Manual activity) {
		XTrace result = new XTraceImpl(traceMap);
		XAttributeMap attMap = new XAttributeMapImpl();
		putLiteral(attMap, "concept:name", activity.getName());
		putLiteral(attMap, "lifecycle:transition", "complete");
		putLiteral(attMap, "org:resource", "artificial");
		result.add(new Event(attMap));
		return result;
	}

	private XTrace generateTraceXor(Xor node) throws Exception {
		int child = randomGenerator.nextInt(node.getChildren().size());
		List<Node> children = sortChildren(node);
		XTrace result = generateTrace(children.get(child));
		return result;
	}

	private XTrace generateTraceSeq(Seq node) throws Exception {
		XTrace result = new XTraceImpl(traceMap);
		for (Node child : node.getChildren()) {
			result.addAll(generateTrace(child));
		}
		return result;
	}

	private XTrace generateTraceXorLoop(XorLoop node) throws Exception {
		Node leftChild = node.getChildren().get(0);
		Node middleChild = node.getChildren().get(1);
		Node rightChild = node.getChildren().get(2);

		XTrace result = generateTrace(leftChild);
		while (randomGenerator.nextInt(10) > 4) {
			result.addAll(generateTrace(middleChild));
			result.addAll(generateTrace(leftChild));
		}
		result.addAll(generateTrace(rightChild));
		return result;
	}

	private XTrace generateTraceAnd(And node) throws Exception {
		int countChildren = node.getChildren().size();

		List<Node> children = sortChildren(node);

		//put all events in a single trace
		XTrace result = new XTraceImpl(traceMap);
		Iterator<XEvent>[] iterators = new Iterator[countChildren];
		{
			int branch = 0;
			for (Node child : children) {
				XTrace trace = generateTrace(child);
				for (XEvent event : trace) {
					((Event) event).branch = branch;
				}
				iterators[branch] = trace.iterator();
				result.addAll(trace);
				branch++;
			}
		}

		//shuffle the result
		Collections.shuffle(result, randomGenerator);

		//order all traces from a single branch
		for (int e = 0; e < result.size(); e++) {
			int branch = ((Event) result.get(e)).branch;
			result.set(e, iterators[branch].next());
		}

		return result;
	}

	private final static class CustomComparator implements Comparator<Node> {
		public int compare(Node o1, Node o2) {
			return CompareTrees.hash(o1).compareTo(CompareTrees.hash(o2));
		}
	}

	private static List<Node> sortChildren(AbstractBlock node) {
		List<Node> result = new ArrayList<Node>(node.getChildren());
		Collections.sort(result, new CustomComparator());
		return result;
	}

	public static void putLiteral(XAttributeMap attMap, String key, String value) {
		attMap.put(key, new XAttributeLiteralImpl(key, value));
	}

	public static class Event extends XEventImpl {
		public int branch = 0;

		public Event(XAttributeMap attMap) {
			super(attMap);
		}
	}

	/**
	 * Get a list of all manual leaves of a tree.
	 * 
	 * @param node
	 * @return
	 */
	public static List<String> getActivities(Node node) {
		List<String> result = new ArrayList<>();
		if (node instanceof Manual) {
			result.add(((Manual) node).getName());
		} else if (node instanceof Block) {
			for (Node child : ((Block) node).getChildren()) {
				result.addAll(getActivities(child));
			}
		}
		return result;
	}
}