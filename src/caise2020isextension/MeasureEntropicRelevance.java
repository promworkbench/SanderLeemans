package caise2020isextension;

import java.io.File;
import java.io.FileWriter;

import org.deckfour.xes.model.XLog;
import org.jbpt.pm.models.SAutomaton;
import org.jbpt.pm.relevance.Relevance;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomaton.EdgeIterable;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMapped;
import org.processmining.stochasticawareconformancechecking.helperclasses.StochasticPetriNet2StochasticDeterministicFiniteAutomaton2;

public class MeasureEntropicRelevance implements Measure {

	public String getTitle() {
		return "entrel";
	}

	public String getLatexTitle() {
		return "ER~\\cite{DBLP:conf/icpm/PolyvyanyyMG20}";
	}

	public String[] getMeasureNames() {
		return new String[] { "entropic relevance" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "relevance" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return true;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call) throws Exception {
		StochasticDeterministicFiniteAutomatonMapped automatona = StochasticPetriNet2StochasticDeterministicFiniteAutomaton2
				.convert(model, initialMarking);

		String automatons = automaton2json(automatona);

		File tempFile = File.createTempFile("bladiebladiebla", ".json");
		FileWriter writer = new FileWriter(tempFile);
		org.apache.commons.io.IOUtils.write(automatons, writer);
		writer.close();
		
		System.out.println(tempFile.getAbsolutePath());

		SAutomaton sa = SAutomaton.readJSON(tempFile.getAbsolutePath());

		String relevance = Relevance.compute(log, sa, false).toString();

		relevance = relevance.substring(relevance.indexOf(61) + 1, relevance.indexOf(125));
		System.out.println(String.format("Relevance: %s", relevance));

		tempFile.delete();

		return new double[] { Double.valueOf(relevance) };
	}

	public static String automaton2json(StochasticDeterministicFiniteAutomatonMapped automaton) {
		StringBuilder result = new StringBuilder();
		result.append("{");
		result.append("\"initialState\": " + automaton.getInitialState() + ",");
		result.append("\"transitions\": [");

		for (EdgeIterable it = automaton.getEdgesIterator(); it.hasNext();) {
			it.next();

			int from = it.getSource();
			int to = it.getTarget();
			double probability = it.getProbability();
			String label = automaton.transform(it.getActivity());

			result.append("{\"from\":" + from + ",\"to\":" + to + ",\"label\":\"" + label + "\",\"prob\":" + probability
					+ "},");
		}

		result.deleteCharAt(result.length() - 1);

		result.append(" ]");
		result.append("}");
		return result.toString();
	}
}
