package svn48healthcare.synthetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class svn48Sx0Distributions {

	public static TIntObjectMap<DistributionInstance> createDistributions(EfficientTree tree, Distribution distribution,
			long seed) throws IllegalArgumentException, SecurityException {

		TIntObjectMap<DistributionInstance> result = new TIntObjectHashMap<>();

		Random random = new Random(seed);
		for (int node = 0; node < tree.getMaxNumberOfNodes(); node++) {
			if (tree.isActivity(node)) {
				result.put(tree.getActivity(node), distribution.instantiate(random.nextInt(100, 1000)));
			}
		}

		return result;
	}

	public static TIntObjectMap<DistributionInstance> loadDistributions(Call call) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(call.getDistributionsFile()));

		TIntObjectMap<DistributionInstance> result = new TIntObjectHashMap<>();
		EfficientTree tree = call.getTree();
		for (int node = 0; node < tree.getMaxNumberOfNodes(); node++) {
			if (tree.isActivity(node)) {
				result.put(tree.getActivity(node),
						call.getDistribution().instantiate(Double.valueOf(reader.readLine())));
			}
		}

		reader.close();
		return result;
	}

	public static void main(String[] args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getDistributionsDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File distributionsFile = call.getDistributionsFile();

			if (!call.isAttempted(distributionsFile)) {
				System.out.println("generate distributions " + call.toString());

				EfficientTree tree = call.getTree();
				TIntObjectMap<DistributionInstance> distributions = createDistributions(tree, call.getDistribution(),
						call.getSeed());

				try {
					PrintWriter writer = new PrintWriter(new FileWriter(distributionsFile));

					for (int node = 0; node < tree.getMaxNumberOfNodes(); node++) {
						if (tree.isActivity(node)) {
							writer.println(distributions.get(tree.getActivity(node)).getParameter());
						}
					}

					writer.close();
				} catch (Exception e) {
					Call.setError(distributionsFile, e);
				}
			}
		}

		System.out.println("done");

		svn48Sx1GenerateLogs.main(new String[] {});
		svn48Sx2GenerateTestLogs.main(new String[] {});
		svn48Sx3CostModels.main(new String[] {});
		svn48Sx4Measure.main(new String[] {});
		svn48Sx5Gather.main(new String[] {});
	}
}
