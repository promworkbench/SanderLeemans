package svn48healthcare.synthetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

public class Call {
	private final int repetition;
	private final Distribution distribution;
	private final ExperimentParameters parameters;
	private final EfficientTree tree;

	public Call(Distribution distribution, int repetition, EfficientTree tree, ExperimentParameters parameters) {
		this.distribution = distribution;
		this.repetition = repetition;
		this.tree = tree;
		this.parameters = parameters;
	}

	public String toString() {
		return repetition + "-" + distribution.getAbbreviation();
	}

	public int getRepetition() {
		return repetition;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public EfficientTree getTree() {
		return tree;
	}

	public long getSeed() {
		return repetition * 1500000 + distribution.hashCode();
	}

	public ExperimentParameters getParameters() {
		return parameters;
	}

	private String base() {
		return repetition + "-" + distribution.getAbbreviation() + "-" + tree.toString();
	}

	public File getLogFile() {
		return new File(parameters.getLogsDirectory(), base() + ".xes.gz");
	}
	
	public File getTestLogFile() {
		return new File(parameters.getTestLogsDirectory(), base() + ".xes.gz");
	}

	public File getDistributionsFile() {
		return new File(parameters.getDistributionsDirectory(), base() + ".dists");
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

	public boolean isMeasureDone() {
		return getMeasureFile().exists();
	}
}