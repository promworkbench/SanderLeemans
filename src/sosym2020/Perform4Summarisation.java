package sosym2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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
	}

	public static void writeToFile(final ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) throws IOException {
		int totalModels = parameters.getNumberOfRuns() * parameters.getNumberOfFolds();

		PrintWriter w = new PrintWriter(parameters.getOutputfile());

		w.write("%DO NOT EDIT THIS FILE: it is automatically generated and your changes will be overwritten.\n");
		w.write("\\begin{tabular}{l");
		for (int s = 0; s < numberOfLogsPerRow; s++) {
			w.write("|rr|rr|r|r");
		}
		w.write("}\n\\hline");

		File[] allLogFiles = parameters.getLogDirectory().listFiles();
		Arrays.sort(allLogFiles, new Comparator<File>() {
			public int compare(File o1, File o2) {
				return parameters.getSortName().get(o1.getName()).toLowerCase()
						.compareTo(parameters.getSortName().get(o2.getName()).toLowerCase());
			}

		});

//		for (int i = 0; i < numberOfLogsPerRow; i++) {
//			w.write("&\\multicolumn{2}{|c}{(a)}&\\multicolumn{2}{|c}{(b)}&\\multicolumn{1}{|c}{(c)}&\\multicolumn{1}{|c}{(d)}");
//		}
//		w.write("\\\\\n");
		for (int i = 0; i < numberOfLogsPerRow; i++) {
			w.write("&\\multicolumn{2}{|c}{not reduced}&\\multicolumn{2}{|c}{PT}&\\multicolumn{1}{|c}{PN}&\\multicolumn{1}{|c}{PT\\&PN}");
		}
		w.write("\\\\\n");
		for (int i = 0; i < numberOfLogsPerRow; i++) {
			w.write("&tree&pn&tree&pn&pn&pn");
		}
		w.write("\\\\\\hline\n");

		for (int s = 0; s < allLogFiles.length; s += numberOfLogsPerRow) {
			File[] logFiles = Arrays.copyOfRange(allLogFiles, s, Math.min(s + numberOfLogsPerRow, allLogFiles.length));

			//header
			for (File logFile : logFiles) {
				w.write("&\\multicolumn{" + 6 + "}{|c}{" + parameters.getLog2abbreviation().get(logFile.getName())
						+ "}");
			}
			w.write("\\\\\\hline\n");

			//body
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				w.write(algorithm.getAbbreviation());
				for (File logFile : logFiles) {
					Pair<Integer, Integer> p = completeModels(parameters, logFile, algorithm);
					if (p.getA() < totalModels) {
						//not done yet with mining
						if (p.getB() > 0) {
							//errors
							w.print("&-&-&-&-&-&-");
						} else {
							//not done yet
							w.print("&-&-&-&-&-&-");
						}
					} else {
						//done with mining
						writeMeasureToFile(w, results, logFile, algorithm, "r__-t nodes");
						writeMeasureToFile(w, results, logFile, algorithm, "r__-pn nodes");
						writeMeasureToFile(w, results, logFile, algorithm, "rt_-t nodes");
						writeMeasureToFile(w, results, logFile, algorithm, "rt_-pn nodes");
						writeMeasureToFile(w, results, logFile, algorithm, "r_p-pn nodes");
						writeMeasureToFile(w, results, logFile, algorithm, "rtp-pn nodes");
//						writeMeasurePartToFile(w, results, logFile, algorithm, "r__-pn nodes", "rtp-pn nodes");
					}
				}
				w.write("\\\\\n");
			}

			w.write("\\hline\n");
		}
		w.write("\\end{tabular}");
		w.close();

		FileUtils.copyFile(parameters.getOutputfile(),
				new File("Z:\\17 - Alifah - minimum self-distance\\svn Alifah\\results.tex"));
	}

	private static void writeMeasureToFile(PrintWriter w, Map<Triple<File, Algorithm, String>, TDoubleList> results,
			File logFile, Algorithm algorithm, String... measures) {
		int count = 0;
		for (String measure : measures) {
			if (results.containsKey(Triple.of(logFile, algorithm, measure))) {
				TDoubleList list = results.get(Triple.of(logFile, algorithm, measure));
				if (isAllMeasuresPresent(list)) {
					//measure done
					double mean = list.sum() / list.size();
					double stdev = getStandardDeviation(list);
					w.print("&");
					if (count > 0) {
						w.print("\\underline{");
					}
					w.print(String.format("%.0f", mean));
					//w.print("$\\pm$" + String.format("%.2f", stdev));
					if (count > 0) {
						w.print("}");
					}
					return;
				} else if (hasErrors(list)) {
					w.print("&!");
					return;
				}
				count++;
			}
		}
		w.print("&!");
	}

	private static void writeMeasurePartToFile(PrintWriter w, Map<Triple<File, Algorithm, String>, TDoubleList> results,
			File logFile, Algorithm algorithm, String measure1, String measure2) {
		int count = 0;
		if (results.containsKey(Triple.of(logFile, algorithm, measure1))
				&& results.containsKey(Triple.of(logFile, algorithm, measure2))) {
			TDoubleList list1 = results.get(Triple.of(logFile, algorithm, measure1));
			TDoubleList list2 = results.get(Triple.of(logFile, algorithm, measure2));
			if (isAllMeasuresPresent(list1) && isAllMeasuresPresent(list2)) {
				//measure done
				double mean1 = list1.sum() / list1.size();
				double mean2 = list2.sum() / list2.size();
				w.print("&");
				if (count > 0) {
					w.print("\\underline{");
				}
				w.print(String.format("%.2f", (mean2 / mean1)));
				if (count > 0) {
					w.print("}");
				}
				return;
			} else if (hasErrors(list1)) {
				w.print("&!");
				return;
			}
			count++;
		}
		w.print("&!");
	}

	public static void writeMeasureToFile(PrintWriter w, TDoubleList list) {
		if (isAllMeasuresPresent(list)) {
			//measure done
			double mean = list.sum() / list.size();
			double stdev = getStandardDeviation(list);
			w.print("&" + String.format("%.2f", mean));
			w.print("$\\pm$" + String.format("%.2f", stdev));
		} else {
			//measure not done
			w.print("&~");
		}
	}

	public static void writeLogMeasureToFile(PrintWriter w, TDoubleList list) {
		if (isAllMeasuresPresent(list)) {
			//measure done
			double mean = Math.log10((list.sum() / list.size()) / 1000);
			w.print("&" + String.format("%1$7s", String.format("%.0f", mean)));
		} else {
			//measure not done
			w.print("&");
		}
	}

	public static void writeToScreen(ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) throws IOException {

		int totalModels = parameters.getNumberOfRuns() * parameters.getNumberOfFolds();

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

	private static boolean hasErrors(TDoubleList list) {
		TDoubleIterator it = list.iterator();
		while (it.hasNext()) {
			double d = it.next();
			if (d == Double.MAX_VALUE) {
				return true;
			}
		}
		return false;
	}

	private static Pair<Integer, Integer> completeModels(ExperimentParameters parameters, File logFile,
			Algorithm algorithm) throws IOException {
		int completeModels = 0;
		int errors = 0;
		for (int runNr : parameters.getRuns()) {
			for (int foldNr : parameters.getFolds()) {
				Call call = new Call(parameters, algorithm, logFile, runNr, foldNr);
				if (call.isModelDone()) {
					if (Perform3Measures.isError(call.getModelFile())) {
						errors++;
					} else {
						completeModels++;
					}
				}
			}
		}
		return Pair.of(completeModels, errors);
	}

	public static Iterable<String> getAllMeasures(ExperimentParameters parameters) {
		List<String> result = new ArrayList<>();
		for (Measure measure : parameters.getMeasures()) {
			int numberOfCombinedMeasures = 1 + measure.getNumberOfMeasures() - measure.getCombinedMeasures();
			for (int i = 0; i < numberOfCombinedMeasures; i++) {
				result.add(measure.getMeasureNames()[i]);
			}
		}
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

				//time
				{
					TDoubleList l = new TDoubleArrayList();
					for (int runNr : parameters.getRuns()) {
						for (int foldNr : parameters.getFolds()) {
							Call call = new Call(parameters, algorithm, logFile, runNr, foldNr);

							if (call.getTimeFile().exists()) {
								BufferedReader r = new BufferedReader(new FileReader(call.getTimeFile()));
								l.add(Double.valueOf(r.readLine().split(" ")[0]));
								r.close();
							} else {
								//not done yet
								l.add(Double.MIN_VALUE);
							}
						}
					}
					result.put(Triple.of(logFile, algorithm, "time"), l);
				}

				//other measures
				for (Measure measure : parameters.getMeasures()) {
					int numberOfCombinedMeasures = 1 + measure.getNumberOfMeasures() - measure.getCombinedMeasures();

					TDoubleList[] l = new TDoubleArrayList[numberOfCombinedMeasures];
					for (int i = 0; i < numberOfCombinedMeasures; i++) {
						l[i] = new TDoubleArrayList();
						result.put(Triple.of(logFile, algorithm, measure.getMeasureNames()[i]), l[i]);
					}

					for (int runNr : parameters.getRuns()) {
						for (int foldNr : parameters.getFolds()) {
							Call call = new Call(parameters, algorithm, logFile, runNr, foldNr);

							if (call.isModelDone()
									&& (Perform3Measures.isError(call.getModelFile()) || (call.isMeasureDone(measure)
											&& Perform3Measures.isError(call.getMeasureFile(measure))))) {

								//error in model and/or measure
								for (int i = 0; i < numberOfCombinedMeasures; i++) {
									l[i].add(Double.MAX_VALUE);
								}

							} else if (call.isModelDone() && call.isMeasureDone(measure)
									&& !Perform3Measures.isError(call.getMeasureFile(measure))) {

								//read in the file
								BufferedReader r = new BufferedReader(new FileReader(call.getMeasureFile(measure)));
								for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
									if (i < numberOfCombinedMeasures) {
										l[i].add(Double.valueOf(r.readLine().split(" ")[0]));
									} else {
										l[numberOfCombinedMeasures - 1].add(Double.valueOf(r.readLine().split(" ")[0]));
									}
								}
								r.close();
							} else {
								//measure not done yet
								for (int i = 0; i < numberOfCombinedMeasures; i++) {
									l[i].add(Double.MIN_VALUE);
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
}