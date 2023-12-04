package bpm2020cohortanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Call {

	private String logFileName;
	private ExperimentParameters parameters;
	private int featureSetSize;

	public Call(String logFileName, int featureSetSize, ExperimentParameters parameters) {
		this.logFileName = logFileName;
		this.parameters = parameters;
		this.featureSetSize = featureSetSize;
	}

	public File getLogMeasuresFile() {
		return new File(parameters.getLogMeasuresDirectory(), logFileName + ".txt");
	}

	public File getFeatureSetsFile() {
		return new File(parameters.getFeatureSetsDirectory(), logFileName + "-" + featureSetSize + ".txt");
	}

	public File getCohortsFile() {
		return new File(parameters.getCohortsDirectory(), logFileName + "-" + featureSetSize + ".txt");
	}

	public File getCohortsTimeFile() {
		return new File(parameters.getCohortsDirectory(), logFileName + "-" + featureSetSize + ".time");
	}

	public String toString() {
		return logFileName + ", features " + featureSetSize;
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
}
