package thesis.evaluation.standAloneMiners;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.log.LogFilter;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

import com.raffaeleconforti.log.util.LogImporter;
import com.raffaeleconforti.log.util.LogReaderClassic;
import com.raffaeleconforti.wrapper.marking.MarkingDiscoverer;

import thesis.evaluation.standAloneMiners.tsinghuaalpha.TsinghuaAlphaProcessMiner;
import thesis.helperClasses.FakeContext;

public class RunTsinghuaAlphaMiner {
	
	public static void main(String[] args) throws Exception {
		boolean help = false;
		File file = null;
		if (args.length != 1) {
			help = true;
		} else {
			file = new File(args[0]);
			help = help || !file.exists();
		}

		if (help) {
			System.out.println("Usage: TsinghuaAlphaMiner.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}
		
		XLog log = RunInductiveMiner.loadLog(file);
		mine(log);
		
		ExecutorService e = (ExecutorService) new FakeContext().getExecutor();
		e.shutdown();
	}
	
	public static AcceptingPetriNet mine(XLog log) throws Exception {
		LogImporter.exportToFile("", "tmpLog.mxml.gz", log);
		LogFile logFile = LogFile.getInstance("tmpLog.mxml.gz");
		LogReader logReader = LogReaderClassic.createInstance((LogFilter) null, logFile);
		//logReader does not initialise properly, so trigger that with toString().
		logReader.toString();
		
		TsinghuaAlphaProcessMiner miner = new TsinghuaAlphaProcessMiner();
		PetriNetResult miningResult = (PetriNetResult) miner.mine(logReader);
		PetriNet prom5PetriNet = miningResult.getPetriNet();
		Petrinet petriNet = getPetrinet(prom5PetriNet);

		return AcceptingPetriNetFactory.createAcceptingPetriNet(petriNet,
				MarkingDiscoverer.constructInitialMarking(new FakeContext(), petriNet),
				MarkingDiscoverer.constructFinalMarking(new FakeContext(), petriNet));
	}

	private static Petrinet getPetrinet(PetriNet result) {
		PetrinetImpl petrinet = new PetrinetImpl("TsinghuaAlpha");
		UnifiedMap<Transition, org.processmining.models.graphbased.directed.petrinet.elements.Transition> transitionUnifiedMap = new UnifiedMap<Transition, org.processmining.models.graphbased.directed.petrinet.elements.Transition>();
		UnifiedMap<Place, org.processmining.models.graphbased.directed.petrinet.elements.Place> placeUnifiedMap = new UnifiedMap<Place, org.processmining.models.graphbased.directed.petrinet.elements.Place>();
		Iterator<?> arg4 = result.getTransitions().iterator();

		Transition t;
		while (arg4.hasNext()) {
			t = (Transition) arg4.next();
			org.processmining.models.graphbased.directed.petrinet.elements.Transition place = petrinet
					.addTransition(t.getLogEvent().getModelElementName());
			place.setInvisible(t.isInvisibleTask());
			transitionUnifiedMap.put(t, place);
		}

		arg4 = result.getPlaces().iterator();

		while (arg4.hasNext()) {
			Place t1 = (Place) arg4.next();
			org.processmining.models.graphbased.directed.petrinet.elements.Place place2 = petrinet
					.addPlace(t1.getName());
			placeUnifiedMap.put(t1, place2);
		}

		arg4 = result.getTransitions().iterator();

		while (arg4.hasNext()) {
			t = (Transition) arg4.next();
			Iterator<Place> place3 = result.getPlaces().iterator();

			while (place3.hasNext()) {
				Place p = place3.next();
				org.processmining.models.graphbased.directed.petrinet.elements.Transition transition = transitionUnifiedMap
						.get(t);
				org.processmining.models.graphbased.directed.petrinet.elements.Place place1 = placeUnifiedMap.get(p);
				if (result.findEdge(t, p) != null) {
					petrinet.addArc(transition, place1);
				}

				if (result.findEdge(p, t) != null) {
					petrinet.addArc(place1, transition);
				}
			}
		}

		return petrinet;
	}
}
