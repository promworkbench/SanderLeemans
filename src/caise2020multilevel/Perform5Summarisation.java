package caise2020multilevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.THashMap;

public class Perform5Summarisation {

	public static final int numberOfLogsPerRow = 3;

	public static void main(String... args) throws NumberFormatException, IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		Map<Triple<File, Algorithm, String>, TDoubleList> results = read(parameters);
		String humanString = humanString(parameters, results);

		{
			PrintWriter w = new PrintWriter(parameters.getOutputFile());
			w.print(humanString);
			w.close();
		}

		String latexString = latexString(parameters, results);

		{
			PrintWriter w = new PrintWriter(parameters.getOutputLatexFile());
			w.print(latexString);
			w.close();
		}
		
		String csvString = csvString(parameters, results);
		System.out.println(csvString);

		//writeToFile(parameters, results);
		//writeMaxTime(parameters, results);
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

	public static String latexString(ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) throws IOException {
		StringBuilder result = new StringBuilder();

		//header
		{
			result.append("\\begin{longtable}");

			result.append("{");
			result.append("ll");
			for (Measure measure : parameters.getMeasures()) {
				for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
					result.append("r");
				}
			}
			result.append("r"); //time
			result.append("}\n");

			result.append("\\toprule\n");

			result.append("log");
			result.append("&algorithm");
			for (Measure measure : parameters.getMeasures()) {
				result.append("&");
				result.append(
						"\\multicolumn{" + measure.getNumberOfMeasures() + "}{c}{" + measure.getLatexTitle() + "}");
			}
			result.append("&time");
			result.append("\\\\\n");

			//midrules
			{
				int col = 2;
				for (Measure measure : parameters.getMeasures()) {
					if (measure.getNumberOfMeasures() > 1) {
						result.append(
								"\\cmidrule(r{4pt}){" + (col + 1) + "-" + (col + measure.getNumberOfMeasures()) + "}");
					}
					col += measure.getNumberOfMeasures();
				}
				result.append("\n");
			}

			result.append("&");
			for (Measure measure : parameters.getMeasures()) {
				for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
					result.append("&");
					result.append(measure.getMeasureLatexNames()[i]);
				}
			}
			result.append("&(ms)");
			result.append("\\\\\n");
		}

		result.append("\\midrule\n");

		//body
		{
			int totalModels = parameters.getNumberOfRuns() * parameters.getNumberOfFolds();
			for (Iterator<File> itFile = Arrays.asList(parameters.getLogDirectory().listFiles()).iterator(); itFile
					.hasNext();) {
				File logFile = itFile.next();
				result.append(parameters.getLogAbbreviation(logFile));
				for (Algorithm algorithm : parameters.getAlgorithms()) {

					result.append("&");
					result.append(algorithm.getAbbreviation());

					Pair<Integer, Integer> p = completeModels(parameters, logFile, algorithm);
					if (p.getA() < totalModels) {
						//not done yet with mining
						result.append("&\\multicolumn{4}{l}{discovered " + p.getA());
						if (p.getB() > 0) {
							result.append(" errors " + p.getB());
						}
						result.append("}");
					} else {
						//done with mining
						for (String measure : getAllMeasures(parameters)) {
							TDoubleList list = results.get(Triple.of(logFile, algorithm, measure));

							result.append("&");
							if (isAllMeasuresPresent(list)) {
								double mean = list.sum() / list.size();
								double stdev = getStandardDeviation(list);

								result.append(String.format("%.2f", mean));
							}
						} //measure
					}
					result.append("\\\\\n");
				} //algorithm

				if (itFile.hasNext()) {
					result.append("\\addlinespace[0.5em]\n");
				}
			} //log
		}

		//footer
		{
			result.append("\\bottomrule\n");
			result.append("\\end{longtable}\n");
		}

		return result.toString();
	}

	public static String humanString(ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) throws IOException {

		int totalModels = parameters.getNumberOfRuns() * parameters.getNumberOfFolds();

		StringBuilder result = new StringBuilder();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			result.append("Log " + logFile.getName() + " (" + parameters.getLogAbbreviation(logFile) + ")\n");
			for (Algorithm algorithm : parameters.getAlgorithms()) {

				result.append(String.format("%1$10s", algorithm.getAbbreviation()));
				result.append(" ");

				Pair<Integer, Integer> p = completeModels(parameters, logFile, algorithm);
				if (p.getA() < totalModels) {
					//not done yet with mining
					result.append(" discovered " + p.getA());
					if (p.getB() > 0) {
						result.append(" errors " + p.getB());
					}
				} else {
					//done with mining
					for (String measure : getAllMeasures(parameters)) {
						TDoubleList list = results.get(Triple.of(logFile, algorithm, measure));

						if (isAllMeasuresPresent(list)) {
							double mean = list.sum() / list.size();
							double stdev = getStandardDeviation(list);

							result.append(" " + measure);
							result.append(" µ" + String.format("%1$8s", String.format("%.3f", mean)));
							result.append(" σ" + String.format("%1$8s", String.format("%.3f", stdev)));
							result.append("  ");
						} else {
							result.append(" " + measure);
							result.append("         -");
							result.append("-         ");
							result.append("  ");
						}

					} //measure

				}

				result.append("\n");

			} //algorithm
		} //log

		return result.toString();
	}
	
	public static String csvString(ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> results) throws IOException {

		int totalModels = parameters.getNumberOfRuns() * parameters.getNumberOfFolds();

		StringBuilder result = new StringBuilder();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			result.append("Log " + logFile.getName() + " (" + parameters.getLogAbbreviation(logFile) + ")\n");
			for (Algorithm algorithm : parameters.getAlgorithms()) {

				result.append(algorithm.getAbbreviation());
				result.append(",");

				Pair<Integer, Integer> p = completeModels(parameters, logFile, algorithm);
				if (p.getA() < totalModels) {
					//not done yet with mining
					result.append(" discovered " + p.getA());
					if (p.getB() > 0) {
						result.append(" errors " + p.getB());
					}
				} else {
					//done with mining
					for (String measure : getAllMeasures(parameters)) {
						TDoubleList list = results.get(Triple.of(logFile, algorithm, measure));

						if (isAllMeasuresPresent(list)) {
							double mean = list.sum() / list.size();

							result.append(measure);
							result.append(",");
							result.append(mean);
							result.append(",");
						} else {
							result.append(measure);
							result.append(",");
							result.append("-");
							result.append(",");
						}

					} //measure

				}

				result.append("\n");

			} //algorithm
		} //log

		return result.toString();
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
			if (d == -Double.MAX_VALUE || d == Double.MAX_VALUE) {
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
					if (Perform4Measures.isError(call.getModelFile())) {
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
			for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
				result.add(measure.getMeasureNames()[i]);
			}
		}
		result.add("time");
		return result;
	}

	/**
	 * The list contains -Double.maxvalue for errors, and Double.maxvalue for
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
					TDoubleList l = getTimeList(parameters, logFile, algorithm);
					result.put(Triple.of(logFile, algorithm, "time"), l);
				}

				//other measures
				for (Measure measure : parameters.getMeasures()) {
					getMeasureList(parameters, result, logFile, algorithm, measure);
				}
			}
		}
		return result;
	}

	private static void getMeasureList(ExperimentParameters parameters,
			Map<Triple<File, Algorithm, String>, TDoubleList> result, File logFile, Algorithm algorithm,
			Measure measure) throws IOException, FileNotFoundException {
		TDoubleList[] l = new TDoubleArrayList[measure.getNumberOfMeasures()];
		for (int i = 0; i < l.length; i++) {
			l[i] = new TDoubleArrayList();
			result.put(Triple.of(logFile, algorithm, measure.getMeasureNames()[i]), l[i]);
		}

		for (int runNr : parameters.getRuns()) {
			for (int foldNr : parameters.getFolds()) {
				Call call = new Call(parameters, algorithm, logFile, runNr, foldNr);

				if (call.isModelDone() && (Perform4Measures.isError(call.getModelFile())
						|| (call.isMeasureDone(measure) && Perform4Measures.isError(call.getMeasureFile(measure))))) {

					//error in model and/or measure
					for (int i = 0; i < l.length; i++) {
						l[i].add(Double.MAX_VALUE);
					}

				} else if (call.isModelDone() && call.isMeasureDone(measure)
						&& !Perform4Measures.isError(call.getMeasureFile(measure))) {

					//read in the file
					BufferedReader r = new BufferedReader(new FileReader(call.getMeasureFile(measure)));
					for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
						if (i < l.length) {
							l[i].add(Double.valueOf(r.readLine().split(" ")[0]));
						} else {
							l[l.length - 1].add(Double.valueOf(r.readLine().split(" ")[0]));
						}
					}
					r.close();
				} else {
					//measure not done yet
					for (int i = 0; i < l.length; i++) {
						l[i].add(-Double.MAX_VALUE);
					}
				}
			}
		}
	}

	private static TDoubleList getTimeList(ExperimentParameters parameters, File logFile, Algorithm algorithm)
			throws FileNotFoundException, IOException {
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
					l.add(-Double.MAX_VALUE);
				}
			}
		}
		return l;
	}
}