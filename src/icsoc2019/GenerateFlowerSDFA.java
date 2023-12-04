package icsoc2019;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMapped;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMappedImpl;

public class GenerateFlowerSDFA {
	@Plugin(name = "Caise 2019 Create flower stochastic deterministic finite automaton", returnLabels = {
			"Stochastic deterministic finite automaton" }, returnTypes = {
					StochasticDeterministicFiniteAutomatonMapped.class }, parameterLabels = {}, userAccessible = true, help = "Convert log to stochastic deterministic finite automaton.", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = {})
	public StochasticDeterministicFiniteAutomatonMapped convert(final PluginContext context) {
		StochasticDeterministicFiniteAutomatonMappedImpl result = new StochasticDeterministicFiniteAutomatonMappedImpl();

		int state = result.getInitialState();
		short a = result.transform("a");
		short b = result.transform("b");
		short c = result.transform("c");
		short d = result.transform("d");
		short e = result.transform("e");

		result.addEdge(state, a, state, 0.23);
		result.addEdge(state, b, state, 0.06);
		result.addEdge(state, c, state, 0.06);
		result.addEdge(state, d, state, 0.29);
		result.addEdge(state, e, state, 0.12);

		return result;
	}
}