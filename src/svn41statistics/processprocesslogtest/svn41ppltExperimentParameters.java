package svn41statistics.processprocesslogtest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.SystemUtils;

public class svn41ppltExperimentParameters {

	public static final File baseDirectory = SystemUtils.IS_OS_LINUX ? new File(
			"/home/sander/Documents/svn/41 - stochastic statistics/experiments/10 - process process log test sensitivity")
			: new File(
					"C:\\Users\\leemans2\\Documents\\svn\\41 - stochastic statistics\\experiments\\10 - process process log test sensitivity");

	private List<Algorithm> algorithms = new ArrayList<>();

	private int sampleSizeMin = 100;
	private int sampleSizeMax = 1000;
	private int sampleSizeStep = 100;

	private int numberOfSamplesMin = 100;
	private int numberOfSamplesMax = 1000;
	private int numberOfSamplesStep = 100;

	public svn41ppltExperimentParameters() {
		algorithms.add(new AlgorithmWeightFrequency50());
		algorithms.add(new AlgorithmWeightFrequency60());
	}

	public List<svn41ppltCall> getCalls() {
		List<svn41ppltCall> result = new ArrayList<>();

		File[] files = getLogDirectory().listFiles();
		for (File logFile : files) {
			if (logFile.getName().endsWith(".xes.gz")) {
				for (int numberOfSamples : getNumbersOfSamples()) {
					for (int sampleSize : getSampleSizes()) {
						result.add(new svn41ppltCall(logFile.getName(), numberOfSamples, sampleSize, this));
					}
				}
			} else {
				System.out.println("log file " + logFile.getName()
						+ " skipped as it's not a .xes.gz file or no attribute is defined for it");
			}
		}

		return result;
	}

	public List<Integer> getSampleSizes() {
		List<Integer> result = new ArrayList<>();
		for (int sampleSize = sampleSizeMin; sampleSize <= sampleSizeMax; sampleSize += sampleSizeStep) {
			result.add(sampleSize);
		}
		return result;
	}

	public List<Integer> getNumbersOfSamples() {
		List<Integer> result = new ArrayList<>();
		for (int numberOfSamples = numberOfSamplesMin; numberOfSamples <= numberOfSamplesMax; numberOfSamples += numberOfSamplesStep) {
			result.add(numberOfSamples);
		}
		return result;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory.getParentFile(), "00 - base logs");
	}

	public File getDiscoveredModelsDirectory() {
		return new File(baseDirectory, "1-discoveredmodels");
	}

	public File getTestsDirectory() {
		return new File(baseDirectory, "2-tests");
	}

	public File getResultsDirectory() {
		return new File(baseDirectory, "3-results");
	}

	public List<Algorithm> getAlgorithms() {
		return Collections.unmodifiableList(algorithms);
	}

}