package batch.stability;

import generation.GenerateTreeParameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.util.HTMLToString;

import batch.miners.MinerClass;

public class StabilityResult implements HTMLToString {
	private String format = "%5.3f";
	private double[][] averageLogSizes;
	private double[][] averageDfgCompletenesses;
	private double[][] averageEfgCompletenesses;
	private final List<String> trees;
	private final List<String> miners;
	private String disclaimer;

	public StabilityResult(StabilityParameters parameters, List<String> trees) {
		if (trees == null) {
			this.trees = new LinkedList<String>();
			for (GenerateTreeParameters p : parameters.getTreeSeeds()) {
				this.trees.add(p.toString());
			}
		} else {
			this.trees = trees;
		}

		miners = new LinkedList<String>();
		for (MinerClass p : parameters.getMinerClasses()) {
			miners.add(p.identification);
		}
		averageLogSizes = new double[miners.size()][this.trees.size()];
		averageDfgCompletenesses = new double[miners.size()][this.trees.size()];
		averageEfgCompletenesses = new double[miners.size()][this.trees.size()];

		disclaimer = "Log seeds from " + parameters.getStartLogSeed() + " to " + parameters.getEndLogSeed() + "<br>";
	}

	public synchronized void recordResult(StabilityParameters parameters, MinerClass minerClass, String treeId,
			double averageLogSize, double averageDfgCompleteness, double averageEfgCompleteness) {
		recordResult(treeId, minerClass.identification, averageLogSize, averageDfgCompleteness, averageEfgCompleteness,
				parameters.getResultsFile());
	}

	private synchronized void recordResult(String tree, String miner, double averageLogSize, double averageDfgCompleteness,
			double averageEfgCompleteness, File file) {
		averageLogSizes[miners.indexOf(miner)][trees.indexOf(tree)] = averageLogSize;
		averageDfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)] = averageDfgCompleteness;
		averageEfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)] = averageEfgCompleteness;
		writeToDisk(file);
	}

	private synchronized void writeToDisk(File file) {
		//write the results to disk
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);
			out.write(toHTMLString(true));
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

	public synchronized String toHTMLString(boolean includeHTMLTags) {

		StringBuilder r = new StringBuilder();
		r.append("Smallest log size, for which model was rediscovered, averaged over logs");
		r.append("<table><tr><td>seeds");
		for (String tree : trees) {
			r.append("<td>");
			r.append(tree);
		}
		for (String miner : miners) {
			r.append("<tr><td>");
			r.append(miner);
			for (String tree : trees) {
				r.append("<td>");
				r.append(String.format(format, averageLogSizes[miners.indexOf(miner)][trees.indexOf(tree)]));
			}
			r.append("<br>\n");
		}
		r.append("</table>");
		r.append("<br>");
		
		r.append("Smallest directly-follows completeness, for which model was rediscovered, averaged over logs");
		r.append("<table>");
		for (String miner : miners) {
			r.append("<tr><td>");
			r.append(miner);
			for (String tree : trees) {
				r.append("<td>");
				r.append(String.format(format, averageDfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)]));
			}
			r.append("<br>\n");
		}
		r.append("</table>");
		r.append("<br>");
		
		r.append("Smallest eventually-follows completeness, for which model was rediscovered, averaged over logs");
		r.append("<table>");
		for (String miner : miners) {
			r.append("<tr><td>");
			r.append(miner);
			for (String tree : trees) {
				r.append("<td>");
				r.append(String.format(format, averageEfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)]));
			}
			r.append("<br>\n");
		}
		r.append("</table>");
		r.append("<br>");
		
		r.append(disclaimer);
		return r.toString();
	}
}
