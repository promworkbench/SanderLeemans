package svn51traceprobability;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;

public interface StochasticAlgorithm {

	public String getName();

	public String getAbbreviation();

	public String getLatexName();

	public String getFileExtension();

	public void run(File logFile, XLog log, AcceptingPetriNet net, File modelFile) throws Exception;

}