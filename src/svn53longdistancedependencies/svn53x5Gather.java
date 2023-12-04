package svn53longdistancedependencies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.Quintuple;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class svn53x5Gather {
	public static void main(String... args) throws IOException, Exception {
		ExperimentParameters parameters = new ExperimentParameters();

		//find logs that are not complete
		Set<String> incompleteLogsDiscovery = new THashSet<>();
		{
			MultiSet<String> errorLogs = new MultiSet<>();
			for (Call call : parameters.getCalls()) {
				if (Call.isAttempted(call.getDiscoveredStochasticModelFile())
						&& Call.isError(call.getDiscoveredStochasticModelFile())) {
					System.out.println(call.getDiscoveredStochasticModelFile());
					//						call.getDiscoveredModelFile().delete();
					//call.getMeasureFile(measure).delete();
					errorLogs.add(call.getLogFile().getAbsolutePath());
				}
				//if (!Call.isAttempted(call.getMeasureFile(measure)) || Call.isError(call.getMeasureFile(measure))) {
				if (!Call.isAttempted(call.getDiscoveredStochasticModelFile())) {
					incompleteLogsDiscovery.add(call.getLogFile().getName());
				}
			}

			System.out.println(
					incompleteLogsDiscovery.size() + " logs not done with discovery " + incompleteLogsDiscovery);
			System.out.println("in total " + errorLogs.setSize() + " error logs");
			System.out.println(errorLogs.size() + " errors " + errorLogs);
		}

		//find logs that are not complete
		Set<String> incompleteLogsMeasure = new THashSet<>();
		{
			MultiSet<String> errorLogs = new MultiSet<>();
			for (Call call : parameters.getCalls()) {
				if (Call.isAttempted(call.getMeasureFile()) && Call.isError(call.getMeasureFile())) {
					System.out.println(call.getMeasureFile());
					//						call.getDiscoveredModelFile().delete();
					//call.getMeasureFile(measure).delete();
					errorLogs.add(call.getLogFile().getAbsolutePath());
				}
				//if (!Call.isAttempted(call.getMeasureFile(measure)) || Call.isError(call.getMeasureFile(measure))) {
				if (!Call.isAttempted(call.getMeasureFile())) {
					incompleteLogsMeasure.add(call.getLogFile().getName());
				}
			}

			System.out.println(incompleteLogsMeasure.size() + " logs not done with measures " + incompleteLogsMeasure);
			System.out.println("in total " + errorLogs.setSize() + " error logs");
			System.out.println(errorLogs.size() + " errors " + errorLogs);
		}

		//initialise
		THashMap<Quintuple<File, Algorithm, StochasticAlgorithm, Measure, Integer>, TDoubleList> results = new THashMap<>();
		//read values
		{
			for (File logFile : parameters.getLogDirectory().listFiles()) {
				for (Algorithm algorithm : parameters.getAlgorithms()) {
					for (StochasticAlgorithm stochasticAlgorithm : parameters.getStochasticAlgorithms()) {
						for (Measure measure : parameters.getMeasures()) {
							for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
								results.put(Quintuple.of(logFile, algorithm, stochasticAlgorithm, measure, meas),
										new TDoubleArrayList());
							}
						}
					}
				}
			}
			for (Call call : parameters.getCalls()) {
				if (Call.isAttempted(call.getMeasureFile()) && !Call.isError(call.getMeasureFile())) {

					BufferedReader reader = new BufferedReader(new FileReader(call.getMeasureFile()));
					for (int meas = 0; meas < call.getMeasure().getNumberOfMeasures(); meas++) {
						Quintuple<File, Algorithm, StochasticAlgorithm, Measure, Integer> key = Quintuple.of(
								call.getLogFile(), call.getAlgorithm(), call.getStochasticAlgorithm(),
								call.getMeasure(), meas);

						String line = reader.readLine();
						double value = Double.valueOf(line.substring(0, line.indexOf(' ')));

						results.get(key).add(value);
					}
					reader.close();
				}
			}
		}

		//print output
		{
			List<Call> calls = parameters.getCalls();

			{
				int discoveryErrors = 0;
				int discoveryDone = 0;
				long discoveryTime = 0;
				for (Call call : calls) {
					if (Call.isAttempted(call.getDiscoveredStochasticModelFile())) {
						discoveryDone++;
						if (call.isError(call.getDiscoveredStochasticModelFile())) {
							discoveryErrors++;
						} else {
							BufferedReader reader = new BufferedReader(
									new FileReader(call.getDiscoveredStochasticModelTimeFile()));
							String line = reader.readLine();
							discoveryTime += Long.valueOf(line);
							reader.close();
						}
					}
				}

				System.out.println("stochastic discovery: done " + discoveryDone + ", errors " + discoveryErrors
						+ ", of " + calls.size() + ", time " + discoveryTime);
			}

			{
				int measuresDone = 0;
				int measuresErrors = 0;
				long measuresTime = 0;
				long maxMeasureTime = 0;
				Call maxMeasureTimeCall = null;
				for (Call call : calls) {
					if (Call.isAttempted(call.getMeasureFile())) {
						measuresDone++;
						if (call.isError(call.getMeasureFile())) {
							measuresErrors++;
						} else {
							if (!call.getMeasureTimeFile().exists()) {
								System.out.println("svn remove \"" + call.getMeasureFile().getAbsolutePath() + "\"");
							} else {
								BufferedReader reader = new BufferedReader(new FileReader(call.getMeasureTimeFile()));
								String line = reader.readLine();
								long measureTime = Long.valueOf(line);
								measuresTime += measureTime;

								if (measureTime > maxMeasureTime) {
									maxMeasureTime = measureTime;
									maxMeasureTimeCall = call;
								}
								reader.close();
							}
						}
					}
				}

				System.out.println("measures: done " + measuresDone + ", errors " + measuresErrors + ", of "
						+ (calls.size() * parameters.getMeasures().size()) + ", time " + measuresTime);

				System.out.println("maximum time taken by " + maxMeasureTimeCall.getMeasureTimeFile());
			}
		}

		//output file
		{
			PrintWriter w = new PrintWriter(parameters.getOutputfile());

			w.print("\\begin{tabular}{lll");
			for (Measure measure : parameters.getMeasures()) {
				for (int m = 0; m < measure.getNumberOfMeasures(); m++) {
					w.print("r");
				}
			}
			w.println("}");
			w.println("\\toprule");
			w.print("Log & discovery & stochastic discovery ");
			for (Measure measure : parameters.getMeasures()) {
				w.print("& \\multicolumn{" + measure.getNumberOfMeasures() + "}{l}{" + measure.getLatexTitle() + "}");
			}
			//			w.println("\\\\");
			//			int i = 4;
			//			for (Measure measure : parameters.getMeasures()) {
			//				w.print("\\cmidrule{" + i + "-" + (i + measure.getNumberOfMeasures() - 1) + "}");
			//				i += measure.getNumberOfMeasures();
			//			}
			//			w.print("&&");
			//			for (Measure measure : parameters.getMeasures()) {
			//				for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
			//					w.print("&" + measure.getMeasureLatexNames()[meas]);
			//				}
			//			}
			w.println("\\\\");
			w.println("\\midrule");

			File lastLogFile = null;
			Algorithm lastAlgorithm = null;
			for (File logFile : parameters.getLogDirectory().listFiles()) {
				if (!incompleteLogsDiscovery.contains(logFile.getName())) {
					for (Algorithm algorithm : parameters.getAlgorithms()) {
						for (StochasticAlgorithm stochasticAlgorithm : parameters.getStochasticAlgorithms()) {
							w.print(logFile.equals(lastLogFile) ? ""
									: parameters.getLog2abbreviation().get(logFile.getName()));
							w.print("&" + (algorithm.equals(lastAlgorithm) ? "" : algorithm.getLatexName()));
							w.print("&" + stochasticAlgorithm.getLatexName());

							lastLogFile = logFile;
							lastAlgorithm = algorithm;

							for (Measure measure : parameters.getMeasures()) {
								for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {

									Quintuple<File, Algorithm, StochasticAlgorithm, Measure, Integer> key = Quintuple
											.of(logFile, algorithm, stochasticAlgorithm, measure, meas);
									TDoubleList values = results.get(key);

									if (values.size() == 10) {
										DecimalFormat f = new DecimalFormat(measure.getMeasureFormatting()[meas]);
										w.print("&" + f.format(values.sum() / values.size()));
									} else {
										//										w.print("&" + values.size() + " done");
										w.print("&-");
									}
								}
							}

							w.println("\\\\");
						}
					}
				}
			}
			w.println("\\bottomrule");
			w.println("\\end{tabular}");

			w.close();
		}
	}
}