package batch.incompleteness;

import generation.GenerateTreeParameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.util.HTMLToString;

import batch.incompleteness.miners.MinerOption;

public class BatchRediscoverabilityResult implements HTMLToString {
	private String format = "%5.3f";
	private int[][] rediscovereds;
	private double[][] averageLogSizes;
	private double[][] averageDfgCompletenesses;
	private double[][] averageEfgCompletenesses;
	private double[][] averageMiningTimes;
	private final List<String> trees;
	private final List<String> miners;
	private String disclaimer;
	private String title;

	public BatchRediscoverabilityResult(IncompletenessParameters parameters, List<String> trees, String title) {
		if (trees == null) {
			this.trees = new LinkedList<String>();
			for (GenerateTreeParameters p : parameters.getTreeSeeds()) {
				this.trees.add(p.toString());
			}
		} else {
			this.trees = trees;
		}

		miners = new LinkedList<String>();
		for (MinerOption p : parameters.getMinerOptions()) {
			miners.add(p.toString());
		}
		rediscovereds = new int[miners.size()][this.trees.size()];
		averageLogSizes = new double[miners.size()][this.trees.size()];
		averageDfgCompletenesses = new double[miners.size()][this.trees.size()];
		averageEfgCompletenesses = new double[miners.size()][this.trees.size()];
		averageMiningTimes = new double[miners.size()][this.trees.size()];

		disclaimer = "Log seeds from " + parameters.getStartLogSeed() + " to " + parameters.getEndLogSeed() + "<br>";
		this.title = title;
	}

	public synchronized void recordResult(IncompletenessParameters parameters, MinerOption minerOption, String treeId,
			double averageLogSize, double averageDfgCompleteness, double averageEfgCompleteness, int rediscovered,
			double averageMiningTime) {
		recordResult(treeId, minerOption.toString(), averageLogSize, averageDfgCompleteness, averageEfgCompleteness,
				rediscovered, averageMiningTime, parameters.getResultsFile());
	}

	private synchronized void recordResult(String tree, String miner, double averageLogSize,
			double averageDfgCompleteness, double averageEfgCompleteness, int rediscovered, double averageMiningTime,
			File file) {
		rediscovereds[miners.indexOf(miner)][trees.indexOf(tree)] = rediscovered;
		averageLogSizes[miners.indexOf(miner)][trees.indexOf(tree)] = averageLogSize;
		averageDfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)] = averageDfgCompleteness;
		averageEfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)] = averageEfgCompleteness;
		averageMiningTimes[miners.indexOf(miner)][trees.indexOf(tree)] = averageMiningTime;
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
		r.append(title);
		r.append(" ");
		r.append("Number of logs rediscovered");
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
				r.append(rediscovereds[miners.indexOf(miner)][trees.indexOf(tree)]);
			}
			r.append("<br>\n");
		}

		r.append("<tr><td colspan=100>Smallest log size, for which model was rediscovered, averaged over logs</td></tr>");
		for (String miner : miners) {
			r.append("<tr><td>");
			r.append(miner);
			for (String tree : trees) {
				r.append("<td>");
				r.append(String.format(format, averageLogSizes[miners.indexOf(miner)][trees.indexOf(tree)]));
			}
			r.append("<br>\n");
		}

		r.append("<tr><td colspan=100>Smallest directly-follows completeness, for which model was rediscovered, averaged over logs</td></tr>");
		for (String miner : miners) {
			r.append("<tr><td>");
			r.append(miner);
			for (String tree : trees) {
				r.append("<td>");
				r.append(String.format(format, averageDfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)]));
			}
			r.append("<br>\n");
		}
		
		r.append("<tr><td colspan=100>Mining time (s), for total binary search, averaged over logs</td></tr>");
		for (String miner : miners) {
			r.append("<tr><td>");
			r.append(miner);
			for (String tree : trees) {
				r.append("<td>");
				r.append(String.format(format, averageMiningTimes[miners.indexOf(miner)][trees.indexOf(tree)]));
			}
			r.append("<br>\n");
		}

		//leave the eventually-follows for now
		{
			//			r.append("<tr><td colspan=100>Smallest eventually-follows completeness, for which model was rediscovered, averaged over logs</td></tr>");
			//			for (String miner : miners) {
			//				r.append("<tr><td>");
			//				r.append(miner);
			//				for (String tree : trees) {
			//					r.append("<td>");
			//					r.append(String.format(format, averageEfgCompletenesses[miners.indexOf(miner)][trees.indexOf(tree)]));
			//				}
			//				r.append("<br>\n");
			//			}
		}
		r.append("</table>");
		r.append("<br>");

		r.append(disclaimer);
		return r.toString();
	}
}
