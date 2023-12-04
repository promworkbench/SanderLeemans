package caise2020multilevel;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;

public interface Algorithm {

	public String getName();

	public String getAbbreviation();

	public String getFileExtension();

	public void run(File logFile, XLog log, File modelFile, XEventClassifier[] classifiers) throws Exception;

	public String getFlattenedFileExtension();

}
