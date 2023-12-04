package svn53longdistancedependenciesresample;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.statisticaltests.test.XLogWriterIncremental;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;
import thesis.helperClasses.XLogParserIncremental;

public class svn53Rx1SplitLogs {
	public static void main(String[] args) {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getSplitLogsDirectory().mkdirs();
		parameters.getSplitLogsTestDirectory().mkdirs();
		PluginContext context = new FakeContext();

		List<Call> calls = parameters.getCalls();
		for (Call call : calls) {
			File logFile = call.getLogFile();
			File splitLogFile = call.getSplitLogFile();
			File splitLogFileTest = call.getSplitLogTestFile();

			Random random = new Random(call.getSeed());

			if (!call.isAttempted(splitLogFile)) {
				System.out.println("split log " + call.toString());
				try {

					XLog testLog = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getLogFile());

					XLogWriterIncremental writer = new XLogWriterIncremental(splitLogFile);
					for (int i = 0; i < testLog.size(); i++) {
						int trace = random.nextInt(testLog.size());
						writer.writeTrace(testLog.get(trace));
					}

					XLogWriterIncremental writerTest = new XLogWriterIncremental(splitLogFileTest);
					XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {
						public Object call(XTrace input) throws Exception {
							writerTest.writeTrace(input);
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
