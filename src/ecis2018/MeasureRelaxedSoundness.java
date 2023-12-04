package ecis2018;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.petrinet.behavioralanalysis.woflan.Woflan;
import org.processmining.plugins.petrinet.behavioralanalysis.woflan.WoflanDiagnosis;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;

import thesis.helperClasses.CheckRelaxedSoundnessWithLola;
import thesis.helperClasses.FakeContextThatKeepsMarking;

public class MeasureRelaxedSoundness implements Measure {

	public String getTitle() {
		return "relaxed soundness";
	}

	public String[] getMeasureNames() {
		return new String[] { "relaxeded sound" };
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public double[] compute(XLog log, AcceptingPetriNet aNet) throws Exception {

		double relaxedSound = CheckRelaxedSoundnessWithLola.isRelaxedSound(aNet) ? 1 : 0;

		return new double[] { relaxedSound };
	}

	public double[] compute(XLog log, EfficientTree tree)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException {
		return new double[] { 1 };
	}

	public int getCombinedMeasures() {
		return 1;
	}

	public static boolean isSoundUsingWoflan(AcceptingPetriNet aNet) throws Exception {
		PluginContext context = new FakeContextThatKeepsMarking(aNet.getInitialMarking(), aNet.getFinalMarkings());
		WoflanDiagnosis diagnosis = new Woflan().diagnose(context, aNet.getNet());
		return diagnosis.isSound();
	}
}