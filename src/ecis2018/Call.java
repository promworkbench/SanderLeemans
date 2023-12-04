package ecis2018;

import java.io.File;

/**
 * One run of a discovery algorithm
 * 
 * @author sander
 *
 */
public class Call {

	private final int runNr;
	private final int foldNr;
	private final File logFile;
	private final Algorithm algorithm;
	private final ExperimentParameters parameters;

	public Call(ExperimentParameters parameters, Algorithm algorithm, File logFile, int runNr, int foldNr) {
		this.parameters = parameters;
		this.algorithm = algorithm;
		this.logFile = logFile;
		this.runNr = runNr;
		this.foldNr = foldNr;
	}

	public String toString() {
		return logFile.getName() + ", " + getAlgorithm().getName() + ", r" + runNr + ", f" + foldNr;
	}

	/**
	 * 
	 * @return Name of the log file
	 */
	private String getName0() {
		return logFile.getName() + "-r" + runNr + "-f" + foldNr;
	}

	/**
	 * 
	 * @return Name of the model/measures file
	 */
	private String getName1() {
		return getName0() + "-" + getAlgorithm().getName();
	}

	public File getSplitLogDiscoveryFile() {
		return new File(parameters.getSplitLogDirectory(), getName0() + "-discovery.xes.gz");
	}

	public File getSplitLogMeasureFile() {
		return new File(parameters.getSplitLogDirectory(), getName0() + "-measure.xes.gz");
	}

	public File getModelFile() {
		return new File(parameters.getModelsDirectory(), getName1() + "-" + getAlgorithm().getFileExtension());
	}

	public File getTimeFile() {
		return new File(parameters.getMeasuresDirectory(), getName1() + "-time.txt");
	}

	public boolean isModelDone() {
		return getModelFile().exists();
	}

	public File getMeasureFile(Measure measure) {
		return new File(parameters.getMeasuresDirectory(), getName1() + "-" + measure.getTitle() + ".txt");
	}

	public boolean isMeasureDone(Measure measure) {
		return getMeasureFile(measure).exists();
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}
}
