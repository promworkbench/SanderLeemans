package bpm2020cohortanalysis;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfo;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class Perform1LogMeasures {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getLogMeasuresDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {

			Call call = new Call(logFile.getName(), -1, parameters);

			File logMeasuresFile = call.getLogMeasuresFile();

			try {

				if (!Call.isAttempted(logMeasuresFile)) {
					System.out.println("getting measures from " + logFile);

					XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

					AttributesInfo info = new AttributesInfoImpl(log);
					int traceAttributes = info.getTraceAttributes().size();
					int traces = log.size();
					int activities = info.getEventAttributeValues("concept:name").getStringValues().size();

					int events = 0;
					for (XTrace trace : log) {
						events += trace.size();
					}

					//write result
					PrintWriter writer = new PrintWriter(logMeasuresFile);
					writer.write(traces + " traces\n");
					writer.write(traceAttributes + " trace attributes\n");
					writer.write(activities + " activities\n");
					writer.write(events + " events\n");

					writer.flush();
					writer.close();
				}
			} catch (Exception e) {
				PrintWriter writer = new PrintWriter(logMeasuresFile);
				writer.write("error\n");
				e.printStackTrace(writer);
				writer.flush();
				writer.close();
			}
		}
	}
}