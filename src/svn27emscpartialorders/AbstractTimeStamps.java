package svn27emscpartialorders;

import java.io.File;
import java.util.Calendar;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class AbstractTimeStamps {
	public static void main(String[] args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		File folder = parameters.getLogDirectory();

		File inFile = new File(folder, "Road_Traffic_Fine_Management_Process.xes.gz");
		File outFile = new File(folder, "Road_Traffic_Fine_Management_Process-abstractedTime.xes.gz");

		XLogWriterIncremental writer = new XLogWriterIncremental(outFile);
		XLogParserIncremental.parseTraces(inFile, new Function<XTrace, Object>() {
			public Object call(XTrace trace) throws Exception {

				for (XEvent event : trace) {
					if (event.getAttributes().containsKey(XTimeExtension.KEY_TIMESTAMP)) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(XTimeExtension.instance().extractTimestamp(event));
						calendar.set(Calendar.MILLISECOND, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.HOUR, 0);
						calendar.set(Calendar.DAY_OF_MONTH, 0);
						calendar.set(Calendar.MONTH, 0);

						event.getAttributes().put(XTimeExtension.KEY_TIMESTAMP,
								new XAttributeTimestampImpl(XTimeExtension.KEY_TIMESTAMP, calendar.getTime()));
					}
				}

				writer.writeTrace(trace);

				return null;
			}
		});

		writer.close();
	}
}
