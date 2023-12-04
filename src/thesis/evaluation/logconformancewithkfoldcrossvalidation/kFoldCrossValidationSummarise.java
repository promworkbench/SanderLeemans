package thesis.evaluation.logconformancewithkfoldcrossvalidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;

import cern.colt.Arrays;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;
import thesis.helperClasses.ParetoOptimality;

public class kFoldCrossValidationSummarise {

	public static class Log {
		String name;

		public static Set<Log> logs = new THashSet<>();

		public static Log getLog(File file) {
			for (Log log : logs) {
				if (file.getName().startsWith(log.name)) {
					return log;
				}
			}
			for (File logFile : kFoldCrossValidationSettings.fullLogDirectory.listFiles()) {
				if (file.getName().startsWith(logFile.getName())) {
					Log result = new Log();
					result.name = logFile.getName();
					logs.add(result);
					return result;
				}
			}
			return null;
		}

		public String toString() {
			return name;
		}
	}

	public static class Algorithm {

		public static String getAlgorithm(Fold fold) {
			return fold.remainingFileName.substring(0, fold.remainingFileName.indexOf('.'));

		}
	}

	public static class Fold {
		public int fold;
		public String remainingFileName;

		public static Fold getFold(Log log, File file) {
			String r = file.getName().substring(log.name.length() + 10);
			Fold f = new Fold();
			f.fold = Integer.valueOf(r.substring(0, r.indexOf('.')));
			f.remainingFileName = r.substring(r.indexOf('-') + 1);
			return f;
		}
	}

