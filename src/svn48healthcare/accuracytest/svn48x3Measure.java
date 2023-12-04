package svn48healthcare.accuracytest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import svn48healthcare.CostModel;
import svn48healthcare.CostModelAbstract;
import svn48healthcare.CostModelImplSojourn;
import thesis.helperClasses.FakeContext;

public class svn48x3Measure {
	public static void main(String... args) throws IOException, Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getMeasuresDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			processCall(parameters, context, call);
		}
	}

	private static void processCall(ExperimentParameters parameters, PluginContext context, Call call)
			throws IOException, Exception, FileNotFoundException {
		File discoveredModelFile = call.getDiscoveredModelFile();
		File testLogFile = call.getLogSplitFileTest();

		if (Call.isAttempted(discoveredModelFile)) {

			if (call.isError(discoveredModelFile)) {
				//propagate discovery errors
				if (!call.isMeasureDone()) {
					System.out.println("propagate error");
					call.getMeasureFile().getParentFile().mkdirs();
					call.setError(call.getMeasureFile(), null);
				}
			} else {
				if (!call.isMeasureDone()) {
					//we can perform a measure
					call.getMeasureFile().getParentFile().mkdirs();

					System.out.println("measuring to " + call.getMeasureFile());
					System.out.println(LocalDateTime.now());

					XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, testLogFile);
					CostModel model = readModelFile(discoveredModelFile);

					List<DataRow<Object>> results = model.getModelRepresentation(log);

					try {
						PrintWriter writer = new PrintWriter(call.getMeasureFile());

						for (DataRow<Object> row : results) {
							if (row.getName(1).contains("average")) {
								writer.println(Arrays.toString(row.getNames()));
								writer.println(row.getValues()[0].getValue());
							}
						}

						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
						call.setError(call.getMeasureFile(), e);
					}
				}
			}
		}
	}

	public static boolean isError(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		String firstLine = r.readLine();
		r.close();
		return firstLine.startsWith("error");
	}

	public static CostModelAbstract readModelFile(File modelFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(modelFile));

		int numberOfActivities = Integer.valueOf(reader.readLine());
		List<String> activities = new ArrayList<>();
		for (int i = 0; i < numberOfActivities; i++) {
			String activity = reader.readLine();
			activities.add(activity);
		}

		int numberOfParameters = Integer.valueOf(reader.readLine());
		double[] parameters = new double[numberOfParameters];
		for (int i = 0; i < numberOfParameters; i++) {
			parameters[i] = Double.valueOf(reader.readLine());
		}

		reader.close();

		CostModelAbstract result = new CostModelImplSojourn(activities, new XEventNameClassifier());
		result.setParameters(parameters);

		return result;
	}
}