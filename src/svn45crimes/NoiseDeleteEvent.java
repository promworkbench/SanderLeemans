package svn45crimes;

import java.io.File;
import java.util.Iterator;
import java.util.Random;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class NoiseDeleteEvent implements Noise {

	public String getTitle() {
		return "deleteevent";
	}

	public String getLatexTitle() {
		return "delete event";
	}

	public void compute(File inputLogFile, final Call call, File outputLogFile) throws Exception {
		XLogWriterIncremental writer = new XLogWriterIncremental(outputLogFile);

		Random random = new Random(call.getSeed());

		XLogParserIncremental.parseTraces(inputLogFile, new Function<XTrace, Object>() {
			public Object call(XTrace input) throws Exception {

				for (Iterator<XEvent> it = input.iterator(); it.hasNext();) {
					it.next();
					if (random.nextDouble() < call.getNoiseAmount()) {
						it.remove();
					}
				}
				writer.writeTrace(input);

				return null;
			}
		});

		writer.close();
	}
}