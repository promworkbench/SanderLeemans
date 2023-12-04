package thesis.helperClasses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.processtree.Event;
import org.processmining.processtree.Event.Message;
import org.processmining.processtree.Event.TimeOut;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.InvalidProcessTreeException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.NotYetImplementedException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.UnfoldedNode;

public class Simplicity {
	
	public static int measure(AcceptingPetriNet net) {
		return net.getNet().getNodes().size() + net.getNet().getEdges().size();
	}

	public static int measure(EfficientTree tree) throws NotYetImplementedException, InvalidProcessTreeException {
		int result = 0;

		//Place source = petrinet.addPlace("source " + placeCounter.incrementAndGet());
		//Place sink = petrinet.addPlace("sink " + placeCounter.incrementAndGet());
		result += 2;
		//		Marking initialMarking = new Marking();
		//		initialMarking.add(source);
		//		Marking finalMarking = new Marking();
		//		finalMarking.add(sink);

		Map<UnfoldedNode, Set<Transition>> mapPath2Transitions = new HashMap<UnfoldedNode, Set<Transition>>();
		Map<Transition, UnfoldedNode> mapTransition2Path = new HashMap<Transition, UnfoldedNode>();

		result += convertNode(tree, tree.getRoot(), false, false, mapPath2Transitions, mapTransition2Path);
		
		return result;
	}

	/**
	 * Dirty, but here we copy code from the ProcessTree package, as that is not
	 * in a compilable shape at time of writing, and progress is to be made...
	 * 
	 * @param unode
	 * @param source
	 * @param sink
	 * @param petrinet
	 * @param forbiddenToPutTokensInSource
	 * @param keepStructure
	 * @param mapPath2Transitions
	 * @param mapTransition2Path
	 * @throws NotYetImplementedException
	 * @throws InvalidProcessTreeException
	 */

