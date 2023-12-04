package generation;

import java.util.LinkedList;
import java.util.List;

import org.processmining.plugins.InductiveMiner.Pair;

public class EntropyResult {
	public long dfgEdgesInModel;
	public long efgEdgesInModel;
	public long msdEdgesInModel;
	public List<Pair<String, String>> missingDfgEdges;
	public List<Pair<String, String>> missingEfgEdges;
	public List<Pair<String, String>> missingMsdEdges;

	public EntropyResult() {
		this.dfgEdgesInModel = 0;
		this.efgEdgesInModel = 0;
		this.msdEdgesInModel = 0;
		missingDfgEdges = new LinkedList<Pair<String, String>>();
		missingEfgEdges = new LinkedList<Pair<String, String>>();
		missingMsdEdges = new LinkedList<Pair<String, String>>();
	}

	public double getDfgEntropy() {
		return 1 - missingDfgEdges.size() / (dfgEdgesInModel * 1.0);
	}

	public double getEfgEntropy() {
		return 1 - missingEfgEdges.size() / (efgEdgesInModel * 1.0);
	}

	public double getMsdEntropy() {
		return 1 - missingMsdEdges.size() / (msdEdgesInModel * 1.0);
	}

	public String toString() {
		return toString(false, false);
	}

	public String toString(boolean msd, boolean useHTML) {
		String newLine;
		if (useHTML) {
			newLine = "<br>\n";
		} else {
			newLine = "\n";
		}

		StringBuilder result = new StringBuilder();

		{

			result.append("Directly-follows completeness " + getDfgEntropy() + ", missing edges:" + newLine);
			for (Pair<String, String> e : missingDfgEdges) {
				result.append(e.getLeft() + " -> " + e.getRight() + newLine);
			}

		}

		{
			result.append("Eventually-follows completeness " + getEfgEntropy() + ", missing edges:" + newLine);
			for (Pair<String, String> e : missingEfgEdges) {
				result.append(e.getLeft() + " -> " + e.getRight() + newLine);
			}
		}

		if (msd) {
			if (missingMsdEdges.size() == 0) {
				result.append("Log is minimum self-distance complete to model." + newLine);
			} else {
				result.append("Log is not minimum self-distance complete to model. Missing edges: (" + getMsdEntropy()
						+ ")" + newLine);
				for (Pair<String, String> e : missingMsdEdges) {
					result.append(e.getLeft() + " -> " + e.getRight() + newLine);
				}
			}
		}

		return result.toString();
	}
}
