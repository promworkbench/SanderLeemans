package svn45crimes;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class NoiseFreshActivity implements Noise {

	public String getTitle() {
		return "fresh";
	}

	public String getLatexTitle() {
		return "insert fresh activity";
	}

	public void compute(File inputLogFile, Call call, File outputLogFile) throws IOException, Exception {
		XLogWriterIncremental writer = new XLogWriterIncremental(outputLogFile);
		XFactoryNaiveImpl factory = new XFactoryNaiveImpl();

		Random random = new Random(call.getSeed());
		AtomicLong acts = new AtomicLong();

		XLogParserIncremental.parseTraces(inputLogFile, new Function<XTrace, Object>() {
			public Object call(XTrace input) throws Exception {

				XTrace trace = factory.createTrace();
				for (XEvent event : input) {
					trace.add(event);

					if (random.nextDouble() < call.getNoiseAmount()) {
						String activity = "a" + random.nextInt(50);
						XEvent event2 = factory.createEvent();
						event2.getAttributes().put(XConceptExtension.KEY_NAME,
								new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, activity));
						event2.getAttributes().put(XLifecycleExtension.KEY_TRANSITION,
								new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "complete"));
						trace.add(event2);
					}
				}
				writer.writeTrace(trace);

				return null;
			}
		});

		writer.close();
	}

}
