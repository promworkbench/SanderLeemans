package batch.miners;


import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;

public class ETM extends Miner {
	
	public String getIdentification() {
		return "Evolutionary Tree Miner";
	}

	public String getIdentificationShort() {
		return "ETM";
	}
	
	//private NAryTree internalTree;

	public Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> mine(
			PluginContext context,
			XLog log,
			XEventClassifier classifier, 
			XLogInfo logInfo,
			Integer maxMiningTime) throws Exception {
		
		/*ETMParamSimple parameters = new ETMParamSimple();
		parameters.setEventlog(log);
		parameters.setContext(context);
		parameters.setMaxDuration(maxMiningTime*1000);
		org.processmining.plugins.etm.ETM miner = new org.processmining.plugins.etm.ETM(parameters.createETMParams());
		miner.run();
		
		internalTree = miner.getResult();
		
		System.out.println("Tree: " + TreeUtils.toString(internalTree, logInfo.getEventClasses()));
		
		//convert to Process Tree
		ProcessTree tree = NAryTreeToProcessTree.convert(logInfo.getEventClasses(), internalTree);
		debug("ProcessTree " + tree.toString());
		PetrinetWithMarkings tr = ProcessTree2Petrinet.convert(tree);
		Petrinet petrinet = tr.petrinet;
		Marking initialMarking = tr.initialMarking;
		Marking finalMarking = tr.finalMarking;
		
		TransEvClassMapping mapping = getTransEvClassMapping(classifier, logInfo, petrinet);
		
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(petrinet, initialMarking, finalMarking, mapping);*/
		return new Quadruple<Petrinet, Marking, Marking, TransEvClassMapping>(null, null, null, null);
	}
	
	@Override
	protected XEventClass mapTransitionToEventClass(Transition t, XEventClasses activities) throws unmappableTransitionException {
		String label = t.getLabel();
		
		//extract the activity name from ETM data structure
		//if I guess correctly, it's something like 'LEAF: 10'. This last number is the node number,
		//for which we look up the 'type', which is the index of the activity in the list
		
		//update: it seems this number is just the activity index
		debug("");
		debug("label: " + label);
		debug(activities.getClasses().toString());
		if (label.startsWith("LEAF: ")) {
			//int nodeNr = Integer.valueOf(label.substring(6));
			//debug("node nr: " + String.valueOf(nodeNr));
			//debug(TreeUtils.toString(internalTree, nodeNr, activities));
			//label = activities.getByIndex(nodeNr).toString();
			
			label = label.substring(6);
		}
		
		//find the event class with the same label as the transition
		for (XEventClass activity : activities.getClasses()) {
			if (label.equals(activity.toString())) {
				return activity;
			}
		}
		debug("no XEventClass found for transition " + label);
		throw new unmappableTransitionException(t);
	}
}
