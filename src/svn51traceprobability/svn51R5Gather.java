package svn51traceprobability;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Quintuple;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class svn51R5Gather {
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
		THashMap<Quadruple<File, Algorithm, StochasticAlgorithm, Measure>, TLongList> timesMeasures = new THashMap<>();
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

							if (measure.printTime()) {
								timesMeasures.put(Quadruple.of(logFile, algorithm, stochasticAlgorithm, measure),
										new TLongArrayList());
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

					if (call.getMeasure().printTime()) {
						Quadruple<File, Algorithm, StochasticAlgorithm, Measure> key = Quadruple.of(call.getLogFile(),
								call.getAlgorithm(), call.getStochasticAlgorithm(), call.getMeasure());

						BufferedReader readerTime = new BufferedReader(new FileReader(call.getMeasureTimeFile()));

						String line = readerTime.readLine();
						long value = Long.valueOf(line);
						timesMeasures.get(key).add(value);

						readerTime.close();
					}
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

			w.print("\\begin{longtable}{lll");
			for (Measure measure : parameters.getMeasures()) {
				for (int m = 0; m < measure.getNumberOfMeasures(); m++) {
					w.print("r");
					if (measure.printTime()) {
						w.print("r");
					}
				}
			}
			w.println("}");
			w.println("\\caption{Results of the evaluation.}");
			w.println("\\label{tbl:results}");
			w.println("\\\\");
			w.println("\\toprule");
			w.print("Log & discovery & sto.dis. ");
			for (Measure measure : parameters.getMeasures()) {
				w.print("& \\multicolumn{"
						+ (measure.printTime() ? 1 + measure.getNumberOfMeasures() : measure.getNumberOfMeasures())
						+ "}{l}{" + measure.getLatexTitle() + "}");
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
			w.print("&&&");
			Iterator<Measure> itMeasure = parameters.getMeasures().iterator();
			while (itMeasure.hasNext()) {
				Measure measure = itMeasure.next();
				for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
					if (measure.getNumberOfMeasures() > 1) {
						w.print(measure.getMeasureLatexNames()[meas]);
					}
					w.print("&");
				}
				if (measure.printTime()) {
					w.print("time (ms)");
					if (itMeasure.hasNext()) {
						w.print("&");
					}
				}
			}
			w.println("\\\\");
			w.println("\\midrule");
			w.println("\\endhead");
			w.println("\\bottomrule");
			w.println("\\endfoot");

			File lastLogFile = null;
			Algorithm lastAlgorithm = null;
			List<File> files = Arrays.asList(parameters.getLogDirectory().listFiles());
			Collections.sort(files, new Comparator<File>() {
				public int compare(File o1, File o2) {
					String name1 = parameters.getLog2abbreviation().get(o1.getName());
					if (name1 == null) {
						name1 = o1.getName();
					}
					String name2 = parameters.getLog2abbreviation().get(o2.getName());
					if (name2 == null) {
						name2 = o1.getName();
					}
					return name1.toLowerCase().compareTo(name2.toLowerCase());
				}
			});
			Iterator<File> itLog = files.iterator();
			while (itLog.hasNext()) {
				File logFile = itLog.next();
				if (parameters.getLog2abbreviation().containsKey(logFile.getName())
						&& (true || !incompleteLogsDiscovery.contains(logFile.getName()))) {
					Iterator<Algorithm> itAlg = parameters.getAlgorithms().iterator();
					while (itAlg.hasNext()) {
						Algorithm algorithm = itAlg.next();

						Iterator<StochasticAlgorithm> itStochAlg = parameters.getStochasticAlgorithms().iterator();
						while (itStochAlg.hasNext()) {
							StochasticAlgorithm stochasticAlgorithm = itStochAlg.next();
							w.print(logFile.equals(lastLogFile) ? ""
									: parameters.getLog2abbreviation().get(logFile.getName()));
							w.print("&" + (algorithm.equals(lastAlgorithm) ? "" : algorithm.getLatexName()));
							w.print("&" + stochasticAlgorithm.getLatexName());

							lastLogFile = logFile;
							lastAlgorithm = algorithm;

							Iterator<Measure> itMeas = parameters.getMeasures().iterator();
							while (itMeas.hasNext()) {
								Measure measure = itMeas.next();
								for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {

									Quintuple<File, Algorithm, StochasticAlgorithm, Measure, Integer> key = Quintuple
											.of(logFile, algorithm, stochasticAlgorithm, measure, meas);
									TDoubleList values = results.get(key);

									if (values.size() == parameters.getRepetitions()) {
										DecimalFormat f = new DecimalFormat(measure.getMeasureFormatting()[meas]);
										w.print("&" + f.format(values.sum() / values.size()));
									} else {
										//										w.print("&" + values.size() + " done");
										w.print("&-");
									}
								}

								if (measure.printTime()) {
									Quadruple<File, Algorithm, StochasticAlgorithm, Measure> key = Quadruple.of(logFile,
											algorithm, stochasticAlgorithm, measure);
									TLongList time = timesMeasures.get(key);

									if (time.size() == parameters.getRepetitions()) {
										long timeValue = time.sum() / time.size();

										DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
										symbols.setGroupingSeparator(' ');

										DecimalFormat formatter = new DecimalFormat("###,###", symbols);

										w.print("&" + formatter.format(timeValue).replace(" ", "\\,"));
									} else {
										w.print("&-");
									}
								}
							}

							if (itStochAlg.hasNext() || itAlg.hasNext()) {
								w.print("\\\\*");
							} else {
								w.print("\\\\");
							}
						}
						if (itAlg.hasNext() || itLog.hasNext()) {
							w.println("[0.1cm]");
						}
					}
				} else {
					System.out.println(" log " + logFile.getName() + " excluded");
				}
			}
			w.println("\\end{longtable}");

			w.close();
		}

		//print times
		{
			Map<StochasticAlgorithm, TDoubleList> times = new THashMap<>();

			for (StochasticAlgorithm stochasticAlgorithm : parameters.getStochasticAlgorithms()) {
				times.put(stochasticAlgorithm, new TDoubleArrayList());
			}

			List<File> files = Arrays.asList(parameters.getLogDirectory().listFiles());
			Iterator<File> itLog = files.iterator();
			while (itLog.hasNext()) {
				File logFile = itLog.next();
				if (parameters.getLog2abbreviation().containsKey(logFile.getName())
						&& (!incompleteLogsDiscovery.contains(logFile.getName()))) {
					for (Measure measure : parameters.getMeasures()) {
						if (measure.getTitle().equals("time")) {
							for (Algorithm algorithm : parameters.getAlgorithms()) {
								for (StochasticAlgorithm stochasticAlgorithm : parameters.getStochasticAlgorithms()) {

									Quintuple<File, Algorithm, StochasticAlgorithm, Measure, Integer> key = Quintuple
											.of(logFile, algorithm, stochasticAlgorithm, measure, 0);

									times.get(stochasticAlgorithm).addAll(results.get(key));
								}
							}
						}
					}
				}
			}

			PrintWriter w = new PrintWriter(parameters.getOutputfileTimes());
			TDoubleIterator[] its = new TDoubleIterator[times.size()];
			int i = 0;
			w.print("x ");
			for (Entry<StochasticAlgorithm, TDoubleList> e : times.entrySet()) {
				e.getValue().sort();
				its[i] = e.getValue().iterator();
				w.print(e.getKey().getAbbreviation() + " ");
				i++;
			}
			w.println();
			int j = 0;
			while (its[0].hasNext()) {
				w.print(j + " ");
				j++;
				for (TDoubleIterator it : its) {
					w.print(it.next() + " ");
				}
				w.println();
			}

			w.close();
		}

		//print times for measures
		{
			Map<Measure, TDoubleList> times = new THashMap<>();
			List<Algorithm> algorithms = new ArrayList<>();
			List<StochasticAlgorithm> stochAlgos = new ArrayList<>();

			List<Class<?>> addColumns = new ArrayList<>();
			addColumns.add(MeasureSilentTransitions.class);
			addColumns.add(MeasureLog.class);

			for (Measure measure : parameters.getMeasures()) {
				if (measure.printTime() || addColumns.contains(measure.getClass())) {
					times.put(measure, new TDoubleArrayList());
				}
			}

			List<File> files = Arrays.asList(parameters.getLogDirectory().listFiles());
			Iterator<File> itLog = files.iterator();
			while (itLog.hasNext()) {
				File logFile = itLog.next();
				if (parameters.getLog2abbreviation().containsKey(logFile.getName())
						&& (!incompleteLogsMeasure.contains(logFile.getName()))) {
					for (Measure measure : parameters.getMeasures()) {
						if (measure.printTime()) {
							for (Algorithm algorithm : parameters.getAlgorithms()) {
								for (StochasticAlgorithm stochasticAlgorithm : parameters.getStochasticAlgorithms()) {

									Quadruple<File, Algorithm, StochasticAlgorithm, Measure> key = Quadruple.of(logFile,
											algorithm, stochasticAlgorithm, measure);
									for (long x : timesMeasures.get(key).toArray()) {
										times.get(measure).add(x);
										algorithms.add(algorithm);
										stochAlgos.add(stochasticAlgorithm);
									}
								}
							}
						} else if (addColumns.contains(measure.getClass())) {
							for (Algorithm algorithm : parameters.getAlgorithms()) {
								for (StochasticAlgorithm stochasticAlgorithm : parameters.getStochasticAlgorithms()) {

									Quintuple<File, Algorithm, StochasticAlgorithm, Measure, Integer> key = Quintuple
											.of(logFile, algorithm, stochasticAlgorithm, measure, 0);

									TDoubleIterator itAlg = results.get(key).iterator();
									while (itAlg.hasNext()) {
										times.get(measure).add(itAlg.next());
										algorithms.add(algorithm);
										stochAlgos.add(stochasticAlgorithm);
									}
								}
							}
						}
					}
				}
			}

			PrintWriter w = new PrintWriter(parameters.getOutputfileMeasureTimes());
			TDoubleIterator[] its = new TDoubleIterator[times.size()];
			Iterator<Algorithm> itAlg = algorithms.iterator();
			Iterator<StochasticAlgorithm> itStAl = stochAlgos.iterator();
			int i = 0;
			w.print("algorithm ");
			w.print("stochastic-algorithm ");
			for (Entry<Measure, TDoubleList> e : times.entrySet()) {
				its[i] = e.getValue().iterator();
				w.print(e.getKey().getTitle().replace(' ', '-') + " ");
				i++;
			}
			w.println();
			while (its[0].hasNext()) {
				w.print(itAlg.next().getAbbreviation() + " ");
				w.print(itStAl.next().getAbbreviation() + " ");
				for (TDoubleIterator it : its) {
					w.print(it.next() + " ");
				}
				w.println();
			}
			w.close();
		}

		//print values for measures
		{
			Map<Measure, TDoubleList> times = new THashMap<>();
			List<Algorithm> algorithms = new ArrayList<>();
			List<StochasticAlgorithm> stochAlgos = new ArrayList<>();

			for (Measure measure : parameters.getMeasures()) {
				times.put(measure, new TDoubleArrayList());
			}

			List<File> files = Arrays.asList(parameters.getLogDirectory().listFiles());
			Iterator<File> itLog = files.iterator();
			while (itLog.hasNext()) {
				File logFile = itLog.next();
				if (parameters.getLog2abbreviation().containsKey(logFile.getName())
						&& (!incompleteLogsMeasure.contains(logFile.getName()))) {
					for (Measure measure : parameters.getMeasures()) {
						for (Algorithm algorithm : parameters.getAlgorithms()) {
							for (StochasticAlgorithm stochasticAlgorithm : parameters.getStochasticAlgorithms()) {

								Quintuple<File, Algorithm, StochasticAlgorithm, Measure, Integer> key = Quintuple
										.of(logFile, algorithm, stochasticAlgorithm, measure, 0);

								TDoubleIterator itR = results.get(key).iterator();
								while (itR.hasNext()) {
									times.get(measure).add(itR.next());
									algorithms.add(algorithm);
									stochAlgos.add(stochasticAlgorithm);
								}
							}
						}
					}
				}
			}

			PrintWriter w = new PrintWriter(parameters.getOutputfileMeasures());
			TDoubleIterator[] its = new TDoubleIterator[times.size()];
			Iterator<Algorithm> itAlg = algorithms.iterator();
			Iterator<StochasticAlgorithm> itStAl = stochAlgos.iterator();
			int i = 0;
			w.print("x ");
			w.print("algorithm ");
			w.print("stochastic-algorithm ");
			for (Entry<Measure, TDoubleList> e : times.entrySet()) {
				its[i] = e.getValue().iterator();
				w.print(e.getKey().getTitle().replace(' ', '-') + " ");
				i++;
			}
			w.println();
			int j = 0;
			while (its[0].hasNext()) {
				w.print(j + " ");
				w.print(itAlg.next().getAbbreviation() + " ");
				w.print(itStAl.next().getAbbreviation() + " ");
				j++;
				for (TDoubleIterator it : its) {
					w.print(it.next() + " ");
				}
				w.println();
			}
			w.close();
		}
	}
}