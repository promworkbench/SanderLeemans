package svn48healthcare.synthetic;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.statisticaltests.test.XLogWriterIncremental;

import gnu.trove.map.TIntObjectMap;

public class svn48Sx1GenerateLogs {

	public static void main(String[] args) throws IllegalArgumentException, SecurityException, IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getLogsDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File logFile = call.getLogFile();

			if (!call.isAttempted(logFile) && call.isDone(call.getDistributionsFile())) {
				System.out.println("generate log " + call.toString());

				EfficientTree tree = call.getTree();
				TIntObjectMap<DistributionInstance> distributions = svn48Sx0Distributions.loadDistributions(call);

				try {
					XLogWriterIncremental writer = new XLogWriterIncremental(logFile);

					ProMCanceller canceller = IvMCanceller.NEVER_CANCEL;
					for (XTrace trace : GenerateLog.generateLog(tree, parameters.getNumberOfTraces(), call.getSeed(),
							false, canceller, distributions)) {
						writer.writeTrace(trace);
					}

					writer.close();
				} catch (Exception e) {
					Call.setError(logFile, e);
				}
			}
		}

		System.out.println("done");
	}
}