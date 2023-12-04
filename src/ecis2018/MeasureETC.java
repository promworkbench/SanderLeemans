package ecis2018;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.multietc.res.MultiETCResult;
import org.processmining.plugins.multietc.sett.MultiETCSettings;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

import nl.tue.astar.AStarException;

public class MeasureETC implements Measure {

	private static final int repeatPrecision = 5;

	public String getTitle() {
		return "ETC";
	}

	public String[] getMeasureNames() {
		String[] result = new String[1 + repeatPrecision];
		result[0] = "fitnessE";
		for (int i = 1; i < result.length; i++) {
			result[i] = "precisionE";
		}
		return result;
	}

	public boolean isSupportsTrees() {
		return true;
	}

	public int getNumberOfMeasures() {
		return 1 + repeatPrecision;
	}

	/**
	 * Given a particular accepting Petri net, the ETC method seems to be
	 * deterministic.
	 */
	public double[] compute(XLog log, AcceptingPetriNet aNet) throws AStarException {

		double[] result = new double[getNumberOfMeasures()];

		//fitness
		Triple<Double, TransEvClassMapping, PNRepResult> t = MeasureArya.AryaFitness(aNet, log);
		result[0] = t.getA();

		//precision
		PNRepResult alignment = t.getC();
		MultiETCSettings sett = new MultiETCSettings();
		sett.put(MultiETCSettings.REPRESENTATION, MultiETCSettings.Representation.ORDERED);
		AcceptingPetriNetMultiETCConformance etcp = new AcceptingPetriNetMultiETCConformance();
		Object[] result2 = etcp.checkMultiETCAlign1(log, aNet, sett, alignment);

		for (int i = 1; i < result.length; i++) {
			result[i] = (double) ((MultiETCResult) result2[0]).getAttribute(MultiETCResult.PRECISION);
		}

		return result;
	}

	public double[] compute(XLog log, EfficientTree tree) throws AStarException {
		double[] result = new double[getNumberOfMeasures()];

		//fitness
		{
			AcceptingPetriNet aNet = EfficientTree2AcceptingPetriNet.convert(tree);
			Triple<Double, TransEvClassMapping, PNRepResult> t = MeasureArya.AryaFitness(aNet, log);
			result[0] = t.getA();
		}

		//precision
		for (int i = 1; i < result.length; i++) {
			AcceptingPetriNet aNet = EfficientTree2AcceptingPetriNet.convert(tree);
			Triple<Double, TransEvClassMapping, PNRepResult> t = MeasureArya.AryaFitness(aNet, log);
			PNRepResult alignment = t.getC();
			MultiETCSettings sett = new MultiETCSettings();
			sett.put(MultiETCSettings.REPRESENTATION, MultiETCSettings.Representation.ORDERED);
			AcceptingPetriNetMultiETCConformance etcp = new AcceptingPetriNetMultiETCConformance();
			Object[] result2 = etcp.checkMultiETCAlign1(log, aNet, sett, alignment);
			result[i] = (double) ((MultiETCResult) result2[0]).getAttribute(MultiETCResult.PRECISION);
		}

		return result;
	}

	public int getCombinedMeasures() {
		return repeatPrecision;
	}

}
