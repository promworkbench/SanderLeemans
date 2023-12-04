package bpm2020;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.processmining.plugins.InductiveMiner.Function;

import gnu.trove.map.hash.THashMap;
import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class MakeLogUnreliable {

	public final static String extension = "quality:";

	public static void main(String... args) throws Exception {

		String name = "bpic18 Control summary";
		File inputFile = new File("/home/sander/Documents/datasets/BPI challenge 2018/" + name + ".xes.gz");
		File outputFile = new File("/home/sander/Desktop/" + name + "-unreliabled.xes.gz");
		final Random random = new Random(1);
		final double threshold = 0.5;
		Map<String, String> activityLabelMap = new THashMap<>();
		activityLabelMap.put("initialize", "initial");
		activityLabelMap.put("begin editing", "begin");
		activityLabelMap.put("finish editing", "finish");
		activityLabelMap.put("save", "sav");

		final XLogWriterIncremental writer = new XLogWriterIncremental(outputFile);
		XLogParserIncremental.parseTraces(inputFile, new Function<XTrace, Object>() {
			public Object call(XTrace trace) throws Exception {

				for (XEvent event : trace) {
					//changeTimestampPrecise(random, event);
					changeAttributeLabel(random, activityLabelMap, event);
				}

				changeTimestamp(random, trace);

				writer.writeTrace(trace);

				return null;
			}

			private void changeTimestampPrecise(Random random, XEvent event) {
				XAttributeMap attributes = event.getAttributes();
				if (random.nextDouble() < threshold) {
					String resource = XOrganizationalExtension.instance().extractResource(event);
					String activityLabel = XConceptExtension.instance().extractName(event);

					if (true || resource.equals("2ca0ae") && activityLabel.equals("save")) {
						Date time = XTimeExtension.instance().extractTimestamp(event);
						time.setHours(0);
						time.setMinutes(0);
						time.setSeconds(0);
						long time2 = time.getTime() / 1000 * 1000;

						attributes.put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", time2));

						attributes.put(extension + "timestamp:precise",
								new XAttributeContinuousImpl(extension + "timestamp:precise", 0));

						return;
					}
				}
				attributes.put(extension + "timestamp:precise",
						new XAttributeContinuousImpl(extension + "timestamp:precise", 1));
			}

			private void changeTimestamp(Random random, XTrace trace) {
				long delta = 108000000;

				for (int i = 0; i < trace.size(); i++) {
					XEvent event = trace.get(i);
					XAttributeMap attributes = event.getAttributes();
					if (random.nextDouble() < threshold) {
						long time;
						if (i < trace.size() - 1) {
							time = XTimeExtension.instance().extractTimestamp(trace.get(i + 1)).getTime();
						} else {
							time = XTimeExtension.instance().extractTimestamp(trace.get(i)).getTime() + delta;
						}
						attributes.put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", time));

						attributes.put(extension + "timestamp:accurate",
								new XAttributeContinuousImpl(extension + "timestamp:accurate", 0));
					} else {
						attributes.put(extension + "timestamp:accurate",
								new XAttributeContinuousImpl(extension + "timestamp:accurate", 1));
					}
				}
			}

			private void changeAttributeLabel(final Random random, Map<String, String> activityLabelMap, XEvent event) {
				XAttributeMap attributes = event.getAttributes();
				if (random.nextDouble() < threshold) {
					String activityLabel = XConceptExtension.instance().extractName(event);
					String newActivityLabel = activityLabelMap.get(activityLabel);
					if (newActivityLabel != null) {
						attributes.put("concept:name", new XAttributeLiteralImpl("concept:name", newActivityLabel));
						attributes.put(extension + "activity:precise",
								new XAttributeContinuousImpl(extension + "activity:precise", 0));
						return;
					}
				}
				attributes.put(extension + "activity:precise",
						new XAttributeContinuousImpl(extension + "activity:precise", 1));
			}
		});

		writer.close();

		System.out.println("done");
	}
}
