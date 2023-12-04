package svn48healthcare.accuracytest;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.statisticaltests.test.XLogWriterIncremental;

import thesis.helperClasses.XLogParserIncremental;

public class svn48x1SplitLogs {
	public static void main(String[] args) {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getSplitLogsDirectory().mkdirs();
		parameters.getSplitLogsTestDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File logFile = call.getLogFile();
			File splitLogFile = call.getLogSplitFile();
			File splitLogFileTest = call.getLogSplitFileTest();

			Random random = new Random(call.getSeed());

			if (!call.isAttempted(splitLogFile)) {
				System.out.println("split log " + call.toString());
				try {

					XLogWriterIncremental writer = new XLogWriterIncremental(splitLogFile);
					XLogWriterIncremental writerTest = new XLogWriterIncremental(splitLogFileTest);
					XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {

						public Object call(XTrace input) throws Exception {
							if (random.nextInt(parameters.getBins()) == call.getBin()) {
								writer.writeTrace(input);
							} else {
								writerTest.writeTrace(input);
							}
							return null;
						}
					});

					writer.close();
					writerTest.close();

				} catch (Exception e) {
					Call.setError(splitLogFile, e);
				}
			}
		}

		System.out.println("done");
	}
}
