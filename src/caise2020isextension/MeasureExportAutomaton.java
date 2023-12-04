package caise2020isextension;

import java.io.File;
import java.io.FileWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMapped;
import org.processmining.stochasticawareconformancechecking.helperclasses.StochasticPetriNet2StochasticDeterministicFiniteAutomaton2;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;

import nl.tue.astar.AStarException;

public class MeasureExportAutomaton implements Measure {

	public String getTitle() {
		return "expAut";
	}

	public String getLatexTitle() {
		return "expAut";
	}

	public String[] getMeasureNames() {
		return new String[] { "void" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "void" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return false;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {
		StochasticDeterministicFiniteAutomatonMapped automatona = StochasticPetriNet2StochasticDeterministicFiniteAutomaton2
				.convert(model, initialMarking);

		String automatons = MeasureEntropicRelevance.automaton2json(automatona);

		File outputFile = new File(call.getMeasureFile(this).getAbsolutePath() + ".json");
		FileWriter writer = new FileWriter(outputFile);
		org.apache.commons.io.IOUtils.write(automatons, writer);
		writer.close();

		return new double[] { -1 };
	}

}
