package svn41statistics.logcategoricaltest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class svn41lctCall {

	private String logFileName;
	private svn41lctExperimentParameters parameters;
	private int numberOfSamples;
	private int sampleSize;
	private Attribute attribute;

	public svn41lctCall(String logFileName, int numberOfSamples, int sampleSize,
			svn41lctExperimentParameters parameters, Attribute attribute) {
		this.logFileName = logFileName;
		this.sampleSize = sampleSize;
		this.numberOfSamples = numberOfSamples;
		this.parameters = parameters;
		this.attribute = attribute;
	}

	public String toString() {
		return logFileName + "-ns" + numberOfSamples + "ss" + sampleSize;
	}

	private String base() {
		return logFileName + "-ns" + numberOfSamples + "ss" + sampleSize;
	}

	public File getLogFile() {
		return new File(parameters.getLogDirectory(), logFileName);
	}

	public File getAssociationFile() {
		return new File(parameters.getAssociationDirectory(), base() + ".txt");
	}

	public File getAssociationTimeFile() {
		return new File(parameters.getAssociationDirectory(), base() + ".time");
	}

	public File getResultsFile() {
		return new File(parameters.getResultsDirectory(), "lct-samsen-" + base() + ".csv");
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

	public int getSampleSize() {
		return sampleSize;
	}

}