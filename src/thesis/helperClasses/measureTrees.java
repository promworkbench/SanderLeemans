package thesis.helperClasses;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import gnu.trove.map.hash.THashMap;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationConformance;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationSettings;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationSummarise.Algorithm;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationSummarise.Fold;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationSummarise.Log;

public class measureTrees {
	public static void main(String[] args) throws Exception {

		Map<String, Map<Log, Map<Integer, Integer>>> activities = new THashMap<>();

		DecimalFormat df2 = new DecimalFormat("#.00");

		for (final File modelFile : kFoldCrossValidationSettings.modelDirectory.listFiles()) {
			if (FilenameUtils.getExtension(modelFile.getAbsolutePath()).equals("tree")) {
				if (modelFile.getName().contains("fm")&&modelFile.getName().contains("hospit")) {
					Log log = Log.getLog(modelFile);
					Fold fold = Fold.getFold(log, modelFile);
					String algorithm = Algorithm.getAlgorithm(fold);

					if (!activities.containsKey(algorithm)) {
						activities.put(algorithm, new THashMap<Log, Map<Integer, Integer>>());
					}

					Map<Log, Map<Integer, Integer>> activitiesMap = activities.get(algorithm);

					if (!activitiesMap.containsKey(log)) {
						activitiesMap.put(log, new THashMap<Integer, Integer>());
					}

					Map<Integer, Integer> activitiesLogMap = activitiesMap.get(log);

					EfficientTree tree = kFoldCrossValidationConformance.loadEfficientTree(modelFile);

					activitiesLogMap.put(fold.fold, tree.getInt2activity().length);
				}
			}
		}

		//print
		for (String algorithm : new TreeSet<>(activities.keySet())) {
			for (Log log : Log.logs) {
				int count = 0;
				double sumActivities = 0;
				if (activities.get(algorithm).containsKey(log)) {
					for (double value : activities.get(algorithm).get(log).values()) {
						count++;
						sumActivities += value;
					}

					if (count == kFoldCrossValidationSettings.folds) {
						System.out.println(algorithm + " " + log + " " + (sumActivities / count));
					}
				}
			}
		}
	}
}
