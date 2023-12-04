package svn48healthcare.synthetic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import svn48healthcare.CostModelAbstract;
import svn48healthcare.DecisionTree2CostModel;
import thesis.helperClasses.FakeContext;

public class svn48Sx3CostModels {
	public static void main(String[] args) throws IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getDiscoveredModelsDirectory().mkdirs();
		PluginContext context = new FakeContext();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File logFile = call.getLogFile();
			File modelFile = call.getDiscoveredModelFile();

			if (!call.isAttempted(modelFile) && call.isDone(call.getLogFile())) {
				System.out.println("compute cost model " + call.toString());
				try {

					XEventClassifier classifier = new XEventNameClassifier();
					XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

					long start = System.currentTimeMillis();

					Pair<CostModelAbstract, List<String>> costModel = DecisionTree2CostModel.computeModel(log,
							classifier);

					long end = System.currentTimeMillis();
					{
						PrintWriter writer = new PrintWriter(call.getDiscoveredModelTimeFile());
						writer.write((end - start) + "\n");
						writer.write("ms\n");
						writer.close();
					}

					{
						double[] costModelParameters = costModel.getA().getParameters();

						PrintWriter writer = new PrintWriter(modelFile);
						writer.println(costModel.getB().size());
						for (String activity : costModel.getB()) {
							writer.println(activity);
						}
						writer.println(costModelParameters.length);
						for (double parameter : costModelParameters) {
							writer.println(parameter);
						}

						writer.close();
					}
				} catch (Exception e) {
					Call.setError(logFile, e);
				}
			}
		}

		System.out.println("done");
	}
}
