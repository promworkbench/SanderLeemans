package svn27emscpartialorders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Call {

	private String logFileName;
	private ExperimentParameters parameters;
	private Algorithm algorithm;

	public Call(String logFileName, Algorithm algorithm, ExperimentParameters parameters) {
		this.logFileName = logFileName;
		this.algorithm = algorithm;
		this.parameters = parameters;
	}

	public File getDiscoveredModelFile() {
		return new File(parameters.getDiscoveredModelsDirectory(),
				logFileName + "-" + algorithm.getAbbreviation() + ".pnml");
	}

	public File getDiscoveredModelTimeFile() {
		return new File(parameters.getDiscoveredModelsDirectory(),
				logFileName + "-" + algorithm.getAbbreviation() + ".time");
	}

	public File getLogMeasuresFile() {
		return new File(parameters.getLogPerformanceDirectory(), logFileName + ".txt");
	}

	public File getMeasureFile(Measure measure) {
		return new File(parameters.getMeasuresDirectory(),
				logFileName + "-" + algorithm.getAbbreviation() + "-" + measure.getTitle() + ".txt");
	}

	public File getMeasureTimeFile(Measure measure) {
		return new File(parameters.getMeasuresDirectory(),
				logFileName + "-" + algorithm.getAbbreviation() + "-" + measure.getTitle() + ".time");
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
}
