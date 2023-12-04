package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.processmining.plugins.InductiveMiner.Function;

import com.raffaeleconforti.efficientlog.XTraceImpl;

import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class Perform0LogTransformation {
	public static void main(String... args) throws Exception {
		final ExperimentParameters parameters = new ExperimentParameters();
		parameters.getTransformedLogDirectory().mkdirs();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			Call call = new Call(parameters, new AlgorithmFlower(), logFile, 0, 0);
			if (!(call.getTransformedLogFile().exists())) {

				final XLogWriterIncremental transformedLogWriter = new XLogWriterIncremental(
						call.getTransformedLogFile());

				final XEventClassifier classifier = new XEventAndClassifier(parameters.getClassifiers(logFile));

				XLogParserIncremental.parseTraces(logFile, new Function<XTrace, Object>() {
					public Object call(XTrace trace) throws Exception {

						XTrace newTrace = new XTraceImpl(trace.getAttributes());
						for (XEvent event : trace) {
							XEvent newEvent = new XEventImpl();
							String activity = classifier.getClassIdentity(event);
							newEvent.getAttributes().put(XConceptExtension.KEY_NAME,
									new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, activity));

							newTrace.add(newEvent);
						}

						transformedLogWriter.writeTrace(newTrace);

						return null;
					}
				});

				transformedLogWriter.close();
			}
		}

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			Call call = new Call(parameters, new AlgorithmFlower(), logFile, 0, 0);
			if (call.getTransformedLogFile().exists()) {
				File modelFile = new File(call.getTransformedLogFile().getAbsolutePath(), ".bpmn");

				AlgorithmSplitMiner.callSplitMiner(call.getTransformedLogFile(), modelFile);
			}
		}
	}
}