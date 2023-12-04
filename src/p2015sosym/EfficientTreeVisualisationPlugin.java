package p2015sosym;

import javax.swing.JComponent;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2processTree;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationDataImplEmpty;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMEfficientTree;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisation;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationParameters;

public class EfficientTreeVisualisationPlugin {
	public JComponent visualize(PluginContext context, EfficientTree tree) {
		ProcessTreeVisualisation visualisation = new ProcessTreeVisualisation();
		return new DotPanel(
				visualisation
						.fancy(new IvMModel(new IvMEfficientTree(EfficientTree2processTree.convert(tree))),
								new AlignedLogVisualisationDataImplEmpty(), new ProcessTreeVisualisationParameters())
						.getA());
	}
}
