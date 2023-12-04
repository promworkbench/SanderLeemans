package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;

public abstract class AlgorithmFlatAbstract implements Algorithm {

	public void run(File logFile, XLog log, File modelFile, XEventClassifier[] classifiers)
			throws Exception {
		XEventAndClassifier classifier = new XEventAndClassifier(classifiers);
		run(logFile, log, modelFile, classifier);
	}

	public abstract void run(File logFile, XLog log, File modelFile, XEventClassifier classifier) throws Exception;

}