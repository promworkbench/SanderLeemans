import java.io.File;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class Unzip {
	public static void main(String... args) throws Exception {
		final XLogWriterIncremental writer = new XLogWriterIncremental(new File("gzipped xmlXES event log.gz"));

		XLogParserIncremental.parseTraces(new File("c:/users/sander/desktop/gzipped xmlXES event log.gz"),
				new Function<XTrace, Object>() {
					public Object call(XTrace trace) throws Exception {
						writer.writeTrace(trace);
						return null;
					}
				});

		writer.close();
	}
}
