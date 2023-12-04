package p2018reduction;

import java.io.File;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.processmining.plugins.InductiveMiner.plugins.IMTree;
import org.processmining.plugins.log.util.XESImport;

public class X010Discover {
	public static void main(String... args) throws IOException {

		for (File logFile : Parameters.getInputFiles()) {

			File modelFile = Parameters.getDiscoveredModelFile(logFile);

			if (!modelFile.exists()) {

				//load log
				XLog log = XESImport.readXLog(logFile.getAbsolutePath());

				//discover model
				MiningParameters parameters = new MiningParametersIMf();
				parameters.setReduceParameters(null);
				EfficientTree tree = IMTree.mineTree(log, parameters);

				//save model
				EfficientTreeExportPlugin.export(tree, modelFile);

			}
		}
		System.out.println("done");
	}
}
