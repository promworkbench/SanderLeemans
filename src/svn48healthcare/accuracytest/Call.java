package svn48healthcare.accuracytest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Call {

	private String logFileName;
	private ExperimentParameters parameters;
	private int bin;
	private int repetition;

	public Call(String logFileName, int repetition, int bin, ExperimentParameters parameters) {
		this.logFileName = logFileName;
		this.parameters = parameters;
		this.repetition = repetition;
		this.bin = bin;
	}

	public String toString() {
		return logFileName + "-b" + bin + "-r" + repetition;
	}

	public int getSeed() {
		return repetition;
	}

	private String base() {
		return logFileName + "-b" + bin + "-r" + repetition;
	}

	public File getLogFile() {
		return new File(parameters.getLogDirectory(), logFileName);
	}

	public File getLogSplitFile() {
		return new File(parameters.getSplitLogsDirectory(), base() + ".xes.gz");
	}

	public File getLogSplitFileTest() {
		return new File(parameters.getSplitLogsTestDirectory(), base() + ".xes.gz");
	}

	public File getDiscoveredModelFile() {
		return new File(parameters.getDiscoveredModelsDirectory(), base() + ".cmodel");
	}

	public File getDiscoveredModelTimeFile() {
		return new File(parameters.getDiscoveredModelsDirectory(), base() + ".time");
	}

	public File getMeasureFile() {
		return new File(parameters.getMeasuresDirectory(), base() + ".txt");
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

	public int getBin() {
		return bin;
	}

	public boolean isMeasureDone() {
		return getMeasureFile().exists();
	}

}
