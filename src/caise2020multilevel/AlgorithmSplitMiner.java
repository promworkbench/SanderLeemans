package caise2020multilevel;

import java.io.File;
import java.io.IOException;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import com.raffaeleconforti.memorylog.XAttributeLiteralImpl;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class AlgorithmSplitMiner extends AlgorithmFlatAbstract {

	public String getName() {
		return "split miner";
	}

	public String getAbbreviation() {
		return "sm";
	}

	public String getFileExtension() {
		return ".bpmn";
	}

	public String getFlattenedFileExtension() {
		return ".bpmn";
	}

	public static File getTempLogFile(File logFile, final XEventClassifier classifier) throws Exception {
		File tempFile = File.createTempFile("log", ".xes.gz");

		final XLogWriterIncremental writer = new XLogWriterIncremental(tempFile);

		XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {
			public Object call(XTrace input) throws Exception {

				for (XEvent event : input) {
					String activity = classifier.getClassIdentity(event);
					event.getAttributes().put(XConceptExtension.KEY_NAME,
							new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, activity));
				}

				writer.writeTrace(input);

				return null;
			}
		});
		writer.close();

		return tempFile;
	}

	public void run(File logFile, XLog log, File modelFile, XEventClassifier classifier) throws Exception {
		File logFile2 = getTempLogFile(logFile, classifier);

		callSplitMiner(logFile2, modelFile);

		logFile2.delete();
	}

	public static void callSplitMiner(File logFile, File modelFile) throws InterruptedException, IOException {
		//Split miner automatically puts .bpmn after the file name
		File out = new File(modelFile.getParentFile().getAbsolutePath(),
				modelFile.getName().substring(0, modelFile.getName().length() - 5));

		if (1 == 2) {
			ProcessBuilder pb = new ProcessBuilder("java", "-jar",
					"C:\\Users\\sander\\workspace\\SanderLeemans\\src\\coopis2018\\splitminer.jar", "0.1", "0.4",
					logFile.getAbsolutePath(), out.getAbsolutePath());
			pb.inheritIO().start().waitFor();
		} else {
			ProcessBuilder pb = new ProcessBuilder("java", "-cp", "splitminer.jar;lib\\*",
					"au.edu.unimelb.services.ServiceProvider", "SMD", "0.1", "0.4", "false", logFile.getAbsolutePath(),
					out.getAbsolutePath());
			pb.directory(new File(
					"C:\\Users\\sander\\Documents\\svn\\22 - multi-level process mining\\experiments\\splitminer2.0"));
			pb.inheritIO().start().waitFor();
		}
	}
}
