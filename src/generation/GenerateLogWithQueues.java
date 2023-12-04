package generation;

import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
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

public class GenerateLogWithQueues {

	private Random randomGenerator;
	private XAttributeMap traceMap;
	private Map<String, TLongArrayList> resourcesAvailableAt;
	private final static int nrOfResourcesPerActivity = 5;
	private final static double lambdaTransit = 1;
	private final static double lambdaService = 3;

	@Plugin(name = "Generate log with queues from process tree", returnLabels = { "Log" }, returnTypes = { XLog.class }, parameterLabels = { "Process Tree" }, userAccessible = true)
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

	public XLog generateLog(ProcessTree tree, GenerateLogParameters parameters) throws Exception {
		XAttributeMap logMap = new XAttributeMapImpl();
		GenerateLog.putLiteral(logMap, "concept:name", "generated log from process tree");
		randomGenerator = new Random(parameters.getSeed());

		//set up the resources
		resourcesAvailableAt = new THashMap<>();
		for (String activity : getActivities(tree.getRoot())) {
			resourcesAvailableAt.put(activity, new TLongArrayList());
			for (int i = 0; i < nrOfResourcesPerActivity; i++) {
				resourcesAvailableAt.get(activity).add(0);
			}
		}

		XLog result = new XFactoryExternalStore.MapDBDiskImpl().createLog(logMap);

		for (int t = 0; t < parameters.getNumberOfTraces(); t++) {
			traceMap = new XAttributeMapImpl();
			XTrace trace = generateTrace(tree.getRoot(), 0);

			//construct trace name
			List<String> name = new LinkedList<String>();
			for (XEvent e : trace) {
				name.add(e.getAttributes().get("concept:name").toString());
			}
			GenerateLog.putLiteral(traceMap, "concept:name", t + " " + name.toString());

			result.add(trace);
		}

		return result;
	}

	private XTrace generateTrace(Node node, long initiate) throws Exception {
		if (node instanceof Automatic) {
			return generateTraceTau((Automatic) node);
		} else if (node instanceof Manual) {
			return generateTraceActivity((Manual) node, initiate);
		} else if (node instanceof Xor) {
			return generateTraceXor((Xor) node, initiate);
		} else if (node instanceof Seq) {
			return generateTraceSeq((Seq) node, initiate);
		} else if (node instanceof XorLoop) {
			return generateTraceXorLoop((XorLoop) node, initiate);
		} else if (node instanceof And) {
			return generateTraceAnd((And) node, initiate);
		}
		throw new Exception("not implemented");
	}

	private XTrace generateTraceTau(Automatic tau) {
		XTrace result = new XTraceImpl(traceMap);
		return result;
	}

	private XTrace generateTraceActivity(Manual activity, long initiate) {
		XTrace result = new XTraceImpl(traceMap);

		long enqueue = initiate + drawExponential(randomGenerator, lambdaTransit);
		long duration = drawExponential(randomGenerator, lambdaService);
		long start = Math.max(initiate, claimResource(activity.getName(), initiate, duration));
		long complete = start + duration;

		{
			XAttributeMap attMap = new XAttributeMapImpl();
			GenerateLog.putLiteral(attMap, "concept:name", activity.getName());
			GenerateLog.putLiteral(attMap, "lifecycle:transition", "enqueue");
			GenerateLog.putLiteral(attMap, "org:resource", "artificial");
			XEvent event = new Event(attMap);
			XTimeExtension.instance().assignTimestamp(event, enqueue);
			result.add(event);
		}
		{
			XAttributeMap attMap = new XAttributeMapImpl();
			GenerateLog.putLiteral(attMap, "concept:name", activity.getName());
			GenerateLog.putLiteral(attMap, "lifecycle:transition", "start");
			GenerateLog.putLiteral(attMap, "org:resource", "artificial");
			XEvent event = new Event(attMap);
			XTimeExtension.instance().assignTimestamp(event, start);
			result.add(event);
		}
		{
			XAttributeMap attMap = new XAttributeMapImpl();
			GenerateLog.putLiteral(attMap, "concept:name", activity.getName());
			GenerateLog.putLiteral(attMap, "lifecycle:transition", "complete");
			GenerateLog.putLiteral(attMap, "org:resource", "artificial");
			XEvent event = new Event(attMap);
			
			XTimeExtension.instance().assignTimestamp(event, complete);
			result.add(event);
		}
		return result;
	}

	private XTrace generateTraceXor(Xor node, long initiate) throws Exception {
		int child = randomGenerator.nextInt(node.getChildren().size());
		List<Node> children = sortChildren(node);
		XTrace result = generateTrace(children.get(child), initiate);
		return result;
	}

	private XTrace generateTraceSeq(Seq node, long initiate) throws Exception {
		XTrace result = new XTraceImpl(traceMap);
		for (Node child : node.getChildren()) {
			result.addAll(generateTrace(child, initiate));
			initiate = getLastTimestamp(result);
		}
		return result;
	}

	private XTrace generateTraceXorLoop(XorLoop node, long initiate) throws Exception {
		Node leftChild = node.getChildren().get(0);
		Node middleChild = node.getChildren().get(1);
		Node rightChild = node.getChildren().get(2);

		XTrace result = generateTrace(leftChild, initiate);
		while (randomGenerator.nextInt(10) > 4) {
			result.addAll(generateTrace(middleChild, getLastTimestamp(result)));
			result.addAll(generateTrace(leftChild, getLastTimestamp(result)));
		}
		result.addAll(generateTrace(rightChild, getLastTimestamp(result)));
		return result;
	}

	private XTrace generateTraceAnd(And node, long initiate) throws Exception {
		int countChildren = node.getChildren().size();

		List<Node> children = sortChildren(node);

		//put all events in a single trace
		XTrace result = new XTraceImpl(traceMap);
		Iterator<XEvent>[] iterators = new Iterator[countChildren];
		{
			int branch = 0;
			for (Node child : children) {
				XTrace trace = generateTrace(child, initiate);
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

	/**
	 * Searches for and yields a resource that will execute the activity.
	 * 
	 * @param activity
	 * @param from
	 * @param duration
	 * @return the start time
	 */
	public long claimResource(String activity, long from, long duration) {
		TLongArrayList l = resourcesAvailableAt.get(activity);
		long start = Math.max(from, l.get(0));
		long complete = start + duration;
		l.set(0, complete);
		l.sort();
		return start;
	}

	public static long drawExponential(Random r, double lambda) {
		return (long) ((Math.log(1 - r.nextFloat()) / -lambda) * 60000);
	}

	public static long getLastTimestamp(XTrace trace) {
		return XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)).getTime();
	}
}