	private static int convertNode(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) throws NotYetImplementedException,
			InvalidProcessTreeException {
		if (tree.isTau(node)) {
			return convertTau(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else if (tree.isActivity(node)) {
			return convertTask(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else if (tree.isConcurrent(node)) {
			return convertAnd(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else if (tree.isSequence(node)) {
			return convertSeq(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else if (tree.isXor(node)) {
			return convertXor(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else if (tree.isLoop(node)) {
			return convertXorLoop(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else if (tree.isOr(node)) {
			return convertOr(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else if (tree.isInterleaved(node)) {
			return convertAnd(tree, node, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
					mapTransition2Path);
		} else {
			throw new NotYetImplementedException();
		}
	}

	private static int convertTau(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) {
		//		Transition t = petrinet.addTransition("tau from tree");
		//		addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
		//		t.setInvisible(true);
		//		petrinet.addArc(source, t);
		//		petrinet.addArc(t, sink);

		return 3;
	}

	private static int convertTask(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) {
		//		Transition t = petrinet.addTransition(unode.getNode().getName());
		//		addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
		//		petrinet.addArc(source, t);
		//		petrinet.addArc(t, sink);

		return 3;
	}

	private static int convertXor(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) throws NotYetImplementedException,
			InvalidProcessTreeException {
		int result = 0;
		//		Block node = unode.getBlock();
		for (int child : tree.getChildren(node)) {
			result += convertNode(tree, child, true, keepStructure, mapPath2Transitions, mapTransition2Path);
		}
		return result;
	}

	private static int convertSeq(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) throws NotYetImplementedException,
			InvalidProcessTreeException {
		int result = 0;
		int last = tree.getNumberOfChildren(node);
		int i = 0;
		//Place lastSink = source;
		for (int child : tree.getChildren(node)) {
			Place childSink;
			if (i == last - 1) {
				//childSink = sink;
			} else {
				//childSink = petrinet.addPlace("sink " + placeCounter.incrementAndGet());
				result++;
			}

			result += convertNode(tree, child, false, keepStructure, mapPath2Transitions, mapTransition2Path);
			//			lastSink = childSink;
			i++;
		}
		return result;
	}

	private static int convertAnd(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) throws NotYetImplementedException,
			InvalidProcessTreeException {
		int result = 0;

		//add split tau
		//		Transition t1 = petrinet.addTransition("tau split");
		//		addTransition(unode, t1, mapPath2Transitions, mapTransition2Path);
		//		t1.setInvisible(true);
		//		petrinet.addArc(source, t1);
		result += 2;

		//add join tau
		//		Transition t2 = petrinet.addTransition("tau join");
		//		addTransition(unode, t2, mapPath2Transitions, mapTransition2Path);
		//		t2.setInvisible(true);
		//		petrinet.addArc(t2, sink);
		result += 2;

		//add for each child a source and sink place
		for (int child : tree.getChildren(node)) {
			//			Place childSource = petrinet.addPlace("source " + placeCounter.incrementAndGet());
			//			petrinet.addArc(t1, childSource);
			result += 2;

			//			Place childSink = petrinet.addPlace("sink " + placeCounter.incrementAndGet());
			//			petrinet.addArc(childSink, t2);
			result += 2;

			result += convertNode(tree, child, false, keepStructure, mapPath2Transitions, mapTransition2Path);
		}
		return result;
	}

	private static int convertXorLoop(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) throws NotYetImplementedException,
			InvalidProcessTreeException {
		int result = 0;
		//		if (unode.getBlock().getChildren().size() != 3) {
		//			//a loop must have precisely three children: body, redo and exit
		//			throw new InvalidProcessTreeException();
		//		}

		//		Place middlePlace = petrinet.addPlace("middle " + placeCounter.incrementAndGet());
		result++;
		if (forbiddenToPutTokensInSource || keepStructure) {
			//add an extra tau
			//			Transition t = petrinet.addTransition("tau start");
			//			addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
			//			t.setInvisible(true);
			//			petrinet.addArc(source, t);
			result += 2;

			//replace the source
			//			source = petrinet.addPlace("replacement source " + placeCounter.incrementAndGet());
			//			petrinet.addArc(t, source);
			result += 2;
		}

		//body
		result += convertNode(tree, tree.getChild(node, 0), true, keepStructure, mapPath2Transitions,
				mapTransition2Path);
		//redo
		result += convertNode(tree, tree.getChild(node, 1), true, keepStructure, mapPath2Transitions,
				mapTransition2Path);
		//exit
		result += convertNode(tree, tree.getChild(node, 2), true, keepStructure, mapPath2Transitions,
				mapTransition2Path);

		return result;
	}

	private static int convertOr(EfficientTree tree, int node, boolean forbiddenToPutTokensInSource,
			boolean keepStructure, Map<UnfoldedNode, Set<Transition>> mapPath2Transitions,
			Map<Transition, UnfoldedNode> mapTransition2Path) throws NotYetImplementedException,
			InvalidProcessTreeException {
		int result = 0;

		//		Transition start = petrinet.addTransition("tau start");
		//		addTransition(unode, start, mapPath2Transitions, mapTransition2Path);
		//		start.setInvisible(true);
		//		petrinet.addArc(source, start);
		result += 2;

		//		Place notDoneFirst = petrinet.addPlace("notDoneFirst " + placeCounter.incrementAndGet());
		//		petrinet.addArc(start, notDoneFirst);
		result += 2;

		//		Place doneFirst = petrinet.addPlace("doneFirst " + placeCounter.incrementAndGet());
		//		Transition end = petrinet.addTransition("tau finish");
		//		addTransition(unode, end, mapPath2Transitions, mapTransition2Path);
		//		end.setInvisible(true);
		//		petrinet.addArc(doneFirst, end);
		//		petrinet.addArc(end, sink);
		result += 4;

		for (int child : tree.getChildren(node)) {
			//			Place childSource = petrinet.addPlace("childSource " + placeCounter.incrementAndGet());
			//			petrinet.addArc(start, childSource);
			//			Place childSink = petrinet.addPlace("childSink " + placeCounter.incrementAndGet());
			//			petrinet.addArc(childSink, end);
			//			Place doChild = petrinet.addPlace("doChild " + placeCounter.incrementAndGet());
			result += 5;

			//skip
			//			Transition skipChild = petrinet.addTransition("tau skipChild");
			//			addTransition(unode, skipChild, mapPath2Transitions, mapTransition2Path);
			//			skipChild.setInvisible(true);
			//			petrinet.addArc(childSource, skipChild);
			//			petrinet.addArc(skipChild, childSink);
			//			petrinet.addArc(skipChild, doneFirst);
			//			petrinet.addArc(doneFirst, skipChild);
			result += 5;

			//first do
			//			Transition firstDoChild = petrinet.addTransition("tau firstDoChild");
			//			addTransition(unode, firstDoChild, mapPath2Transitions, mapTransition2Path);
			//			firstDoChild.setInvisible(true);
			//			petrinet.addArc(childSource, firstDoChild);
			//			petrinet.addArc(notDoneFirst, firstDoChild);
			//			petrinet.addArc(firstDoChild, doneFirst);
			//			petrinet.addArc(firstDoChild, doChild);
			result += 5;

			//later do
			//			Transition laterDoChild = petrinet.addTransition("tau laterDoChild");
			//			addTransition(unode, laterDoChild, mapPath2Transitions, mapTransition2Path);
			//			laterDoChild.setInvisible(true);
			//			petrinet.addArc(childSource, laterDoChild);
			//			petrinet.addArc(laterDoChild, doChild);
			//			petrinet.addArc(laterDoChild, doneFirst);
			//			petrinet.addArc(doneFirst, laterDoChild);
			result += 5;

			result += convertNode(tree, child, false, keepStructure, mapPath2Transitions, mapTransition2Path);
		}
		return result;
	}

	@SuppressWarnings("unused")
	private static String getEventLabel(Event e) throws NotYetImplementedException {
		if (e instanceof Message) {
			return "message " + e.getMessage();
		} else if (e instanceof TimeOut) {
			return "time out " + e.getMessage();
		}
		throw new NotYetImplementedException();
	}

	protected static void addTransition(UnfoldedNode unode, Transition t,
			Map<UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, UnfoldedNode> mapTransition2Path) {
		if (mapPath2Transitions.get(unode) == null) {
			mapPath2Transitions.put(unode, new HashSet<Transition>());
		}
		mapPath2Transitions.get(unode).add(t);

		mapTransition2Path.put(t, unode);
	}
}
