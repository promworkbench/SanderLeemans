package svn41statistics.processprocesslogtest;

import java.io.File;

import org.deckfour.xes.model.XLog;

public interface Algorithm {

	public String getName();

	public String getAbbreviation();
	
	public String getLatexName();
	
	public String getFileExtension();

	public void run(File logFile, XLog log, File modelFile) throws Exception;

}
