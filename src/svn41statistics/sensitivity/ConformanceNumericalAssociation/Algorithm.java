package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.File;

import org.deckfour.xes.model.XLog;

public interface Algorithm {

	public String getName();

	public String getAbbreviation();

	public String getFileExtension();

	public String getLatexName();

	public void run(File logFile, XLog log, File modelFile) throws Exception;

}