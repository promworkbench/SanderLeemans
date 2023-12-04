package icsoc2019;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMapped;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMappedImpl;

public class SeSDFA {
	@Plugin(name = "Create Se stochastic deterministic finite automaton", returnLabels = {
			"Stochastic deterministic finite automaton" }, returnTypes = {
					StochasticDeterministicFiniteAutomatonMapped.class }, parameterLabels = {}, userAccessible = true, help = "Convert log to stochastic deterministic finite automaton.", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = {})
	public StochasticDeterministicFiniteAutomatonMapped convert(final PluginContext context) {
		StochasticDeterministicFiniteAutomatonMappedImpl result = new StochasticDeterministicFiniteAutomatonMappedImpl();

		int state0 = result.getInitialState();
		short a = result.transform("a");

		int state1 = result.addEdge(state0, a, 0.8);
		result.addEdge(state1, a, state1, 0.5);

		return result;
	}
}
