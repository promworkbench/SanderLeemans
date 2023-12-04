package svn48healthcare.accuracytest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SystemUtils;

public class ExperimentParameters {

	public static final File baseDirectory = SystemUtils.IS_OS_LINUX ? new File(
			"/home/sander/Documents/svn/48 - stochastic process mining in healthcare - Andrew Partington/experiments")
			: new File("C:\\experiment\\example experiment");

	private int bins = 5;
	private int repetitions = 10;

	public ExperimentParameters() {

	}

	public List<Call> getCalls() {
		List<Call> result = new ArrayList<>();

		File[] files = getLogDirectory().listFiles();
		for (File logFile : files) {
			if (logFile.getName().endsWith(".xes.gz")) {
				for (int bin = 0; bin < bins; bin++) {
					for (int repetition = 0; repetition < repetitions; repetition++) {
						result.add(new Call(logFile.getName(), repetition, bin, this));
					}
				}
			} else {
				System.out.println("log file " + logFile.getName() + " skipped as it's not a .xes.gz file");
			}
		}

		return result;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory, "0 - input logs");
	}

	public File getSplitLogsDirectory() {
		return new File(baseDirectory, "1 - split logs");
	}

	public File getSplitLogsTestDirectory() {
		return new File(baseDirectory, "2 - split logs test");
	}

	public File getDiscoveredModelsDirectory() {
		return new File(baseDirectory, "3 - discovered cost models");
	}

	public File getMeasuresDirectory() {
		return new File(baseDirectory, "4 - measures");
	}

	public File getResultsDirectory() {
		return new File(baseDirectory, "5 - results");
	}

	public File getTestsResultsFile(File logFile) {
		return new File(getResultsDirectory(), logFile.getName() + "-results.txt");
	}

	public int getBins() {
		return bins;
	}

}