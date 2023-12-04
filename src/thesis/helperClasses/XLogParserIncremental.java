package thesis.helperClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.xeslite.parser.XesLiteXmlParser;

public class XLogParserIncremental {
	public static void parseEvents(File file, Function<XEvent, Object> eventCallback, final Runnable traceCallback)
			throws Exception {
		if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("gz")) {
			InputStream input = new GZIPInputStream(new FileInputStream(file));
			try {
				new XesLiteXmlParser(new XFactoryIncrementalEvent(eventCallback, traceCallback), true).parse(input);
			} finally {
				input.close();
			}
		} else {
			new XesLiteXmlParser(new XFactoryIncrementalEvent(eventCallback, traceCallback), true).parse(file);
		}

	}

	public static void parseTraces(File file, Function<XTrace, Object> traceCallback) throws Exception {
		if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("gz")) {
			InputStream input = null;
			try {
				input = new GZIPInputStream(new FileInputStream(file));
				new XesLiteXmlParser(new XFactoryIncrementalTrace(traceCallback), true).parse(input);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		} else {
			new XesLiteXmlParser(new XFactoryIncrementalTrace(traceCallback), true).parse(file);
		}
	}
}
