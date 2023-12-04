package icsoc2019;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMapped;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMappedImpl;

public class S2SDFA {
	@Plugin(name = "Create S2 stochastic deterministic finite automaton", returnLabels = {
			"Stochastic deterministic finite automaton" }, returnTypes = {
					StochasticDeterministicFiniteAutomatonMapped.class }, parameterLabels = {}, userAccessible = true, help = "Convert log to stochastic deterministic finite automaton.", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = {})
	public StochasticDeterministicFiniteAutomatonMapped convert(final PluginContext context) {
		StochasticDeterministicFiniteAutomatonMappedImpl result = new StochasticDeterministicFiniteAutomatonMappedImpl();

		int state0 = result.getInitialState();
		short a = result.transform("a");
		short b = result.transform("b");
		short c = result.transform("c");
		short d = result.transform("d");
		short e = result.transform("e");

		int state1 = result.addEdge(state0, a, 1.0);
		int state2 = result.addEdge(state1, c, 0.48);
		int state5 = result.addEdge(state2, b, 1);
		int state4 = result.addEdge(state1, b, 0.32);
		result.addEdge(state4, c, state5, 1.0);
		int state3 = result.addEdge(state1, d, 0.2);
		int state6 = result.addEdge(state3, e, 0.8);
		result.addEdge(state6, d, state3, 1.0);

		return result;
	}
}
