package sosym2020;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

public class MeasureTreeReduction implements Measure {

	public String getTitle() {
		return "tree reduction";
	}

	public String[] getMeasureNames() {
		return new String[] { "rt_-t nodes", "rt_-pn nodes" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public double[] compute(XLog log, AcceptingPetriNet aNet) {
		return new double[] { Double.MIN_VALUE, Double.MIN_VALUE };
	}

	public static int getNumberOfNodes(AcceptingPetriNet net) {
		return net.getNet().getNodes().size() + net.getNet().getEdges().size();
	}

	public double[] compute(XLog log, EfficientTree tree) {
		try {
			EfficientTreeReduce.reduce(tree);
		} catch (UnknownTreeNodeException | ReductionFailedException e) {
			e.printStackTrace();
		}
		int trees = tree.traverse(tree.getRoot());
		int pns = getNumberOfNodes(EfficientTree2AcceptingPetriNet.convert(tree));
		return new double[] { trees, pns };
	}

	public int getCombinedMeasures() {
		return 1;
	}

}
