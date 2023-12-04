package bpm2020cohortanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.processmining.framework.plugin.PluginContext;

import thesis.helperClasses.FakeContext;

public class Perform4Gather {

	public static void csv() throws IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		PrintWriter writer = new PrintWriter(parameters.getOutputCSVfile());
		PrintWriter writerNegative = new PrintWriter(parameters.getOutputCSVNegativefile());

		writer.write("featureSets traces time\n");
		writerNegative.write("featureSets traces\n");

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {
			for (int featureSetSize : parameters.getFeatureSetSizes()) {
				Call call = new Call(logFile.getName(), featureSetSize, parameters);
				if (call.isDone(call.getLogMeasuresFile()) && !call.isError(call.getLogMeasuresFile())
						&& call.isDone(call.getFeatureSetsFile()) && !call.isError(call.getFeatureSetsFile())) {
					//read traces
					String traces;
					{
						BufferedReader reader = new BufferedReader(new FileReader(call.getLogMeasuresFile()));
						traces = reader.readLine().split(" ")[0];
						reader.close();
					}

					//read feature sets
					String featureSets;
					{
						BufferedReader reader = new BufferedReader(new FileReader(call.getFeatureSetsFile()));
						featureSets = reader.readLine();
						reader.close();
					}

					if (call.isDone(call.getCohortsTimeFile()) && !call.isError(call.getCohortsTimeFile())) {
						//done, read time
						long time;
						{
							BufferedReader reader = new BufferedReader(new FileReader(call.getCohortsTimeFile()));
							time = Long.valueOf(reader.readLine().split(" ")[0]) / 1000;
							reader.close();
						}

						writer.write(featureSets + " " + traces + " " + time + "\n");
					} else {
						//not done
						writerNegative.write(featureSets + " " + traces + "\n");
					}
				}
			}
		}
		writer.close();
		writerNegative.close();
	}

	public static void main(String... args) throws Exception {
		csv();

		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();

		PrintWriter writer = new PrintWriter(parameters.getOutputfile());

		File[] files = parameters.getLogDirectory().listFiles();
		List<File> newFiles = Arrays.asList(files);
		Collections.sort(newFiles, new Comparator<File>() {
			public int compare(File o1, File o2) {
				return parameters.getLog2abbreviation().get(o1.getName())
						.compareTo(parameters.getLog2abbreviation().get(o2.getName()));
			}
		});

		for (File logFile : newFiles) {
			if (parameters.getLog2abbreviation().containsKey(logFile.getName())) {
				writer.write(parameters.getLog2abbreviation().get(logFile.getName()) + "&");
			} else {
				writer.write(logFile.getName() + "&");
			}

			Call call = new Call(logFile.getName(), -1, parameters);

			if (call.isDone(call.getLogMeasuresFile()) && !call.isError(call.getLogMeasuresFile())) {
				//write log measures
				{
					BufferedReader reader = new BufferedReader(new FileReader(call.getLogMeasuresFile()));
					String traces = reader.readLine().split(" ")[0];
					String traceAttributes = reader.readLine().split(" ")[0];
					String activities = reader.readLine().split(" ")[0];
					String events = reader.readLine().split(" ")[0];
					reader.close();

					writer.write(traces + "&");
					//writer.write(events + "&");
					writer.write(activities + "&");
					writer.write(traceAttributes + "&");
				}

				//writer.write("feature sets&");

				for (int featureSetSize : parameters.getFeatureSetSizes()) {
					Call fCall = new Call(logFile.getName(), featureSetSize, parameters);
					File featureSetFile = fCall.getFeatureSetsFile();

					//number of feature sets
					if (call.isDone(featureSetFile) && !call.isError(featureSetFile)) {
						BufferedReader reader = new BufferedReader(new FileReader(featureSetFile));
						String size = reader.readLine();
						writer.write(size);
						reader.close();
					}

					//					if (featureSetSize < parameters.getFeatureSetSizes().length) {
					writer.write("&");
					//					}
				}

				for (int featureSetSize : parameters.getFeatureSetSizes()) {
					Call fCall = new Call(logFile.getName(), featureSetSize, parameters);
					if (fCall.isDone(fCall.getCohortsTimeFile())) {
						//write time
						BufferedReader reader = new BufferedReader(new FileReader(fCall.getCohortsTimeFile()));
						String time = reader.readLine();
						writer.write("\\textit{");
						long ms = Long.valueOf(time.split(" ")[0]);
						writer.write(ms / 1000 + "");
						writer.write("}");
						reader.close();
						//					} else if (fCall.getCohortsTimeFile().exists() && fCall.isError(fCall.getCohortsTimeFile())) {
						//						writer.write("-");
					}

					if (featureSetSize < parameters.getFeatureSetSizes().length) {
						writer.write("&");
					}
				}
			}

			writer.write("\\\\\n");
		}

		//writer.write("\\bottomrule");
		writer.close();

		System.out.println("done");
	}

}