package coopis2018;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import thesis.helperClasses.CheckRelaxedSoundnessWithLola;

public class MeasureBoundedness implements Measure {

	public String getTitle() {
		return "bounded";
	}

	public String[] getMeasureNames() {
		return new String[] { "bounded" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public double[] compute(XLog log, AcceptingPetriNet aNet) throws Exception {

		double bounded = CheckRelaxedSoundnessWithLola.isBounded(aNet) ? 1 : 0;

		return new double[] { bounded };
	}

	public double[] compute(XLog log, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException {
		return new double[] { 1 };
	}

	public int getCombinedMeasures() {
		return 1;
	}

}