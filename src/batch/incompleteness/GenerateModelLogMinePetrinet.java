package batch.incompleteness;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot2Image;
import org.processmining.plugins.graphviz.dot.Dot2Image.Type;
import org.processmining.plugins.inductiveVisualMiner.plugins.GraphvizPetriNet;
import org.processmining.plugins.inductiveVisualMiner.plugins.GraphvizProcessTree;
import org.processmining.processtree.ProcessTree;

import batch.miners.Flower;
import batch.miners.MinerClass;
import batch.miners.isomorphism.IsoFM;
import batch.stability.batchStability;
import batch.stability.batchStability.PetrinetWithInitialMarking;
import generation.GenerateLog;
import generation.GenerateLogParameters;
import generation.GenerateTree;

@Plugin(name = "Generate model log, generate Petri net", returnLabels = { "Petri net", "initial marking" }, returnTypes = { Petrinet.class, Marking.class }, parameterLabels = { }, userAccessible = true)
public class GenerateModelLogMinePetrinet {
	
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Generate model log, generate Petri net", requiredParameterLabels = {})
	public Object[] generate(final UIPluginContext context) throws Exception {
		ProcessTree tree = (new GenerateTree()).mineGuiPetrinet(context);
		XLog log = (new GenerateLog()).generateLog(tree, new GenerateLogParameters());
		
		//MinerClass c = new MinerClass(Alpha.class, new Alpha().getIdentification(), IsoAlpha.class);
		//MinerClass c = new MinerClass(ILP.class, new ILP().getIdentification(), IsoAlpha.class);
		//MinerClass c = new MinerClass(TSM.class, new TSM().getIdentification(), IsoTransitionSystem.class);
		MinerClass c = new MinerClass(Flower.class, new Flower().getIdentification(), IsoFM.class);
		PetrinetWithInitialMarking pnm = batchStability.discover(context, log, c);
		
		//write discovered net to image
		Dot ds = GraphvizPetriNet.convert(pnm.petrinet, pnm.initialMarking, null);
		Dot2Image.dot2image(ds, new File("d:\\discovered.png"), Type.png);
		Dot2Image.dot2image(ds, new File("d:\\discovered.pdf"), Type.pdf);
		
		//write process tree to image
		Dot dt = GraphvizProcessTree.convert(tree);
		Dot2Image.dot2image(dt, new File("d:\\generated.png"), Type.png);
		Dot2Image.dot2image(dt, new File("d:\\generated.pdf"), Type.pdf);
		
		return new Object[]{pnm.petrinet, pnm.initialMarking};
	}
}
