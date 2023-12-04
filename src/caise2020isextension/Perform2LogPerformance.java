package caise2020isextension;

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.Function;

import gnu.trove.set.hash.THashSet;
import thesis.helperClasses.FakeContext;
import thesis.helperClasses.XLogParserIncremental;

public class Perform2LogPerformance {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getLogPerformanceDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {

			Call call = new Call(logFile.getName(), null, parameters);

			File logMeasuresFile = call.getLogMeasuresFile();

			try {

				if (!Call.isAttempted(logMeasuresFile)) {
					System.out.println("getting measures from " + logMeasuresFile);

					final THashSet<String> activities = new THashSet<>();
					final AtomicInteger eventCount = new AtomicInteger(0);
					final AtomicInteger traceCount = new AtomicInteger(0);

					XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {
						public Object call(XTrace trace) throws Exception {
							traceCount.getAndIncrement();

							for (XEvent event : trace) {
								eventCount.getAndIncrement();

								//only consider completion events
								if (event.getAttributes().containsKey(XLifecycleExtension.KEY_TRANSITION)) {
									String lifecycle = XLifecycleExtension.instance().extractTransition(event);
									if (lifecycle.toLowerCase().equals("complete")) {
										String activity = XConceptExtension.instance().extractName(event);
										activities.add(activity);

									}
								}
							}
							return null;
						}
					});

					//write result
					PrintWriter writer = new PrintWriter(logMeasuresFile);
					writer.write(traceCount.get() + "\n");
					writer.write(eventCount.get() + "\n");
					writer.write(activities.size() + "\n");
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