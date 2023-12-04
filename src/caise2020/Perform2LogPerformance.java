package caise2020;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.Function;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
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

					final THashMap<String, BigInteger> activity2sum = new THashMap<>();
					final TObjectIntMap<String> activity2count = new TObjectIntHashMap<>(10, 0.5f, 0);

					final AtomicReference<BigInteger> traceSum = new AtomicReference<>(BigInteger.ZERO);
					final AtomicInteger traceCount = new AtomicInteger(0);

					XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {
						public Object call(XTrace trace) throws Exception {
							long firstTimestamp = -1;
							long previousTimestamp = -1;

							for (XEvent event : trace) {
								String activity = XConceptExtension.instance().extractName(event);

								//only consider completion events
								if (event.getAttributes().containsKey(XLifecycleExtension.KEY_TRANSITION)) {
									String lifecycle = XLifecycleExtension.instance().extractTransition(event);
									if (!lifecycle.toLowerCase().equals("complete")) {
										continue;
									}
								}

								//only consider events with timestamps
								if (!event.getAttributes().containsKey(XTimeExtension.KEY_TIMESTAMP)) {
									continue;
								}
								long currentTimestamp = XTimeExtension.instance().extractTimestamp(event).getTime();

								if (firstTimestamp == -1) {
									firstTimestamp = currentTimestamp;
								}

								if (previousTimestamp != -1) {
									long duration = currentTimestamp - previousTimestamp;
									activity2count.adjustOrPutValue(activity, 1, 1);

									activity2sum.putIfAbsent(activity, BigInteger.ZERO);
									activity2sum.put(activity,
											activity2sum.get(activity).add(BigInteger.valueOf(duration)));
								}

								previousTimestamp = currentTimestamp;
							}

							if (firstTimestamp != -1) {
								traceCount.incrementAndGet();
								traceSum.set(
										traceSum.get().add(BigInteger.valueOf(previousTimestamp - firstTimestamp)));
							}

							return null;
						}
					});

					//write result
					PrintWriter writer = new PrintWriter(logMeasuresFile);
					writer.write(traceSum.get().divide(BigInteger.valueOf(traceCount.get())) + "\n");
					for (String activity : activity2sum.keySet()) {
						BigInteger avg = activity2sum.get(activity)
								.divide(BigInteger.valueOf(activity2count.get(activity)));
						writer.write(avg + " " + activity + "\n");
					}

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