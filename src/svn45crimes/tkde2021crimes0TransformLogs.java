package svn45crimes;

import java.io.File;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class tkde2021crimes0TransformLogs {
	public static void main(String[] args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();

		File[] files = parameters.getLogDirectory().listFiles();

		for (File logFile : files) {
			if (logFile.getName().endsWith(".xes")) {
				XLogWriterIncremental writer = new XLogWriterIncremental(
						new File(logFile.getParentFile(), logFile.getName() + ".gz"));
				XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {
					public Object call(XTrace trace) throws Exception {
						writer.writeTrace(trace);
						return null;
					}
				});
				writer.close();

				logFile.delete();
			}
		}

		System.out.println("done");
	}
}
