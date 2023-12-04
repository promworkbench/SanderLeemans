package is2020;

import java.io.File;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.DistributionType;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.ExecutionPolicy;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.TimeUnit;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import caise2020.FakeGraphLayoutConnection;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * A flower model in which each transition is equally likely, as well as
 * stopping.
 * 
 * @author sander
 *
 */
public class AlgorithmInductiveMinerStochasticDumb implements Algorithm {

	public String getName() {
		return "IM - equal weights";
	}

	public String getAbbreviation() {
		return "ARS";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {

		MiningParametersAbstract parameters = new MiningParametersIMInfrequent();
		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		parameters.setDebug(false);
		EfficientTree tree = InductiveMiner.mineEfficientTree(parameters.getIMLog(log), parameters, canceller);
		AcceptingPetriNet aNet = EfficientTree2AcceptingPetriNet.convert(tree);

		TObjectIntMap<String> activity2occurrences = new TObjectIntHashMap<>(10, 0.5f, 0);
		int sum = 0;
		{
			for (XTrace trace : log) {
				for (XEvent event : trace) {
					String activity = XConceptExtension.instance().extractName(event);
					activity2occurrences.adjustOrPutValue(activity, 1, 1);
					sum++;
				}
				sum++;
			}
		}

		//copy
		Map<PetrinetNode, PetrinetNode> old2new = new THashMap<>();

		StochasticNet net = new StochasticNetImpl(getName());
		net.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
		net.setTimeUnit(TimeUnit.HOURS);

		//places
		for (Place oldPlace : aNet.getNet().getPlaces()) {
			Place newPlace = net.addPlace(oldPlace.getLabel());
			old2new.put(oldPlace, newPlace);
		}

		//transitions
		for (Transition oldTransition : aNet.getNet().getTransitions()) {

			double weight = 1;
			if (!oldTransition.isInvisible()) {
				String activity = oldTransition.getLabel();
				weight = activity2occurrences.get(activity) / (sum * 1.0);
			}

			TimedTransition newTransition = net.addTimedTransition(oldTransition.getLabel(), weight,
					DistributionType.UNIFORM, 0.0, 200.0);
			newTransition.setInvisible(oldTransition.isInvisible());
			old2new.put(oldTransition, newTransition);
		}

		//edges
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> oldEdge : aNet.getNet().getEdges()) {
			PetrinetNode newSource = old2new.get(oldEdge.getSource());
			PetrinetNode newTarget = old2new.get(oldEdge.getTarget());
			if (newSource instanceof Place) {
				net.addArc((Place) newSource, (Transition) newTarget);
			} else {
				net.addArc((Transition) newSource, (Place) newTarget);
			}
		}

		Marking marking = new Marking();

		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking, new FakeGraphLayoutConnection(net));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

}
