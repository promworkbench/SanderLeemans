package sosym2020;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet.ReduceAcceptingPetriNetKeepLanguage;

public class MeasurePNReduction implements Measure {

	public String getTitle() {
		return "pn reduction";
	}

	public String[] getMeasureNames() {
		return new String[] { "r_p-pn nodes" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public double[] compute(XLog log, AcceptingPetriNet aNet) {
		return new double[] { Double.MIN_VALUE, Double.MIN_VALUE };
	}

	public static int getNumberOfNodes(AcceptingPetriNet net) {
		return net.getNet().getNodes().size() + net.getNet().getEdges().size();
	}

	public double[] compute(XLog log, EfficientTree tree) {
		AcceptingPetriNet pn = EfficientTree2AcceptingPetriNet.convert(tree);
		ReduceAcceptingPetriNetKeepLanguage.reduce(pn, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});
		int pns = getNumberOfNodes(pn);
		return new double[] { pns };
	}

	public int getCombinedMeasures() {
		return 1;
	}

}
