package batch.infrequency2;

import generation.CompareTrees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.plugins.InductiveMiner.Pair;
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

public class GenerateLog2 {
	
	public static class Counter {
		private int c = 0;
		public void inc() {
			c++;
		}
		public int get() {
			return c;
		}
	}

	public static Pair<XLog, Integer> generateLog(ProcessTree tree, int numberOfTraces, long logSeed, long deviationsSeed,
			double probabilityOfDeviation, int numberOfDeviatingTraces) throws Exception {
		XAttributeMap logMap = new XAttributeMapImpl();
		Random logRandom = new Random(logSeed);
		putLiteral(logMap, "concept:name", "generated log from process tree with noise");

		XLog result = new XLogImpl(logMap);

		//clean traces
		for (int t = 0; t < numberOfTraces; t++) {
			XAttributeMap traceMap = new XAttributeMapImpl();
			XTrace trace = generateTrace(tree.getRoot(), logRandom, traceMap, 0, null, null);
			putTraceName(trace, t, traceMap);
			result.add(trace);
		}

		//noisy traces
		Counter deviationsCounter = new Counter();
		for (int t = 0; t < numberOfDeviatingTraces; t++) {
			XAttributeMap traceMap = new XAttributeMapImpl();
			Random deviationsRandom = new Random(deviationsSeed + t);
			XTrace trace = generateTrace(tree.getRoot(), logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
			putTraceName(trace, -t, traceMap);
			result.add(trace);
		}
		
		return Pair.of(result, deviationsCounter.get());
	}

	private static void putTraceName(XTrace trace, int t, XAttributeMap traceMap) {
		List<String> name = new LinkedList<String>();
		for (XEvent e : trace) {
			name.add(e.getAttributes().get("concept:name").toString());
		}
		putLiteral(traceMap, "concept:name", t + " " + name.toString());
	}

	private static boolean isDeviation(double probabilityOfDeviation, Random deviationsRandom) {
		if (deviationsRandom == null) {
			return false;
		}
		return deviationsRandom.nextDouble() < probabilityOfDeviation;
	}

	private static XTrace generateTrace(Node node, Random logRandom, XAttributeMap traceMap,
			double probabilityOfDeviation, Random deviationsRandom, Counter deviationsCounter) throws Exception {
		if (node instanceof Automatic) {
			return generateTraceTau((Automatic) node, traceMap);
		} else if (node instanceof Manual) {
			return generateTraceActivity((Manual) node, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
		} else if (node instanceof Xor) {
			return generateTraceXor((Xor) node, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
		} else if (node instanceof Seq) {
			return generateTraceSeq((Seq) node, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
		} else if (node instanceof XorLoop) {
			return generateTraceXorLoop((XorLoop) node, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
		} else if (node instanceof And) {
			return generateTraceAnd((And) node, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
		}
		throw new Exception("not implemented");
	}

	private static XTrace generateTraceTau(Automatic tau, XAttributeMap traceMap) {
		XTrace result = new XTraceImpl(traceMap);
		return result;
	}

	private static XTrace generateTraceActivity(Manual activity, XAttributeMap traceMap, double probabilityOfDeviation,
			Random deviationsRandom, Counter deviationsCounter) {
		XTrace result = new XTraceImpl(traceMap);
		if (isDeviation(probabilityOfDeviation / 2, deviationsRandom)) {
			//deviation: skip activity
			deviationsCounter.inc();
			return result;
		}

		boolean addOneMoreActivity = true;
		while (addOneMoreActivity) {
			XAttributeMap attMap = new XAttributeMapImpl();
			putLiteral(attMap, "concept:name", activity.getName());
			putLiteral(attMap, "lifecycle:transition", "complete");
			putLiteral(attMap, "org:resource", "artificial");
			result.add(new Event(attMap));

			addOneMoreActivity = isDeviation(probabilityOfDeviation / 2, deviationsRandom);
			if (addOneMoreActivity) {
				deviationsCounter.inc();
			}
		}

		return result;
	}

	private static XTrace generateTraceXor(Xor node, Random logRandom, XAttributeMap traceMap,
			double probabilityOfDeviation, Random deviationsRandom, Counter deviationsCounter) throws Exception {

		XTrace result = new XTraceImpl(traceMap);
		boolean addOneMoreChild = true;
		while (addOneMoreChild) {
			int child = logRandom.nextInt(node.getChildren().size());
			List<Node> children = sortChildren(node);
			result.addAll(generateTrace(children.get(child), logRandom, traceMap, probabilityOfDeviation,
					deviationsRandom, deviationsCounter));

			addOneMoreChild = isDeviation(probabilityOfDeviation, deviationsRandom);
			if (addOneMoreChild) {
				deviationsCounter.inc();
			}
		}
		return result;
	}

	private static XTrace generateTraceSeq(Seq node, Random logRandom, XAttributeMap traceMap,
			double probabilityOfDeviation, Random deviationsRandom, Counter deviationsCounter) throws Exception {
		XTrace result = new XTraceImpl(traceMap);
		for (Node child : node.getChildren()) {
			result.addAll(generateTrace(child, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter));
		}

		//add deviations
		while (isDeviation(probabilityOfDeviation, deviationsRandom)) {
			deviationsCounter.inc();
			int child = deviationsRandom.nextInt(node.getChildren().size());
			List<Node> children = sortChildren(node);
			result.addAll(generateTrace(children.get(child), logRandom, traceMap, probabilityOfDeviation,
					deviationsRandom, deviationsCounter));
		}

		return result;
	}

	private static XTrace generateTraceXorLoop(XorLoop node, Random logRandom, XAttributeMap traceMap,
			double probabilityOfDeviation, Random deviationsRandom, Counter deviationsCounter) throws Exception {
		Node leftChild = node.getChildren().get(0);
		Node middleChild = node.getChildren().get(1);
		Node rightChild = node.getChildren().get(2);

		XTrace result = generateTrace(leftChild, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
		while (logRandom.nextInt(10) > 4) {
			result.addAll(generateTrace(middleChild, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter));
			result.addAll(generateTrace(leftChild, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter));
		}
		result.addAll(generateTrace(rightChild, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter));

		//a loop does not need a deviation

		return result;
	}

	private static XTrace generateTraceAnd(And node, Random logRandom, XAttributeMap traceMap, double probabilityOfDeviation,
			Random deviationsRandom, Counter deviationsCounter) throws Exception {
		int countChildren = node.getChildren().size();

		List<Node> children = sortChildren(node);

		//put all events in a single trace
		XTrace result = new XTraceImpl(traceMap);
		Iterator<XEvent>[] iterators = new Iterator[countChildren];
		{
			int branch = 0;
			for (Node child : children) {
				XTrace trace = generateTrace(child, logRandom, traceMap, probabilityOfDeviation, deviationsRandom, deviationsCounter);
				for (XEvent event : trace) {
					((Event) event).branch = branch;
				}
				iterators[branch] = trace.iterator();
				result.addAll(trace);
				branch++;
			}
		}

		//shuffle the result
		Collections.shuffle(result, logRandom);

		//order all traces from a single branch
		for (int e = 0; e < result.size(); e++) {
			int branch = ((Event) result.get(e)).branch;
			result.set(e, iterators[branch].next());
		}

		//add deviations
		while (isDeviation(probabilityOfDeviation, deviationsRandom)) {
			deviationsCounter.inc();
			int child = deviationsRandom.nextInt(node.getChildren().size());
			result.addAll(generateTrace(children.get(child), logRandom, traceMap, probabilityOfDeviation,
					deviationsRandom, deviationsCounter));
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