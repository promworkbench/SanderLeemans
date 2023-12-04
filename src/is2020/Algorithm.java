package is2020;

import java.io.File;

import org.deckfour.xes.model.XLog;

public interface Algorithm {

	public String getName();

	public String getAbbreviation();

	public void run(File logFile, XLog log, File modelFile) throws Exception;

}
