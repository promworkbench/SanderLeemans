package svn45crimes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Call {

	private String logFileName;
	private ExperimentParameters parameters;
	private Algorithm algorithm;
	private Noise noise;
	private double noiseAmount;
	private int repetition;

	public Call(String logFileName, Algorithm algorithm, Noise noise, double noiseAmount, int repetition,
			ExperimentParameters parameters) {
		this.logFileName = logFileName;
		this.algorithm = algorithm;
		this.parameters = parameters;
		this.noise = noise;
		this.noiseAmount = noiseAmount;
		this.repetition = repetition;
	}

	public String toString() {
		return logFileName + "-" + algorithm.getName() + "-" + noise.getTitle() + "-n" + noiseAmount + "-r"
				+ repetition;
	}

	public int getSeed() {
		return algorithm.getName().hashCode() + noise.getTitle().hashCode() + Double.hashCode(noiseAmount) + repetition;
	}

	private String base() {
		return logFileName + "-" + noise.getTitle() + "-n" + noiseAmount + "-r" + repetition;
	}

	public File getLogFile() {
		return new File(parameters.getLogDirectory(), logFileName);
	}

	public File getNoiseLogFile() {
		return new File(parameters.getNoiseLogsDirectory(), base() + ".xes.gz");
	}

	public File getDiscoveredModelFileOld() {
		return new File(new File(parameters.getDiscoveredModelsDirectory(), algorithm.getAbbreviation()),
				base() + algorithm.getFileExtension());
	}

	public File getDiscoveredModelFile() {
		return new File(new File(parameters.getDiscoveredModelsDirectory(),
				algorithm.getAbbreviation() + "-" + noise.getTitle()), base() + algorithm.getFileExtension());
	}

	public File getDiscoveredModelTimeFileOld() {
		return new File(new File(parameters.getDiscoveredModelsDirectory(), algorithm.getAbbreviation()),
				base() + ".time");
	}

	public File getDiscoveredModelTimeFile() {
		return new File(new File(parameters.getDiscoveredModelsDirectory(),
				algorithm.getAbbreviation() + "-" + noise.getTitle()), base() + ".time");
	}

	public File getMeasureFile(Measure measure) {
		return new File(
				new File(parameters.getMeasuresDirectory(),
						algorithm.getAbbreviation() + "-" + measure.getTitle() + "-" + noise.getTitle()),
				base() + ".txt");
	}

	public File getMeasureFileOld(Measure measure) {
		return new File(parameters.getMeasuresDirectory(),
				base() + "-" + algorithm.getAbbreviation() + "-" + measure.getTitle() + ".txt");
	}

	public File getMeasureTimeFile(Measure measure) {
		return new File(
				new File(parameters.getMeasuresDirectory(),
						algorithm.getAbbreviation() + "-" + measure.getTitle() + "-" + noise.getTitle()),
				base() + ".time");
	}

	public File getMeasureTimeFileOld(Measure measure) {
		return new File(
				new File(parameters.getMeasuresDirectory(), algorithm.getAbbreviation() + "-" + measure.getTitle()),
				base() + ".time");
	}

	public boolean isMeasureDone(Measure measure) {
		return getMeasureFile(measure).exists();
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

	public double getNoiseAmount() {
		return noiseAmount;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public Noise getNoise() {
		return noise;
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

	public boolean isReducedLog() {
		return logFileName.startsWith("BPIC15_1.xes.gz") || logFileName.startsWith("BPIC15_2.xes.gz")
				|| logFileName.startsWith("BPIC15_3.xes.gz") || logFileName.startsWith("BPIC15_4.xes.gz")
				|| logFileName.startsWith("BPIC15_5.xes.gz") || logFileName.startsWith("BPI Challenge 2018.xes.gz")
				|| logFileName.startsWith("Hospital_log.xes.gz");
	}
}
