package svn53longdistancedependenciesresample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Call {

	private File logFile;
	private ExperimentParameters parameters;
	private Algorithm algorithm;
	private StochasticAlgorithm stochasticAlgorithm;
	private int repetition;
	private Measure measure;

	public Call(File logFile, Algorithm algorithm, StochasticAlgorithm stochasticAlgorithm, int repetition,
			Measure measure, ExperimentParameters parameters) {
		this.logFile = logFile;
		this.algorithm = algorithm;
		this.stochasticAlgorithm = stochasticAlgorithm;
		this.measure = measure;
		this.parameters = parameters;
		this.repetition = repetition;
	}

	public File getLogFile() {
		return logFile;
	}

	public File getSplitLogFile() {
		return new File(parameters.getSplitLogsDirectory(), logFile.getName() + repetition + ".xes.gz");
	}

	public File getSplitLogTestFile() {
		return new File(parameters.getSplitLogsTestDirectory(), logFile.getName() + repetition + ".xes.gz");
	}

	public File getDiscoveredModelFile() {
		return new File(parameters.getDiscoveredModelsDirectory(),
				logFile.getName() + "-" + algorithm.getAbbreviation() + ".apnml");
	}

	public File getDiscoveredStochasticModelFile() {
		return new File(parameters.getDiscoveredStochasticModelsDirectory(),
				logFile.getName() + "-" + algorithm.getAbbreviation() + "-" + stochasticAlgorithm.getAbbreviation()
						+ "-" + repetition + stochasticAlgorithm.getFileExtension());
	}

	public File getDiscoveredStochasticModelTimeFile() {
		return new File(parameters.getDiscoveredStochasticModelsDirectory(),
				logFile.getName() + "-" + algorithm.getAbbreviation() + "-" + stochasticAlgorithm.getAbbreviation()
						+ "-" + repetition + ".time");
	}

	public File getMeasureFile() {
		return new File(
				new File(parameters.getMeasuresDirectory(), logFile.getName() + "-" + algorithm.getAbbreviation()),
				stochasticAlgorithm.getAbbreviation() + "-" + repetition + "-" + measure.getTitle() + ".txt");
	}

	public File getMeasureFileOld() {
		return new File(parameters.getMeasuresDirectory(), logFile.getName() + "-" + algorithm.getAbbreviation() + "-"
				+ stochasticAlgorithm.getAbbreviation() + "-" + repetition + "-" + measure.getTitle() + ".txt");
	}

	public File getMeasureTimeFile() {
		return new File(
				new File(parameters.getMeasuresDirectory(), logFile.getName() + "-" + algorithm.getAbbreviation()),
				stochasticAlgorithm.getAbbreviation() + "-" + repetition + "-" + measure.getTitle() + ".time");
	}

	public File getMeasureTimeFileOld() {
		return new File(parameters.getMeasuresDirectory(), logFile.getName() + "-" + algorithm.getAbbreviation() + "-"
				+ stochasticAlgorithm.getAbbreviation() + "-" + repetition + "-" + measure.getTitle() + ".time");
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

	public long getSeed() {
		return logFile.getName().hashCode() * repetition;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public StochasticAlgorithm getStochasticAlgorithm() {
		return stochasticAlgorithm;
	}

	public Measure getMeasure() {
		return measure;
	}
}