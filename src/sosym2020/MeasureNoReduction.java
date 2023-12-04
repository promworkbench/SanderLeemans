package sosym2020;

import java.io.File;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;

import thesis.helperClasses.FakeContext;

public class MeasureNoReduction implements Measure {

	public String getTitle() {
		return "no reduction";
	}

	public String[] getMeasureNames() {
		return new String[] { "r__-t nodes", "r__-pn nodes" };
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
		int trees = tree.traverse(tree.getRoot());
		AcceptingPetriNet pn = EfficientTree2AcceptingPetriNet.convert(tree);
		int pns = getNumberOfNodes(pn);
		
		try {
			pn.exportToFile(new FakeContext(), new File("C:\\Users\\sander\\Desktop\\before.pnml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new double[] { trees, pns };
	}

	public int getCombinedMeasures() {
		return 1;
	}

}
