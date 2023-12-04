package caise2020multilevel;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.multilevelminer.multilevelmodel.MultiLevelModel;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.framework.CompareParameters;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.helperclasses.EfficientLog;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2EfficientTreePlugin;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2PetriNetPlugin;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import nl.tue.astar.AStarException;

public class MeasureProjected implements Measure {

	public String getTitle() {
		return "projected";
	}
	
	public String getLatexTitle() {
		return "projected";
	}

	public String[] getMeasureNames() {
		return new String[] { "fitnessP", "precisionP" };
	}
	
	public String[] getMeasureLatexNames() {
		return new String[] { "fitness", "precision" };
	}

	public boolean isSupportsTrees() {
		return true;
	}
	
	public boolean isSupportsBPMN() {
		return false;
	}

	public boolean isSupportsMultiLevelModels() {
		return false;
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	@Override
	public double[] compute(XLog log, XEventClassifier combinedClassifier, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException {
		CompareParameters parameters = new CompareParameters();
		parameters.setReduceAPN(true);
		parameters.setMultiThreading(true);
		parameters.setDebugEvery(1);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		EfficientLog eLog = new EfficientLog(log, combinedClassifier);
		ProjectedRecallPrecisionResult result = CompareLog2PetriNetPlugin.measure(eLog, aNet, parameters, canceller);

		return new double[] { result.getRecall(), result.getPrecision() };
	}

	@Override
	public double[] compute(XLog log, XEventClassifier combinedClassifier, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException {
		CompareParameters parameters = new CompareParameters();
		parameters.setDebugEvery(10000);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		EfficientLog eLog = new EfficientLog(log, combinedClassifier);
		ProjectedRecallPrecisionResult result = CompareLog2EfficientTreePlugin.measure(eLog, tree, parameters,
				canceller);

		return new double[] { result.getRecall(), result.getPrecision() };
	}

	public double[] compute(XLog log, MultiLevelModel model)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}

	public double[] compute(XLog log, XEventClassifier combinedClassifier, BPMNDiagram diagram)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException {
		return null;
	}
}