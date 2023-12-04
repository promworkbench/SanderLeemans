package is2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Call {

	private final File logFile;
	private final ExperimentParameters parameters;
	private final Algorithm algorithm;
	private final int unfolding;

	public Call(File logFile, Algorithm algorithm, ExperimentParameters parameters, int unfolding) {
		this.logFile = logFile;
		this.algorithm = algorithm;
		this.parameters = parameters;
		this.unfolding = unfolding;
	}

	public String toString() {
		return logFile.getName() + ", " + algorithm.getName() + ", u" + unfolding;
	}

	private String getName0() {
		return logFile.getName() + "-" + algorithm.getAbbreviation();
	}

	private String getName1() {
		return getName0() + "-u" + unfolding;
	}

	public File getDiscoveredModelFile() {
		return new File(parameters.getDiscoveredModelsDirectory(), getName0() + ".pnml");
	}

	public File getDiscoveredModelTimeFile() {
		return new File(parameters.getDiscoveredModelsDirectory() + ".time");
	}

	public File getLogMeasuresFile() {
		return new File(parameters.getMeasureDirectory(), getName1() + ".txt");
	}

	public File getLogMeasuresTimeFile() {
		return new File(parameters.getMeasureDirectory(), getName1() + ".time");
	}

	public File getLanguageSizeFile() {
		return new File(parameters.getLanguageSizesDirectory(), getName1() + ".txt");
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

	public int getUnfolding() {
		return unfolding;
	}
}