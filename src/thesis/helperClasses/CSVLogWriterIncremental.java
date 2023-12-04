package thesis.helperClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import gnu.trove.iterator.TIntIterator;

public class CSVLogWriterIncremental implements LogWriterIncremental {
	private final PrintWriter file;
	private final String delimiter;
	private final String quote;
	private final String[] attributes;
	private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ");

	private String trace = null;
	private AtomicLong traceNr = new AtomicLong();

	/**
	 * 
	 * @param file
	 * @param attributes
	 *            The list of event attributes that will be exported. Only
	 *            attributes in this list will be exported. CSV files do not
	 *            support trace attributes.
	 * 
	 *            If this list is empty, then the attributes concept:name and
	 *            lifecycle:transition will be used.
	 * 
	 *            The trace identifier will always be included and does not need
	 *            to be specified as an event attribute.
	 * @throws IOException
	 */
	public CSVLogWriterIncremental(File file, String... attributes) throws IOException {
		this(",", "\"", file, attributes);
	}

	public CSVLogWriterIncremental(String delimiter, String quote, File file, String... attributes) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		this.delimiter = delimiter;
		this.quote = quote;
		this.file = new PrintWriter(writer, false);

		if (attributes.length == 0) {
			attributes = new String[] { "concept:name", "lifecycle:transition" };
		}
		this.attributes = attributes;

		this.file.print("trace id");
		this.file.print(delimiter);
		this.file.println(String.join(delimiter, attributes));
	}

	@Override
	public void writeTrace(int[] trace, String[] int2activity) {
		startTrace();
		for (int event : trace) {
			writeEvent(int2activity[event], "complete");
		}
		endTrace();
	}

	@Override
	public void writeTrace(TIntIterator iterator, String[] int2activity) {
		startTrace();
		while (iterator.hasNext()) {
			writeEvent(int2activity[iterator.next()], "complete");
		}
		endTrace();
	}

	@Override
	public void writeTrace(XTrace trace) {
		if (trace.getAttributes().containsKey(XConceptExtension.KEY_NAME)) {
			startTrace(XConceptExtension.instance().extractName(trace));
		} else {
			startTrace();
		}
		for (XEvent event : trace) {
			writeEvent(event);
		}
		endTrace();
	}

	@Override
	public void startTrace() {
		startTrace("t" + traceNr.incrementAndGet());
	}

	public void startTrace(String name) {
		trace = name;
	}

	public void endTrace() {
		trace = null;
	}

	public void writeEvent(String name, String lifeCycle) {
		file.write(trace);
		file.write(delimiter);
		for (String attribute : attributes) {
			if (attribute.equals("concept:name")) {
				writeAttribute(name);
			} else if (attribute.equalsIgnoreCase("lifecycle:transition")) {
				writeAttribute(lifeCycle);
			} else {

			}
			file.print(delimiter);
		}
		file.println();
	}

	public void writeEvent(XEvent event) {
		file.write(trace);
		file.write(delimiter);
		int attributeIndex = 0;
		for (String attribute : attributes) {
			if (event.getAttributes().containsKey(attribute)) {
				XAttribute xAtt = event.getAttributes().get(attribute);
				if (xAtt instanceof XAttributeTimestamp) {
					writeAttribute(format.format(((XAttributeTimestamp) xAtt).getValue()));
				} else {
					writeAttribute(xAtt.toString());
				}
			}

			if (attributeIndex < attributes.length - 1) {
				file.print(delimiter);
			}

			attributeIndex++;
		}
		file.println();
	}

	public void close() {
		file.flush();
		file.close();
	}

	private void writeAttribute(String value) {
		if (value.contains(quote)) {
			value = value.replaceAll(quote, quote + quote);
			file.write(quote + value + quote);
		} else if (value.contains(delimiter)) {
			file.write(quote + value + quote);
		} else {
			file.write(value);
		}
	}
}