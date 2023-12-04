package thesis.evaluation.rediscoverability;

import java.io.File;
import java.io.FileWriter;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeImportPlugin;
import org.processmining.projectedrecallandprecision.framework.CompareParameters;
import org.processmining.projectedrecallandprecision.plugins.CompareProcessTree2ProcessTreePlugin;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult;

import dk.brics.automaton2.BasicAutomata;

public class RediscoverabilityMeasure {
	public static void main(String[] args) throws Exception {
		for (final File discoveredModelFile : RediscoverabilitySettings.discoveredModelDirectory.listFiles()) {
			EfficientTree discoveredTree = EfficientTreeImportPlugin.importFromFile(discoveredModelFile);

			for (File generatedModelFile : RediscoverabilitySettings.generatedModelDirectory.listFiles()) {
				if (discoveredModelFile.getName().startsWith(generatedModelFile.getName())) {

					if (1 == 1) {

						EfficientTree generatedTree = EfficientTreeImportPlugin.importFromFile(generatedModelFile);

						File outFile = new File(RediscoverabilitySettings.measuresDirectory,
								discoveredModelFile.getName() + "-measure.txt");

						if (!outFile.exists()) {
							CompareParameters parameters = new CompareParameters(2);
							ProjectedRecallPrecisionResult result = CompareProcessTree2ProcessTreePlugin.measure(
									generatedTree, discoveredTree, parameters, BasicAutomata.notCanceller);

							System.out.println(result.getRecall() + ";" + result.getPrecision());

							FileWriter writer = new FileWriter(outFile);
							writer.write(result.getRecall() + ";" + result.getPrecision());
							writer.close();
						}
					}
				}
			}
		}
		System.out.println("done");
	}
}