	public static void main(String[] args) throws Exception {

		Map<String, Map<Log, Map<Integer, Double>>> fitness = new THashMap<>();
		Map<String, Map<Log, Map<Integer, Double>>> precision = new THashMap<>();
		Map<String, Map<Log, Map<Integer, Integer>>> simplicity = new THashMap<>();
		Map<String, Map<Log, Map<Integer, Boolean>>> soundness = new THashMap<>();

		DecimalFormat df1 = new DecimalFormat("0.0");
		DecimalFormat df2 = new DecimalFormat("0.00");

		for (final File measureFile : kFoldCrossValidationSettings.measuresDirectory.listFiles()) {
			if (FilenameUtils.getExtension(measureFile.getAbsolutePath()).equals("txt")) {
				Log log = Log.getLog(measureFile);
				Fold fold = Fold.getFold(log, measureFile);
				String algorithm = Algorithm.getAlgorithm(fold);

				if (!fitness.containsKey(algorithm)) {
					fitness.put(algorithm, new THashMap<Log, Map<Integer, Double>>());
					precision.put(algorithm, new THashMap<Log, Map<Integer, Double>>());
					simplicity.put(algorithm, new THashMap<Log, Map<Integer, Integer>>());
					soundness.put(algorithm, new THashMap<Log, Map<Integer, Boolean>>());
				}

				Map<Log, Map<Integer, Double>> fitnessMap = fitness.get(algorithm);
				Map<Log, Map<Integer, Double>> precisionMap = precision.get(algorithm);
				Map<Log, Map<Integer, Integer>> simplicityMap = simplicity.get(algorithm);
				Map<Log, Map<Integer, Boolean>> soundnessMap = soundness.get(algorithm);

				if (!fitnessMap.containsKey(log)) {
					fitnessMap.put(log, new THashMap<Integer, Double>());
					precisionMap.put(log, new THashMap<Integer, Double>());
					simplicityMap.put(log, new THashMap<Integer, Integer>());
					soundnessMap.put(log, new THashMap<Integer, Boolean>());
				}

				Map<Integer, Double> fitnessLogMap = fitnessMap.get(log);
				Map<Integer, Double> precisionLogMap = precisionMap.get(log);
				Map<Integer, Integer> simplicityLogMap = simplicityMap.get(log);
				Map<Integer, Boolean> soundnessLogMap = soundnessMap.get(log);

				BufferedReader br = new BufferedReader(new FileReader(measureFile));
				String line = br.readLine();
				br.close();
				if (line.contains("$")) {
					if (line.contains(";")) {
						double fitnessValue = Double.valueOf(line.substring(0, line.indexOf(';')));
						double precisionValue = Double
								.valueOf(line.substring(line.indexOf(';') + 1, line.indexOf('$')));

						fitnessLogMap.put(fold.fold, fitnessValue);
						precisionLogMap.put(fold.fold, precisionValue);
						soundnessLogMap.put(fold.fold, true);
					} else {
						soundnessLogMap.put(fold.fold, false);
					}

					int simplicityValue = Integer.valueOf(line.substring(line.indexOf('$') + 1));
					simplicityLogMap.put(fold.fold, simplicityValue);
				} else {
					soundnessLogMap.put(fold.fold, false);
				}
			}
		}

		List<Log> logs = new ArrayList<>(Log.logs);
		logs.sort(new Comparator<Log>() {

			public int compare(Log o1, Log o2) {
				return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
			}
		});

		//compute pareto-optimality
		Map<Log, ParetoOptimality<String>> log2pareto = new THashMap<>();
		TObjectIntMap<String> algorithm2pareto = new TObjectIntHashMap<String>();
		{
			for (Log log : logs) {
				ParetoOptimality<String> optimality = new ParetoOptimality<>();
				for (String algorithm : new TreeSet<>(fitness.keySet())) {
					int countSound = 0;
					double sumFitness = 0;
					double sumPrecision = 0;

					int countSimplicity = 0;
					double sumSimplicity = 0;

					double countModels = 0;
					if (soundness.get(algorithm).containsKey(log)) {

						for (boolean sound : soundness.get(algorithm).get(log).values()) {
							countModels++;
						}
						for (double value : fitness.get(algorithm).get(log).values()) {
							countSound++;
							sumFitness += value;
						}
						for (double value : precision.get(algorithm).get(log).values()) {
							sumPrecision += value;
						}
						for (int value : simplicity.get(algorithm).get(log).values()) {
							countSimplicity++;
							sumSimplicity += value;
						}

						if (countModels == kFoldCrossValidationSettings.folds) {
							if (countSound / countModels > 0.9) {
								optimality.add(algorithm, sumFitness / countSound, sumPrecision / countSound,
										-sumSimplicity / countSimplicity);
							}
						}
					}
				}
				log2pareto.put(log, optimality);

				System.out.println();
				System.out.println(log);

				int i = 0;
				for (String algorithm : optimality.getParetoOptimalIdentifiers()) {
					System.out.println(algorithm + " " + Arrays.toString(optimality.getParetoOptimalMeasures().get(i)));
					algorithm2pareto.adjustOrPutValue(algorithm, 1, 1);
					i++;
				}
			}
		}

		//write frequency table
		System.out.println();
		System.out.println("frequency table: ");
		for (Object algorithm : algorithm2pareto.keys()) {
			System.out.println(algorithm + " " + algorithm2pareto.get(algorithm));
		}

		//write to csv files
		int fileNr = 0;

		//data
		for (int logBase = 0; logBase < logs.size() / kFoldCrossValidationSettings.maxLogsWidthInTable; logBase++) {

			PrintWriter resultsFile = new PrintWriter(
					new File(kFoldCrossValidationSettings.baseDirectory, "results" + fileNr + ".tex"), "UTF-8");

			//tabular
			resultsFile.write("\\begin{tabular}{l");
			for (int i = 0; i < kFoldCrossValidationSettings.maxLogsWidthInTable; i++) {
				resultsFile.write("|rrR{1.2cm}r");
			}
			resultsFile.write("}");
			resultsFile.println();

			List<Log> localLogs = logs.subList(logBase * kFoldCrossValidationSettings.maxLogsWidthInTable,
					(logBase + 1) * kFoldCrossValidationSettings.maxLogsWidthInTable);
			resultsFile.write("\\hline");

			//header #1
			for (Log log : localLogs) {
				resultsFile.write("& \\multicolumn{4}{c"
						+ (log == localLogs.get(kFoldCrossValidationSettings.maxLogsWidthInTable - 1) ? "" : "|") + "}{"
						+ kFoldCrossValidationSettings.logAbbreviations.getOrDefault(log.name, log.name) + "}");
			}
			resultsFile.println("\\\\");

			//header #2
			for (Log log : localLogs) {
				resultsFile.write("&f&p&s&b");
			}
			resultsFile.println("\\\\\\hline");

			for (String algorithm : new TreeSet<>(fitness.keySet())) {
				resultsFile.write(kFoldCrossValidationSettings.algorithmAbbreviations.getOrDefault(algorithm,
						algorithm.toUpperCase()));
				for (Log log : localLogs) {
					int countSound = 0;
					double sumFitness = 0;
					double sumPrecision = 0;

					int countSimplicity = 0;
					double sumSimplicity = 0;

					double countModels = 0;
					if (soundness.get(algorithm).containsKey(log)) {

						for (boolean sound : soundness.get(algorithm).get(log).values()) {
							countModels++;
						}

						for (double value : fitness.get(algorithm).get(log).values()) {
							countSound++;
							sumFitness += value;
						}
						for (double value : precision.get(algorithm).get(log).values()) {
							sumPrecision += value;
						}
						for (int value : simplicity.get(algorithm).get(log).values()) {
							countSimplicity++;
							sumSimplicity += value;
						}

						if (countModels == kFoldCrossValidationSettings.folds) {
							if (countSound > 0.9) {
								resultsFile.write("&");
								if (countSound < kFoldCrossValidationSettings.folds) {
									resultsFile.write("\\cancel{");
								} else if (log2pareto.get(log).getParetoOptimalIdentifiers().contains(algorithm)) {
									resultsFile.write("\\textbf{");
								}
								resultsFile.write(df2.format(sumFitness / countSound));
								if (countSound < kFoldCrossValidationSettings.folds) {
									resultsFile.write("\\!}");
								} else if (log2pareto.get(log).getParetoOptimalIdentifiers().contains(algorithm)) {
									resultsFile.write("}");
								}
								resultsFile.write("&");
								if (countSound < kFoldCrossValidationSettings.folds) {
									resultsFile.write("\\cancel{");
								} else if (log2pareto.get(log).getParetoOptimalIdentifiers().contains(algorithm)) {
									resultsFile.write("\\textbf{");
								}
								resultsFile.write(df2.format(sumPrecision / countSound));
								if (countSound < kFoldCrossValidationSettings.folds) {
									resultsFile.write("\\!}");
								} else if (log2pareto.get(log).getParetoOptimalIdentifiers().contains(algorithm)) {
									resultsFile.write("}");
								}
							} else {
								resultsFile.write("&&");
							}
							resultsFile.write("&");
							if (countSimplicity > 0) {
								if (log2pareto.get(log).getParetoOptimalIdentifiers().contains(algorithm)) {
									resultsFile.write("\\textbf{");
								}
								resultsFile.write(df1.format(sumSimplicity / countSimplicity));
								if (log2pareto.get(log).getParetoOptimalIdentifiers().contains(algorithm)) {
									resultsFile.write("}");
								}
							}
							resultsFile.write("&");
							resultsFile.write(df1.format(countSound / countModels));
						} else {
							resultsFile.write("&&&&");
						}
					} else {
						resultsFile.write("&&&&");
					}
				}
				resultsFile.println("\\\\");
			}
			resultsFile.write("\\hline\\end{tabular}");
			resultsFile.close();
			fileNr++;
		}

		//System.out.println(fitness);
		//System.out.println(precision);
		//System.out.println(simplicity);
	}
}
