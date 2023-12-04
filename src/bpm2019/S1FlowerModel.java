package bpm2019;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class S1FlowerModel {
	@Plugin(name = "BPM2019 S1 Create flower stochastic language", returnLabels = {
			"Stochastic Petri net S1" }, returnTypes = {
					StochasticNet.class }, parameterLabels = {}, userAccessible = true, help = "Convert log to stochastic language.", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = {})
	public StochasticNet convert(final PluginContext context) {
		StochasticNetImpl result = new StochasticNetImpl("flower model");

		Place source = result.addPlace("source");
		TimedTransition t1 = result.addImmediateTransition("silent start", 1);
		t1.setInvisible(true);
		result.addArc(source, t1);
		Place busy = result.addPlace("busy");
		result.addArc(t1, busy);

		create(result, busy, "a");
		create(result, busy, "b");
		create(result, busy, "c");
		create(result, busy, "d");
		create(result, busy, "e");

		TimedTransition t2 = result.addImmediateTransition("silent stop", 1 / 6.0);
		t2.setInvisible(true);
		result.addArc(busy, t2);

		Place sink = result.addPlace("sink");
		result.addArc(t2, sink);

		return result;
	}

	private void create(StochasticNetImpl result, Place busy, String label) {
		TimedTransition a = result.addImmediateTransition(label, 1 / 6.0);
		result.addArc(busy, a);
		result.addArc(a, busy);
	}
}
