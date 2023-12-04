package svn22multilevelmining;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import com.raffaeleconforti.memorylog.XAttributeLiteralImpl;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class SplitLog {

	static XEventClassifier classifier = new XEventNameClassifier();

	public static void main(String... args) throws Exception {
		File inputFile = new File(
				"\\\\rstore.qut.edu.au\\projects\\sef\\rmsanalysis\\StudentJourney\\new datas set\\withdrawn analysis\\eventlog_fulltimev2.xes.gz");

		File outputfile = new File(
				"\\\\rstore.qut.edu.au\\projects\\sef\\rmsanalysis\\StudentJourney\\new datas set\\withdrawn analysis\\eventlog_fulltimev2-stageAnnotated.xes.gz");

		final XLogWriterIncremental writer = new XLogWriterIncremental(outputfile);
		XLogParserIncremental.parseTraces(inputFile, new Function<XTrace, Object>() {
			public Object call(XTrace trace) throws Exception {
				int lastStage2 = getLastStage2(trace);
				int lastConfirmation = getLastConfirmation(trace);

				int i = 0;
				for (XEvent event : trace) {
					String stage;
					if (i <= lastStage2) {
						stage = "stage 2";
					} else if (i <= lastConfirmation) {
						stage = "confirmation";
					} else {
						stage = "last one";
					}

					event.getAttributes().put("stage", new XAttributeLiteralImpl("stage", stage));

					i++;
				}

				writer.writeTrace(trace);

				return null;
			}
		});
		
		writer.close();
	}

	private static int getLastConfirmation(XTrace trace) {
		return Math.max(
				Math.max(getLast(trace, "Confirmation Seminar"),
						getLast(trace, "Confirmation Seminar Request for Information")),
				getLast(trace, "Confirmation Seminar Re-submission"));
	}

	public static int getLastStage2(XTrace trace) {
		return Math.max(
				Math.max(Math.max(getLast(trace, "Stage 2"), getLast(trace, "Stage 2 Re-submission")),
						getLast(trace, "Stage 2 Request for Information")),
				getLast(trace, "Stage 2 Re-submission Due Date"));
	}

	public static int getLast(XTrace trace, String activity) {
		int result = -1;
		int i = 0;
		for (XEvent event : trace) {
			if (classifier.getClassIdentity(event).equals(activity)) {
				result = i;
			}
			i++;
		}
		return result;
	}
}