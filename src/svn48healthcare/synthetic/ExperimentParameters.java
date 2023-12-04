package svn48healthcare.synthetic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;

public class ExperimentParameters {
	public static final File baseDirectory = new File(
			"/home/sander/Documents/svn/48 - stochastic process mining in healthcare - Andrew Partington/04 - experiments synthetic");

	private final int repetitions = 10;
	private final int numberOfTraces = 1000;
	private List<EfficientTree> trees = new ArrayList<>();
	private List<Distribution> distributions = new ArrayList<>();

	public ExperimentParameters() {
		distributions.add(new DistributionInstanceConstant());
		distributions.add(new DistributionInstanceNormal());
		distributions.add(new DistributionInstanceTriangular());
		distributions.add(new DistributionInstanceExponential());
		distributions.add(new DistributionInstanceWeibull());
		distributions.add(new DistributionInstanceWeibull2());
		distributions.add(new DistributionInstanceGamma());
		distributions.add(new DistributionInstanceLogNormal());

		trees.add(InlineTree.leaf("a"));
		trees.add(InlineTree.loop(InlineTree.xor(InlineTree.leaf("a"), InlineTree.leaf("b")), InlineTree.leaf("c"),
				InlineTree.tau()));
		trees.add(InlineTree.concurrent(InlineTree.leaf("a"), InlineTree.leaf("b")));
		trees.add(InlineTree.loop(InlineTree.xor(InlineTree.leaf("a"), InlineTree.leaf("b")), InlineTree.leaf("c"),
				InlineTree.xor(InlineTree.leaf("d"), InlineTree.leaf("e"))));
		trees.add(InlineTree.concurrent(InlineTree.seq(InlineTree.leaf("a"), InlineTree.leaf("b"), InlineTree.loop(
				InlineTree.xor(InlineTree.leaf("c"), InlineTree.leaf("d")), InlineTree.leaf("e"), InlineTree.tau()))));
		trees.add(InlineTree.seq(InlineTree.loop(InlineTree.leaf("a"), InlineTree.leaf("b"), InlineTree.tau()),
				InlineTree.loop(InlineTree.leaf("c"), InlineTree.leaf("d"), InlineTree.tau())));
		trees.add(InlineTree.xor(InlineTree.leaf("a"), InlineTree.leaf("b")));
		trees.add(InlineTree.xor(InlineTree.loop(InlineTree.leaf("a"), InlineTree.leaf("b"), InlineTree.tau()),
				InlineTree.loop(InlineTree.leaf("c"), InlineTree.leaf("d"), InlineTree.tau())));
		trees.add(InlineTree.seq(InlineTree.loop(InlineTree.leaf("a"), InlineTree.leaf("b"), InlineTree.tau()),
				InlineTree.and(InlineTree.leaf("c"), InlineTree.leaf("d"), InlineTree.tau())));
		trees.add(InlineTree.loop(InlineTree.xor(InlineTree.leaf("a"), InlineTree.leaf("b"), InlineTree.tau()),
				InlineTree.and(InlineTree.leaf("c"), InlineTree.leaf("d")), InlineTree.tau()));
	}

	public int getRepetitions() {
		return repetitions;
	}

	public List<Call> getCalls() {
		List<Call> result = new ArrayList<>();
		for (Distribution distribution : distributions) {
			for (int repetition = 0; repetition < repetitions; repetition++) {
				for (EfficientTree tree : trees) {
					result.add(new Call(distribution, repetition, tree, this));
				}
			}
		}
		return result;
	}

	public File getDistributionsDirectory() {
		return new File(baseDirectory, "0 - distributions");
	}

	public File getLogsDirectory() {
		return new File(baseDirectory, "1 - logs");
	}

	public File getTestLogsDirectory() {
		return new File(baseDirectory, "2 - test logs");
	}

	public File getDiscoveredModelsDirectory() {
		return new File(baseDirectory, "3 - discovered cost models");
	}

	public File getMeasuresDirectory() {
		return new File(baseDirectory, "4 - measures");
	}

	public File getResultsDirectory() {
		return new File(baseDirectory, "5 - results");
	}

	public int getNumberOfTraces() {
		return numberOfTraces;
	}

	public List<Distribution> getDistributions() {
		return distributions;
	}

	public File getTestsResultsFile(Distribution distribution) {
		return new File(getResultsDirectory(), distribution.getAbbreviation() + "-results.txt");
	}
}