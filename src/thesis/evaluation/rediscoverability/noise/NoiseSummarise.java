package thesis.evaluation.rediscoverability.noise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import thesis.evaluation.rediscoverability.RediscoverabilitySettings;
import thesis.evaluation.rediscoverability.RediscoverabilitySettings.Algorithm;

public class NoiseSummarise {
	public static void main(String[] args) throws Exception {

		Map<Algorithm, Map<String, Map<Integer, Double>>> fitness = new THashMap<>();
		Map<Algorithm, Map<String, Map<Integer, Double>>> precision = new THashMap<>();

		//read in
		{
			for (File measureFile : NoiseSettings.measuresDirectory.listFiles()) {
				Algorithm algorithm = getAlgorithm(measureFile);
				int logRound = getNoiseRound(measureFile);
				String generatedModel = getGeneratedModel(measureFile);

				if (!fitness.containsKey(algorithm)) {
					fitness.put(algorithm, new THashMap<String, Map<Integer, Double>>());
					precision.put(algorithm, new THashMap<String, Map<Integer, Double>>());
				}
				Map<String, Map<Integer, Double>> fitnessMap = fitness.get(algorithm);
				Map<String, Map<Integer, Double>> precisionMap = precision.get(algorithm);

				if (!fitnessMap.containsKey(generatedModel)) {
					fitnessMap.put(generatedModel, new THashMap<Integer, Double>());
					precisionMap.put(generatedModel, new THashMap<Integer, Double>());
				}
				Map<Integer, Double> fitnessMapMap = fitnessMap.get(generatedModel);
				Map<Integer, Double> precisionMapMap = precisionMap.get(generatedModel);

				BufferedReader br = new BufferedReader(new FileReader(measureFile));
				String line = br.readLine();
				br.close();
				double fitnessValue = Double.valueOf(line.substring(0, line.indexOf(';')));
				double precisionValue = Double.valueOf(line.substring(line.indexOf(';') + 1));

				fitnessMapMap.put(logRound, fitnessValue);
				precisionMapMap.put(logRound, precisionValue);
			}
		}

		//write to csv
		{
			PrintWriter resultsFile = new PrintWriter(new File(NoiseSettings.baseDirectory, "results.txt"),
					"UTF-8");

			//header
			{
				resultsFile.write("noiseEvents");
				for (Algorithm algorithm : RediscoverabilitySettings.algorithms) {
					resultsFile.write("\t");
					resultsFile.write(algorithm.name() + "recall");
					resultsFile.write("\t");
					resultsFile.write(algorithm.name() + "precision");
				}
			}

			//body
			{
				for (int round : getNoiseRounds()) {
					//write number of traces
					resultsFile.write("\n");
					resultsFile.write(((long) Math.pow(NoiseSettings.noiseIncreaseFactor, round)) + "");

					for (Algorithm algorithm : NoiseSettings.algorithms) {

						//compute the average fitness
						{
							double fitnessSum = 0;
							double fitnessCount = 0;
							for (String generatedModel : getGeneratedModels(fitness)) {
								if (fitness.containsKey(algorithm)
										&& fitness.get(algorithm).containsKey(generatedModel)
										&& fitness.get(algorithm).get(generatedModel).containsKey(round)) {
									fitnessCount++;
									fitnessSum += fitness.get(algorithm).get(generatedModel).get(round);
								}
							}
							if (fitnessCount == getGeneratedModels(fitness).size()) {
								resultsFile.write("\t" + (fitnessSum / fitnessCount));
							} else {
								System.out.println(" measure incomplete: noiseRound " + round + ", "
										+ (int) (getGeneratedModels(fitness).size() - fitnessCount) + " missing, "
										+ algorithm + ", recall value now " + (fitnessSum / fitnessCount));
								resultsFile.write("\tnan");
							}
						}

						//compute the average precision
						double precisionSum = 0;
						double precisionCount = 0;
						for (String generatedModel : getGeneratedModels(precision)) {
							if (precision.containsKey(algorithm)
									&& precision.get(algorithm).containsKey(generatedModel)
									&& precision.get(algorithm).get(generatedModel).containsKey(round)) {
								precisionCount++;
								precisionSum += precision.get(algorithm).get(generatedModel).get(round);
							}
						}
						if (precisionCount == getGeneratedModels(fitness).size()) {
							resultsFile.write("\t" + (precisionSum / precisionCount));
						} else {
							resultsFile.write("\tnan");
						}
					}
				}
				resultsFile.close();
			}

			//write to a file when the average recall and precision leave 1.
			{
				PrintWriter reaches1File = new PrintWriter(new File(NoiseSettings.baseDirectory,
						"leaves1.tex"), "UTF-8");
				for (Algorithm algorithm : NoiseSettings.algorithms) {
					reaches1File.write("\\def\\noiseLeavesPerfection" + algorithm.name() + "{");
					boolean written = false;

					for (int noiseRound = 0; noiseRound < NoiseSettings.noiseRounds; noiseRound++) {
						boolean fitnessPerfect = false;
						{
							double fitnessSum = 0;
							double fitnessCount = 0;
							for (String generatedModel : getGeneratedModels(fitness)) {
								if (fitness.containsKey(algorithm)
										&& fitness.get(algorithm).containsKey(generatedModel)
										&& fitness.get(algorithm).get(generatedModel).containsKey(noiseRound)) {
									fitnessCount++;
									fitnessSum += fitness.get(algorithm).get(generatedModel).get(noiseRound);
								}
							}
							if (fitnessCount == getGeneratedModels(fitness).size() && fitnessSum / fitnessCount == 1) {
								fitnessPerfect = true;
							}
						}
						boolean precisionPerfect = false;
						{
							double precisionSum = 0;
							double precisionCount = 0;
							for (String generatedModel : getGeneratedModels(precision)) {
								if (precision.containsKey(algorithm)
										&& precision.get(algorithm).containsKey(generatedModel)
										&& precision.get(algorithm).get(generatedModel).containsKey(noiseRound)) {
									precisionCount++;
									precisionSum += precision.get(algorithm).get(generatedModel).get(noiseRound);
								}
							}
							if (precisionCount == getGeneratedModels(precision).size()
									&& precisionSum / precisionCount == 1) {
								precisionPerfect = true;
							}
						}

						if (fitnessPerfect && precisionPerfect) {
							reaches1File
									.write((long) Math.pow(NoiseSettings.noiseIncreaseFactor, noiseRound) + "");
							written = true;
							break;
						}
					}

					if (!written) {
						reaches1File.write("10000");
					}

					reaches1File.write("}\n");
				}

				reaches1File.close();
			}
		}

		System.out.println(fitness);
		System.out.println(precision);
		System.out.println("done");
	}

	public static Algorithm getAlgorithm(File file) {
		for (Algorithm algorithm : RediscoverabilitySettings.algorithms) {
			if (file.getName().contains("-" + algorithm.name() + ".tree")) {
				return algorithm;
			}
		}
		return null;
	}

	public static Iterable<Integer> getNoiseRounds() {
		List<Integer> ints = new ArrayList<Integer>();
		for (int i = 0; i < NoiseSettings.noiseRounds; i++) {
			ints.add(i);
		}
		return ints;
	}

	public static Set<String> getGeneratedModels(Map<Algorithm, Map<String, Map<Integer, Double>>> fitness) {
		return fitness.values().iterator().next().keySet();
	}

	public static int getNoiseRound(File file) {
		int base = file.getName().indexOf("noiseRound") + 10;
		return Integer.valueOf(file.getName().substring(base, file.getName().indexOf('.', base)));
	}

	public static String getGeneratedModel(File file) {
		return file.getName().substring(0, file.getName().indexOf(".tree"));
	}
}
