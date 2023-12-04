package coopis2018;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import thesis.helperClasses.FakeContext;

public class MeasureSimplicity implements Measure {

	private static PluginContext context = new FakeContext();

	public String getTitle() {
		return "simplicity";
	}

	public String[] getMeasureNames() {
		return new String[] { "number of nodes", "cyclomatic" };
	}

	public boolean isSupportsTrees() {
		return false;
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public double[] compute(XLog log, AcceptingPetriNet aNet) {
		double numberOfNodes = getNumberOfNodes(aNet);
		//double cyclomatic = (new PetriNetCyclomaticMetric()).compute(context, aNet.getNet(), aNet.getInitialMarking());
		double cyclomatic = Double.MIN_VALUE;
		return new double[] { numberOfNodes, cyclomatic };
	}

	public static double getNumberOfNodes(AcceptingPetriNet net) {
		return net.getNet().getNodes().size() + net.getNet().getEdges().size();
	}

	public double[] compute(XLog log, EfficientTree tree) {
		return null;
	}
	
	public int getCombinedMeasures() {
		return 1;
	}

}
