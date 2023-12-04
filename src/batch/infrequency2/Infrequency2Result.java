package batch.infrequency2;

import generation.GenerateTreeParameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.processmining.framework.util.HTMLToString;

import batch.incompleteness.miners.MinerOption;

public class Infrequency2Result implements HTMLToString {

	private final MinerOption[] algorithms;
	private final GenerateTreeParameters[] treeParameterss;
	private final long[] deviationsSeeds;
	private final int[][][] result;

	public Infrequency2Result(MinerOption[] algorithms, GenerateTreeParameters[] treeParameterss,
			long[] deviationsSeeds, int[][][] result) {
		this.algorithms = algorithms;
		this.treeParameterss = treeParameterss;
		this.deviationsSeeds = deviationsSeeds;
		this.result = result;
		writeToDisk(new File("D:\\output\\infrequency2.csv"));
	}

	private synchronized void writeToDisk(File file) {
		//write the results to disk
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);
			out.write(toHTMLString(false));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String toHTMLString(boolean includeHTMLTags) {
		StringBuilder r = new StringBuilder();

		String newLine = "<br>";
		if (!includeHTMLTags) {
			newLine = "\n";
		}

		r.append("algorithm\t# algorithm\ttree\tdeviations seed\tmaximum number of deviations still rediscovered");
		r.append(newLine);
		for (int a = 0; a < algorithms.length; a++) {
			for (int t = 0; t < treeParameterss.length; t++) {
				for (int d = 0; d < deviationsSeeds.length; d++) {
					r.append(algorithms[a]);
					r.append("\t");
					r.append(a);
					r.append("\t");
					r.append(treeParameterss[t]);
					r.append("\t");
					r.append(deviationsSeeds[d]);
					r.append("\t");
					r.append(result[a][t][d]);
					r.append(newLine);
				}
			}
		}

		return r.toString();
	}
}
