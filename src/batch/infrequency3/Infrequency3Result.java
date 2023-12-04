package batch.infrequency3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.processmining.framework.util.HTMLToString;

import batch.incompleteness.miners.MinerOption;

public class Infrequency3Result implements HTMLToString {
	private final MinerOption[] algorithms;
	private final int[] deviations;
	private final int[] numberOfDeviations;
	private final double[][] fitness;
	private final double[][] precision;
	private final double[][] generalisation;
	
	private static String format = "%5.2f";

	public Infrequency3Result(MinerOption[] algorithms, int[] deviations) {
		this.algorithms = algorithms;
		this.deviations = deviations;
		this.numberOfDeviations = new int[deviations.length];
		this.fitness = new double[algorithms.length][deviations.length];
		this.precision = new double[algorithms.length][deviations.length];
		this.generalisation = new double[algorithms.length][deviations.length];
	}

	public synchronized void writeToDisk(File file) {
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

//		String newLine = "<br>";
//		if (!includeHTMLTags) {
//			newLine = "\\\\\n";
//		}
		String newLine = "\\\\\n";

		//header
		r.append("algorithm\t");
		for (int d = 0; d < deviations.length; d++) {
			r.append(deviations[d] + "\t\t\t");
		}
		r.append(newLine);
		
		//deviations
		r.append("deviations\t");
		for (int d = 0; d < deviations.length; d++) {
			r.append("&");
			r.append(numberOfDeviations[d]);
		}
		r.append(newLine);

		//body
		for (int a = 0; a < algorithms.length; a++) {
			r.append(algorithms[a].toLatex());
			r.append("\t");
			for (int d = 0; d < deviations.length; d++) {
				r.append("&");
				r.append("$f ");
				r.append(String.format(format, fitness[a][d]));
				r.append("$ \t& $");
				r.append("p ");
				r.append(String.format(format, precision[a][d]));
				r.append("$ \t& $");
				r.append("g ");
				r.append(String.format(format, generalisation[a][d]));
				r.append("$\t");
			}
			r.append(newLine);
		}

		return r.toString();
	}

	public void setFitness(int a, int d, double f) {
		fitness[a][d] = f;
		writeToDisk(new File("d://output//infrequency3.txt"));
	}
	
	public void setPrecision(int a, int d, double f) {
		precision[a][d] = f;
	}
	
	public void setGeneralisation(int a, int d, double f) {
		generalisation[a][d] = f;
		writeToDisk(new File("d://output//infrequency3.txt"));
	}

	public void setNumberOfDeviations(int d, int b) {
		numberOfDeviations[d] = b;
	}
}
