package svn45crimes;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class NoiseSwapEvents implements Noise {

	public String getTitle() {
		return "swap";
	}

	public String getLatexTitle() {
		return "swap events";
	}

	public void compute(File inputLogFile, Call call, File outputLogFile) throws IOException, Exception {
		XLogWriterIncremental writer = new XLogWriterIncremental(outputLogFile);

		Random random = new Random(call.getSeed());

		XLogParserIncremental.parseTraces(inputLogFile, new Function<XTrace, Object>() {
			public Object call(XTrace trace) throws Exception {

				for (int i = 0; i < trace.size() - 1; i++) {
					if (random.nextDouble() < call.getNoiseAmount()) {
						XEvent a = trace.get(i);
						XEvent b = trace.get(i + 1);
						trace.set(i, b);
						trace.set(i + 1, a);
						i++;
					}
				}
				writer.writeTrace(trace);

				return null;
			}
		});

		writer.close();
	}

}
