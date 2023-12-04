package coopis2018;

import java.io.File;
import java.util.Random;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class Perform1LogSplitting {
	public static void main(String... args) throws Exception {
		final ExperimentParameters parameters = new ExperimentParameters();
		parameters.getSplitLogDirectory().mkdirs();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			for (int run : parameters.getRuns()) {

				if (!(new Call(parameters, new AlgorithmIMf(), logFile, run, 0)).getSplitLogDiscoveryFile().exists()) {

					/**
					 * Use the hashcode of the log to prevent dividing all logs
					 * in the same way.
					 */
					final Random random = new Random(run + logFile.getName().hashCode());

					final Call[] calls = new Call[parameters.getNumberOfFolds()];
					final XLogWriterIncremental[] discoveryLogs = new XLogWriterIncremental[parameters
							.getNumberOfFolds()];
					final XLogWriterIncremental[] measureLogs = new XLogWriterIncremental[parameters
							.getNumberOfFolds()];
					for (int fold : parameters.getFolds()) {
						Call call = new Call(parameters, new AlgorithmIMf(), logFile, run, fold);
						System.out.println(call);
						calls[fold] = call;
						discoveryLogs[fold] = new XLogWriterIncremental(call.getSplitLogDiscoveryFile());
						measureLogs[fold] = new XLogWriterIncremental(call.getSplitLogMeasureFile());
					}

					XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {
						public Object call(XTrace trace) throws Exception {

							/**
							 * Each trace needs to go into #f folds. We put it
							 * into a bin [0, #f).
							 */

							int bin = random.nextInt(parameters.getNumberOfFolds());

							for (int fold : parameters.getFolds()) {
								if (fold != bin) {
									//discovery
									discoveryLogs[fold].writeTrace(trace);
								} else {
									//measure
									measureLogs[fold].writeTrace(trace);
								}
							}

							return null;
						}
					});

					for (XLogWriterIncremental log : discoveryLogs) {
						log.close();
					}
					for (XLogWriterIncremental log : measureLogs) {
						log.close();
					}
				}
			}
		}
	}
}
