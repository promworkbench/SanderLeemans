package thesis.helperClasses;

import java.io.File;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeImportPlugin;

import thesis.evaluation.rediscoverability.noise.NoiseSettings;

public class CountOrs {
	public static void main(String[] args) throws Exception {
		int files = 0;
		double ors = 0;
		for (final File file : NoiseSettings.discoveredModelDirectory.listFiles()) {
			if (file.getName().contains("imf.")) {
				EfficientTree tree = EfficientTreeImportPlugin.importFromFile(file);

				int length = tree.traverse(tree.getRoot());
				for (int node = tree.getRoot(); node < length; node++) {
					if (tree.isOr(node)) {
						ors++;
					}
				}
				files++;
			}
		}
		System.out.println("ors: " + ors/files);
	}
}
