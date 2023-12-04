package svn41statistics.processprocesslogtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class svn41ppltCall {

	private String logFileName;
	private svn41ppltExperimentParameters parameters;
	private int numberOfSamples;
	private int sampleSize;

	public svn41ppltCall(String logFileName, int numberOfSamples, int sampleSize,
			svn41ppltExperimentParameters parameters) {
		this.logFileName = logFileName;
		this.numberOfSamples = numberOfSamples;
		this.sampleSize = sampleSize;
		this.parameters = parameters;
	}

	public String toString() {
		return logFileName + "-ns" + numberOfSamples + "ss" + sampleSize;
	}

	public int getSeed() {
		return 1;
	}

	private String base() {
		return logFileName + "-ns" + numberOfSamples + "ss" + sampleSize;
	}

	public File getLogFile() {
		return new File(parameters.getLogDirectory(), logFileName);
	}

	public File getDiscoveredModelFile(Algorithm algorithm) {
		int numberOfSamples = this.numberOfSamples;
		int sampleSize = this.sampleSize;
		this.numberOfSamples = 0;
		this.sampleSize = 0;
		File result = new File(parameters.getDiscoveredModelsDirectory(),
				base() + "-" + algorithm.getAbbreviation() + algorithm.getFileExtension());
		this.numberOfSamples = numberOfSamples;
		this.sampleSize = sampleSize;
		return result;
	}

	public File getTestFile() {
		return new File(parameters.getTestsDirectory(), base() + "-" + numberOfSamples + ".txt");
	}

	public File getTestTimeFile() {
		return new File(parameters.getTestsDirectory(), base() + "-" + numberOfSamples + ".time");
	}

	public File getResultsFile() {
		return new File(parameters.getResultsDirectory(), "pplt-samsen-" + base() + ".csv");
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