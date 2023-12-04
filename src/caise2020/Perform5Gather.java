package caise2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import org.processmining.framework.plugin.PluginContext;

import thesis.helperClasses.FakeContext;

public class Perform5Gather {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getOutputfile().mkdirs();

		PrintWriter writer = new PrintWriter(parameters.getOutputfile());

		//write header
		writer.write("log,");
		writer.write("log duration (ms),");
		for (Algorithm algorithm : parameters.getAlgorithms()) {
			writer.write(algorithm.getName() + " recall,");
			writer.write(algorithm.getName() + " precision,");
			writer.write(algorithm.getName() + " simulation duration (ms),");
		}
		writer.write("\n");

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {
			writer.write(logFile.getName() + ",");
			
			//read log duration
			{
				Call call = new Call(logFile.getName(), null, parameters);
				BufferedReader reader = new BufferedReader(new FileReader(call.getLogMeasuresFile()));
				String line = reader.readLine();
				double recall = Double.valueOf(line);
				writer.write(recall + ",");
			}
			
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				Call call = new Call(logFile.getName(), algorithm, parameters);

				if (Call.isAttempted(call.getDiscoveredModelFile()) && Call.isError(call.getDiscoveredModelFile())) {
					writer.write("error,error,error,");
				} else if (Call.isDone(call.getEntropyFile()) && call.isDone(call.getSimulationFile())
						&& call.isDone(call.getLogMeasuresFile())) {

					//read recall & precision
					{
						BufferedReader reader = new BufferedReader(new FileReader(call.getEntropyFile()));
						String line = reader.readLine();
						double recall = Double.valueOf(line.substring(0, line.indexOf(' ')));
						writer.write(recall + ",");
						String line2 = reader.readLine();
						double precision = Double.valueOf(line2.substring(0, line2.indexOf(' ')));
						writer.write(precision + ",");
					}

					//read simulation duration
					{
						BufferedReader reader = new BufferedReader(new FileReader(call.getSimulationFile()));
						String line = reader.readLine();
						double recall = Double.valueOf(line);
						writer.write(recall + ",");
					}

				} else {
					writer.write(",,,");
				}
			}
			writer.write("\n");
		}

		writer.close();
	}
}
