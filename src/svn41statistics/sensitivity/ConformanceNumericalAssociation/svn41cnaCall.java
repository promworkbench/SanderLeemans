package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class svn41cnaCall {

	private String logFileName;
	private svn41cnaExperimentParameters parameters;
	private Algorithm algorithm;
	private int numberOfSamples;
	private Attribute attribute;

	public svn41cnaCall(String logFileName, Algorithm algorithm, int numberOfSamples,
			svn41cnaExperimentParameters parameters, Attribute attribute) {
		this.logFileName = logFileName;
		this.algorithm = algorithm;
		this.numberOfSamples = numberOfSamples;
		this.parameters = parameters;
		this.attribute = attribute;
	}

	public String toString() {
		return logFileName + "-" + algorithm.getName() + "-" + numberOfSamples;
	}

	public int getSeed() {
		return algorithm.getName().hashCode();
	}

	private String base() {
		return logFileName + "-" + algorithm.getName();
	}

	public File getLogFile() {
		return new File(parameters.getLogDirectory(), logFileName);
	}

	public File getDiscoveredModelFile() {
		return new File(parameters.getDiscoveredModelsDirectory(), base() + algorithm.getFileExtension());
	}

	public File getAssociationFile() {
		return new File(parameters.getAssociationDirectory(), base() + "-" + numberOfSamples + ".txt");
	}

	public File getBaselineFile() {
		return new File(parameters.getBaselineDirectory(), base() + ".txt");
	}

	public File getAssociationPlotFile() {
		return new File(parameters.getAssociationDirectory(), base() + "-" + numberOfSamples + ".png");
	}

	public File getAssociationTimeFile() {
		return new File(parameters.getAssociationDirectory(), base() + "-" + numberOfSamples + ".time");
	}

	public File getBaselineTimeFile() {
		return new File(parameters.getBaselineDirectory(), base() + "-samsen.time");

	}

	public File getResultsFile() {
		return new File(parameters.getResultsDirectory(), "cna-samsen-" + base() + ".csv");
	}

	public boolean isAssociationDone() {
		return getAssociationFile().exists();
	}

	public static boolean isAttempted(File file) {
		return file.exists();
	}

	public static boolean isDone(File file) throws IOException {
		if (file.exists()) {
			return !isError(file);
		} else {
			return false;
		}
	}

	public static boolean isError(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		String firstLine = r.readLine();
		r.close();
		return firstLine.startsWith("error");
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public static void setError(File file, Throwable e) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		writer.write("error\n");
		if (e != null) {
			e.printStackTrace(writer);
		}
		writer.flush();
		writer.close();
	}

}