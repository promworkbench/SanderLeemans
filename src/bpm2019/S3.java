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

public class S3 {
	@Plugin(name = "BPM2019 S3 Create GSPN", returnLabels = { "Stochastic Petri net S3" }, returnTypes = {
			StochasticNet.class }, parameterLabels = {}, userAccessible = true, help = "Convert log to stochastic language.", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = {})
	public StochasticNet convert(final PluginContext context) {
		StochasticNetImpl result = new StochasticNetImpl("S2");

		Place source = result.addPlace("source");
		TimedTransition a = result.addImmediateTransition("a", 1);
		result.addArc(source, a);

		Place p1 = result.addPlace("p1");
		result.addArc(a, p1);

		TimedTransition t1 = result.addImmediateTransition("silent start", 0.9);
		t1.setInvisible(true);
		result.addArc(p1, t1);

		Place p2 = result.addPlace("p2");
		result.addArc(t1, p2);
		TimedTransition b = result.addImmediateTransition("b", 0.5);
		result.addArc(p2, b);
		Place p4 = result.addPlace("p4");
		result.addArc(b, p4);

		Place p3 = result.addPlace("p3");
		result.addArc(t1, p3);
		TimedTransition c = result.addImmediateTransition("c", 0.5);
		result.addArc(p3, c);
		Place p5 = result.addPlace("p5");
		result.addArc(c, p5);

		TimedTransition t2 = result.addImmediateTransition("silent stop", 1);
		t2.setInvisible(true);
		result.addArc(p4, t2);
		result.addArc(p5, t2);

		TimedTransition d = result.addImmediateTransition("d", 0.1);
		result.addArc(p1, d);

		Place p6 = result.addPlace("p6");
		result.addArc(t2, p6);
		result.addArc(d, p6);
		
		TimedTransition e = result.addImmediateTransition("e", 1);
		result.addArc(p6, e);

		Place sink = result.addPlace("sink");
		result.addArc(e, sink);

		return result;
	}
}
