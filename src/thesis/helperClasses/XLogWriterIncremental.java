package thesis.helperClasses;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.zip.GZIPOutputStream;

import org.deckfour.spex.util.SXmlCharacterMethods;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.util.XsDateTimeConversion;
import org.deckfour.xes.util.XsDateTimeConversionJava7;

import gnu.trove.iterator.TIntIterator;

public class XLogWriterIncremental implements LogWriterIncremental, Closeable {

	private final PrintWriter file;

	private boolean traceIsStarted = false;
	private final AtomicBoolean cancelled;

	protected XsDateTimeConversion xsDateTimeConversion = new XsDateTimeConversionJava7();

	public XLogWriterIncremental(File file) throws IOException {
		this(file, new AtomicBoolean(false));
	}

	public XLogWriterIncremental(File file, AtomicBoolean cancelled) throws IOException {
		this.cancelled = cancelled;

		GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(file));
		//OutputStream zip = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
		this.file = new PrintWriter(writer, false);

		this.file.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		this.file.println(
				"<log xes.version=\"1.0\" xes.features=\"nested-attributes\" openxes.version=\"1.0RC7\" xmlns=\"http://www.xes-standard.org/\">");
		this.file.println(
				" <extension name=\"Concept\" prefix=\"concept\" uri=\"http://www.xes-standard.org/concept.xesext\"/>");
		this.file.println(" <global scope=\"trace\">");
		this.file.println("  <string key=\"concept:name\" value=\"__INVALID__\"/>");
		this.file.println(" </global>");
		this.file.println(" <global scope=\"event\">");
		this.file.println("  <string key=\"concept:name\" value=\"__INVALID__\"/>");
		this.file.println("  <string key=\"lifecycle:transition\" value=\"complete\"/>");
		this.file.println(" </global>");
		this.file.println(" <classifier name=\"Event Name\" keys=\"concept:name\"/>");
	}

	public void writeTrace(int[] trace, String[] int2activity, long time) {
		if (cancelled.get()) {
			return;
		}

		startTrace();
		for (int event : trace) {
			writeEvent(int2activity[event], "complete", time);
		}
		endTrace();
	}

	public void writeTrace(int[] trace, String[] int2activity) {
		writeTrace(trace, int2activity, -1);
	}

	public void writeTrace(TIntIterator iterator, String[] int2activity) {
		if (cancelled.get()) {
			return;
		}

		startTrace();
		while (iterator.hasNext()) {
			writeEvent(int2activity[iterator.next()], "complete");
		}
		endTrace();
	}

	public void writeTrace(XTrace trace) {
		if (cancelled.get()) {
			return;
		}

		startTrace();
		writeAttributes(trace.getAttributes());
		for (XEvent event : trace) {
			writeEvent(event);
		}
		endTrace();
	}

	public void startTrace() {
		traceIsStarted = true;
		file.print(" <trace>");
	}

	public void endTrace() {
		traceIsStarted = false;
		file.print(" </trace>");
	}

	public void writeEvent(String name, String lifeCycle) {
		writeEvent(name, lifeCycle, -1);
	}

	public void writeEvent(String name, String lifeCycle, long time) {
		if (cancelled.get()) {
			return;
		}

		if (!traceIsStarted) {
			startTrace();
		}
		file.print("  <event>");
		file.print("   <string key=\"concept:name\" value=\"" + name + "\"/>");
		file.print("   <string key=\"lifecycle:transition\" value=\"" + lifeCycle + "\"/>");
		if (time >= 0) {
			file.print("   <date key=\"time:timestamp\" value=\"2018-07-28T19:00:00.000+10:00\"/>");
		}
		file.print("  </event>");
	}

	public void writeEvent(XEvent event) {
		if (cancelled.get()) {
			return;
		}

		if (!traceIsStarted) {
			startTrace();
		}
		file.print("  <event>");
		writeAttributes(event.getAttributes());
		file.print("  </event>");
	}

	public void close() {
		if (traceIsStarted) {
			endTrace();
		}
		file.print("</log>");
		file.flush();
		file.close();
	}

	public void writeAttributes(XAttributeMap attributeMap) {
		if (cancelled.get()) {
			return;
		}

		attributeMap.forEach(new BiConsumer<String, XAttribute>() {
			public void accept(String name, XAttribute attribute) {
				if (attribute instanceof XAttributeLiteral) {
					writeAttribute("string", name, attribute.toString());
				} else if (attribute instanceof XAttributeDiscrete) {
					writeAttribute("int", name, attribute.toString());
				} else if (attribute instanceof XAttributeContinuous) {
					writeAttribute("float", name, attribute.toString());
				} else if (attribute instanceof XAttributeTimestamp) {
					Date timestamp = ((XAttributeTimestamp) attribute).getValue();
					writeAttribute("date", name, xsDateTimeConversion.format(timestamp));
				} else if (attribute instanceof XAttributeBoolean) {
					writeAttribute("boolean", name, attribute.toString());
				} else if (attribute instanceof XAttributeID) {
					writeAttribute("id", name, attribute.toString());
				} else {
					System.out.println("Unknown attribute " + name + " being " + attribute.toString());
				}
			}
		});
	}

	private void writeAttribute(String tag, String name, String value) {
		file.write("<" + tag + " key=\"" + name + "\" value=\"" + SXmlCharacterMethods.convertCharsToXml(value.trim())
				+ "\"/>");
	}
}
