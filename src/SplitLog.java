import java.io.File;
import java.util.Iterator;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.log.exporting.ExportLogXesGz;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import com.raffaeleconforti.efficientlog.XAttributeLiteralImpl;

import thesis.helperClasses.FakeContext;

public class SplitLog {

	static XEventClassifier classifier = new XEventNameClassifier();

	public static void main(String... args) throws Exception {
		//File file = new File(
		//		"\\\\rstore.qut.edu.au\\projects\\sef\\rmsanalysis\\StudentJourney\\new datas set\\profilerlog_v7.xes.gz");
		File file = new File(
				"\\\\rstore.qut.edu.au\\projects\\sef\\rmsanalysis\\StudentJourney\\new datas set\\withdrawn analysis\\eventlog_fulltimev2.xes.gz");

		File outputFile1 = new File(file.getAbsoluteFile() + "-10.xes.gz");
		log1(file, outputFile1);

		File outputFile2 = new File(file.getAbsoluteFile() + "-20.xes.gz");
		log2(file, outputFile2);

		File outputFile3 = new File(file.getAbsoluteFile() + "-30.xes.gz");
		log3(file, outputFile3);
	}

	private static void log1(File inputFile, File outputFile) throws Exception {
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(new FakeContext(), inputFile);
		for (Iterator<XTrace> it = log.iterator(); it.hasNext();) {
			XTrace trace = it.next();

			int lastStage2 = getLastStage2(trace);

			if (!removeTail(trace, it, lastStage2, false)) {
				continue;
			}
		}
		new ExportLogXesGz().export(null, log, outputFile);
	}

	private static void log2(File inputFile, File outputFile) throws Exception {
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(new FakeContext(), inputFile);
		for (Iterator<XTrace> it = log.iterator(); it.hasNext();) {
			XTrace trace = it.next();

			int lastStage2 = getLastStage2(trace);

			if (!removeHead(trace, it, lastStage2, true)) {
				continue;
			}

			XAttributeMap att0 = trace.get(0).getAttributes();
			att0.put("concept:name", new XAttributeLiteralImpl("concept:name", "Stage 2 (last step)"));
			XAttributeMap att1 = trace.get(1).getAttributes();
			att1.put("concept:name", new XAttributeLiteralImpl("concept:name", "Stage 2 (last step)"));

			int lastConfirmation = getLastConfirmation(trace);

			if (!removeTail(trace, it, lastConfirmation, false)) {
				continue;
			}
		}
		new ExportLogXesGz().export(null, log, outputFile);
	}

	private static void log3(File inputFile, File outputFile) throws Exception {
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(new FakeContext(), inputFile);
		for (Iterator<XTrace> it = log.iterator(); it.hasNext();) {
			XTrace trace = it.next();

			int lastConfirmation = getLastConfirmation(trace);

			if (!removeHead(trace, it, lastConfirmation, true)) {
				continue;
			}

			XAttributeMap att0 = trace.get(0).getAttributes();
			att0.put("concept:name", new XAttributeLiteralImpl("concept:name", "Confirmation (last step)"));
			XAttributeMap att1 = trace.get(1).getAttributes();
			att1.put("concept:name", new XAttributeLiteralImpl("concept:name", "Confirmation (last step)"));
		}
		new ExportLogXesGz().export(null, log, outputFile);
	}

	private static boolean removeHead(XTrace trace, Iterator<XTrace> it, int from, boolean removeNonTraces) {
		//remove the head
		int index = from;
		if (index > -1) {
			for (int i = 0; i < index - 1; i++) {
				trace.remove(0);
			}
		} else {
			if (removeNonTraces) {
				it.remove();
			}
			return false;
		}
		return true;
	}

	private static boolean removeTail(XTrace trace, Iterator<XTrace> it, int from, boolean removeNonTraces) {
		//remove the tail
		int index = from;
		if (index > -1) {
			while (index + 1 < trace.size()) {
				trace.remove(index + 1);
			}
		} else {
			if (removeNonTraces) {
				it.remove();
			}
			return false;
		}
		return true;
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