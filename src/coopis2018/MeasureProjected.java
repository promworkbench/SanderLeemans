package coopis2018;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.ProMCanceller;
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

	public String[] getMeasureNames() {
		return new String[] { "fitnessP", "precisionP" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public double[] compute(XLog log, AcceptingPetriNet aNet)
			throws AStarException, ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException {
		CompareParameters parameters = new CompareParameters();
		parameters.setDebugEvery(100);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		EfficientLog eLog = new EfficientLog(log, new XEventNameClassifier());
		ProjectedRecallPrecisionResult result = CompareLog2PetriNetPlugin.measure(eLog, aNet, parameters, canceller);

		return new double[] { result.getRecall(), result.getPrecision() };
	}

	public double[] compute(XLog log, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException {
		CompareParameters parameters = new CompareParameters();
		parameters.setDebugEvery(10000);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		EfficientLog eLog = new EfficientLog(log, new XEventNameClassifier());
		ProjectedRecallPrecisionResult result = CompareLog2EfficientTreePlugin.measure(eLog, tree, parameters,
				canceller);

		return new double[] { result.getRecall(), result.getPrecision() };
	}

	public int getCombinedMeasures() {
		return 1;
	}

}
