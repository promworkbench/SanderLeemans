package caise2020multilevel;

import java.io.IOException;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.multilevelminer.multilevelmodel.SubModel;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureUserSize implements Measure {

	public String getTitle() {
		return "user nodes + edges";
	}

	public String getLatexTitle() {
		return "user";
	}

	public String[] getMeasureNames() {
		return new String[] { "min user nodes + edges", "avg user nodes + edges", "max user nodes + edges" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "min", "avg", "max" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public boolean isSupportsBPMN() {
		return false;
	}

	public boolean isSupportsMultiLevelModels() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 3;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException,
			IOException, ConnectionCannotBeObtained, JSONException, Exception {
		int size = getSize(aNet);
		return new double[] { size, size, size };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		double value = getSize(tree);
		return new double[] { value, value, value };
	}

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		Triple<Integer, Double, Integer> p = getSize(model.model);
		return new double[] { p.getA(), p.getB(), p.getC() };
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		int size = getSize(diagram);
		return new double[] { size, size, size };
	}

	public static double toDouble(Triple<Integer, Double, Integer> triple) {
		return triple.getB();
	}

	public static int getSize(AcceptingPetriNet aNet) {
		return aNet.getNet().getPlaces().size() + aNet.getNet().getTransitions().size()
				+ aNet.getNet().getEdges().size();
	}

	public static int getSize(BPMNDiagram diagram) {
		return diagram.getEdges().size() + diagram.getNodes().size();
	}

	public static int getSize(DirectlyFollowsModel dfm) {
		int result = dfm.getNumberOfNodes();
		for (long edge : dfm.getEdges()) {
			result++;
		}
		return result;
	}

	public static int getSize(EfficientTree tree) {
		return tree.getMaxNumberOfNodes() + tree.getMaxNumberOfNodes() - 1;
	}

	public static <M, T> Triple<Integer, Double, Integer> getSize(SubModel<M, T> model) {
		Quadruple<Integer, Long, Integer, Integer> p = getFromMLM(model);
		return Triple.of(p.getA(), p.getB() / (p.getC() * 1.0), p.getD());
	}

	private static <M, T> Quadruple<Integer, Long, Integer, Integer> getFromMLM(SubModel<M, T> model) {
		int value;
		if (model.getModel() instanceof AcceptingPetriNet) {
			value = getSize((AcceptingPetriNet) model.getModel());
		} else if (model.getModel() instanceof EfficientTree) {
			value = getSize((EfficientTree) model.getModel());
		} else if (model.getModel() instanceof DirectlyFollowsModel) {
			value = getSize((DirectlyFollowsModel) model.getModel());
		} else if (model.getModel() instanceof BPMNDiagram) {
			value = getSize((BPMNDiagram) model.getModel());
		} else {
			assert (false);
			return null;
		}

		int min = value;
		int count = 1;
		long sum = value;
		int max = value;
		for (T transition : model.getTransitions()) {
			if (model.hasSubModel(transition)) {
				Quadruple<Integer, Long, Integer, Integer> p = getFromMLM(model.getSubModel(transition));
				min = Math.min(min, p.getA());
				sum = sum + p.getB();
				count = count + p.getC();
				max = Math.max(max, p.getD());
			}
		}

		return Quadruple.of(min, sum, count, max);
	}
}