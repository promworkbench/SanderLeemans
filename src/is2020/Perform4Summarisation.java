package is2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.THashMap;

public class Perform4Summarisation {

	public static final int numberOfLogsPerRow = 3;

	public static void main(String... args) throws NumberFormatException, IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		Map<Triple<File, Algorithm, String>, TDoubleList> results = read(parameters);
		writeToScreen(parameters, results);
		writeToFile(parameters, results);
		writeMaxTime(parameters, results);
	}

	public static void writeMaxTime(final ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) {
		for (Algorithm algorithm : parameters.getAlgorithms()) {
			double max = Double.MIN_VALUE;
			for (File logFile : parameters.getLogDirectory().listFiles()) {
				max = Math.max(max, results.get(Triple.of(logFile, algorithm, "time")).max());
			}
			System.out.println(algorithm.getAbbreviation() + " max time " + max);
		}
	}

	public static void writeToFile(final ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) throws IOException {
		PrintWriter w = new PrintWriter(parameters.getOutputfile());

		File[] logFiles = parameters.getLogDirectory().listFiles();

		w.write("mass ");
		for (File logFile : logFiles) {
			w.write("emsc-" + parameters.getLog2abbreviation().get(logFile.getName()) + " ");
			w.write("time-" + parameters.getLog2abbreviation().get(logFile.getName()) + " ");
			w.write("size-" + parameters.getLog2abbreviation().get(logFile.getName()) + " ");
		}
		w.write("\n");

		Algorithm algorithm = parameters.getAlgorithms().iterator().next();

		int unfoldingIndex = -1;
		for (int unfolding : parameters.getUnfoldings()) {
			unfoldingIndex++;
			w.write(unfolding + " ");

			for (File logFile : logFiles) {

				double emsc = results.get(Triple.of(logFile, algorithm, "emsc")).get(unfoldingIndex);
				if (emsc != -Double.MAX_VALUE) {
					w.write(emsc + " ");
				} else {
					w.write("NaN ");
				}

				double time = results.get(Triple.of(logFile, algorithm, "time")).get(unfoldingIndex);
				if (time != -Double.MAX_VALUE) {
					w.write(time + " ");
				} else {
					w.write("NaN ");
				}

				double size = results.get(Triple.of(logFile, algorithm, "size")).get(unfoldingIndex);
				if (size != -Double.MAX_VALUE) {
					w.write(size + " ");
				} else {
					w.write("NaN ");
				}

			}

			w.write("\n");
		}
		w.close();
	}

	public static void writeToScreen(ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) throws IOException {

		int totalModels = parameters.getNumberOfUnfoldings();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			System.out.println(
					"Log " + logFile.getName() + " (" + parameters.getLog2abbreviation().get(logFile.getName()) + ")");
			for (Algorithm algorithm : parameters.getAlgorithms()) {

				System.out.print(String.format("%1$10s", algorithm.getName()));
				System.out.print(" ");

				Pair<Integer, Integer> p = completeModels(parameters, logFile, algorithm);
				if (p.getA() < totalModels) {
					//not done yet with mining
					System.out.print(" discovered " + p.getA());
					if (p.getB() > 0) {
						System.out.print(" errors " + p.getB());
					}
				} else {
					//done with mining
					for (String measure : getAllMeasures(parameters)) {
						TDoubleList list = results.get(Triple.of(logFile, algorithm, measure));

						if (isAllMeasuresPresent(list)) {

							double mean = list.sum() / list.size();
							double stdev = getStandardDeviation(list);

							System.out.print(" " + measure);
							System.out.print(" µ" + String.format("%1$7s", String.format("%.2f", mean)));
							System.out.print(" σ" + String.format("%1$7s", String.format("%.2f", stdev)));
						}

					} //measure

				}

				System.out.println();

			} //algorithm
		} //log
	}

	private static double getStandardDeviation(TDoubleList list) {
		double mean = list.sum() / list.size();
		double sum = 0;
		for (TDoubleIterator it = list.iterator(); it.hasNext();) {
			sum += Math.pow(it.next() - mean, 2);
		}
		return Math.sqrt(sum / (list.size() - 1));
	}

	private static boolean isAllMeasuresPresent(TDoubleList list) {
		TDoubleIterator it = list.iterator();
		while (it.hasNext()) {
			double d = it.next();
			if (d == Double.MIN_VALUE || d == Double.MAX_VALUE) {
				return false;
			}
		}
		return true;
	}

	private static Pair<Integer, Integer> completeModels(ExperimentParameters parameters, File logFile,
			Algorithm algorithm) throws IOException {
		int completeModels = 0;
		int errors = 0;
		for (int unfolding : parameters.getUnfoldings()) {
			Call call = new Call(logFile, algorithm, parameters, unfolding);
			if (call.getDiscoveredModelFile().exists()) {
				if (Perform2Measures.isError(call.getDiscoveredModelFile())) {
					errors++;
				} else {
					completeModels++;
				}
			}
		}
		return Pair.of(completeModels, errors);
	}

	public static Iterable<String> getAllMeasures(ExperimentParameters parameters) {
		List<String> result = new ArrayList<>();
		result.add("emsc");
		result.add("time");
		return result;
	}

	//	public static void write() {
	//
	//		int totalCalls = parameters.getNumberOfRuns() * parameters.getNumberOfFolds();
	//		PrintWriter output = new PrintWriter(parameters.getOutputfile());
	//
	//		//header
	//		int totalNumberOfMeasures = 0;
	//		{
	//			for (Measure measure : parameters.getMeasures()) {
	//				int numberOfCombinedMeasures = 1 + measure.getNumberOfMeasures() - measure.getCombinedMeasures();
	//				for (int m = 0; m < numberOfCombinedMeasures; m++) {
	//					output.write("&" + measure.getTitle() + " " + measure.getMeasureNames()[m]);
	//				}
	//				totalNumberOfMeasures += numberOfCombinedMeasures;
	//			}
	//			output.write("\\\\\n");
	//		}
	//
	//		output.write("\\multicolumn{" + (totalNumberOfMeasures + 1) + "}{l}{" + logFile.getName() + "}\\\\\n");
	//
	//		System.out.print("  " + String.format("%1$10s", algorithm.getName()) + ": ");
	//
	//		if (measuresDone == totalCalls) {
	//			System.out.print(" (" + measure.getTitle() + ")");
	//			for (int i = 0; i < numberOfCombinedMeasures - 1; i++) {
	//				System.out.print(" " + measure.getMeasureNames()[i] + " "
	//						+ String.format("%1$6s", String.format("%.2f", measureSum[i] / totalCalls)));
	//
	//			}
	//			System.out.print(" " + measure.getMeasureNames()[numberOfCombinedMeasures - 1] + " "
	//					+ String.format("%1$6s", String.format("%.2f",
	//							measureSum[numberOfCombinedMeasures - 1] / (totalCalls * measure.getCombinedMeasures()))));
	//		} else {
	//			for (int i = 0; i < numberOfCombinedMeasures; i++) {
	//				output.write("&");
	//			}
	//		}
	//
	//		if (modelsDone != parameters.getMeasures().size() * totalCalls) {
	//			System.out.print(" discovered " + modelsDone / parameters.getMeasures().size());
	//		}
	//		if (errors > 0) {
	//			System.out.print(" errors " + errors / parameters.getMeasures().size());
	//		}
	//		System.out.println("");
	//		output.write("\\\\\n");
	//	}

	/**
	 * The list contains Double.minvalue for errors, and Double.maxvalue for
	 * incomplete measures.
	 * 
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	public static Map<Triple<File, Algorithm, String>, TDoubleList> read(ExperimentParameters parameters)
			throws IOException {
		Map<Triple<File, Algorithm, String>, TDoubleList> result = new THashMap<>();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				TDoubleList times = new TDoubleArrayList();
				TDoubleList emsc = new TDoubleArrayList();
				TDoubleList size = new TDoubleArrayList();
				for (int unfolding : parameters.getUnfoldings()) {
					Call call = new Call(logFile, algorithm, parameters, unfolding);

					if (call.getLanguageSizeFile().exists()) {
						//size
						{
							BufferedReader r = new BufferedReader(new FileReader(call.getLanguageSizeFile()));
							r.readLine();
							size.add(Integer.valueOf(r.readLine()));
							r.close();
						}
					} else {
						size.add(-Double.MAX_VALUE);
					}

					if (call.getLogMeasuresFile().exists() && call.getLogMeasuresTimeFile().exists()) {
						if (!Perform2Measures.isError(call.getLogMeasuresFile())) {
							//measures done

							//time
							{
								BufferedReader r = new BufferedReader(new FileReader(call.getLogMeasuresTimeFile()));
								times.add(Double.valueOf(r.readLine()));
								r.close();
							}

							//emsc
							{
								BufferedReader r = new BufferedReader(new FileReader(call.getLogMeasuresFile()));
								emsc.add(Double.valueOf(r.readLine()));
								r.close();
							}
						} else {
							//measure done, but error
							times.add(-Double.MAX_VALUE);
							emsc.add(-Double.MAX_VALUE);
						}

					} else {
						//measure not done yet
						times.add(-Double.MAX_VALUE);
						emsc.add(-Double.MAX_VALUE);
					}
				}
				result.put(Triple.of(logFile, algorithm, "time"), times);
				result.put(Triple.of(logFile, algorithm, "emsc"), emsc);
				result.put(Triple.of(logFile, algorithm, "size"), size);
			}
		}

		return result;
	}
}