package caise2020isextension;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGen;
import org.processmining.plugins.pnalignanalysis.conformance.AlignmentPrecGenRes;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;

import nl.tue.astar.AStarException;
import svn45crimes.MeasureArya;
import thesis.helperClasses.FakeContext;

public class MeasureAlignments implements Measure {

	public String getTitle() {
		return "alignments";
	}

	public String getLatexTitle() {
		return "\\changed{}{alignments~\\cite{DBLP:journals/widm/AalstAD12,DBLP:conf/bpm/AdriansyahMCDA12}}";
	}

	public String[] getMeasureNames() {
		return new String[] { "fitness", "precision", "generalisation" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "\\changed{}{fitness}", "\\changed{}{precision}" };
	}

	public int getNumberOfMeasures() {
		return 2;
	}

	public boolean printTime() {
		return true;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet(model, initialMarking);
		Perform3Measures.fixFinalMarking(aNet);
		Triple<Double, TransEvClassMapping, PNRepResult> t = MeasureArya.AryaFitness(aNet, log);
		double fitness = t.getA();

		System.out.println("alignments done");

		AlignmentPrecGen precisionMeasurer2 = new AlignmentPrecGen();
		PluginContext context = new FakeContext();
		AlignmentPrecGenRes precisionGeneralisation = precisionMeasurer2
				.measureConformanceAssumingCorrectAlignment(context, t.getB(), t.getC(), model, initialMarking, true);

		System.out.println("precision done");
		double precision = precisionGeneralisation.getPrecision();
		double generalisation = precisionGeneralisation.getGeneralization();

		return new double[] { fitness, precision, generalisation };
	}
}