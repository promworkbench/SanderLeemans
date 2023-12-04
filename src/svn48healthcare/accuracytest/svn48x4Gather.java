package svn48healthcare.accuracytest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.InductiveMiner.Triple;

import gnu.trove.map.hash.THashMap;

public class svn48x4Gather {
	public static void main(String[] args) throws IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getResultsDirectory().mkdirs();

		Map<File, List<Triple<Double, Double, Double>>> measures = new THashMap<>();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {
			measures.put(logFile, new ArrayList<>());
		}

		//read
		List<Call> calls = parameters.getCalls();
		for (Call call : calls) {
			if (call.isMeasureDone()) {
				BufferedReader reader = new BufferedReader(new FileReader(call.getMeasureFile()));

				reader.readLine();
				double logCost = Double.valueOf(reader.readLine());
				reader.readLine();
				double modelCost = Double.valueOf(reader.readLine());
				reader.readLine();
				double error = Double.valueOf(reader.readLine());

				reader.close();

				measures.get(call.getLogFile()).add(Triple.of(logCost, modelCost, error));
			}
		}

		//write
		for (File logFile : measures.keySet()) {
			PrintWriter writer = new PrintWriter(parameters.getTestsResultsFile(logFile));

			writer.println("log,model,error");
			for (Triple<Double, Double, Double> t : measures.get(logFile)) {
				writer.println(t.getA() + "," + t.getB() + "," + t.getC());
			}

			writer.close();
		}

		//time
		long max = 0;
		for (Call call : calls) {
			if (call.isDone(call.getDiscoveredModelTimeFile())) {
				BufferedReader reader = new BufferedReader(new FileReader(call.getDiscoveredModelTimeFile()));
				long time = Long.valueOf(reader.readLine());
				max = Math.max(max, time);
				reader.close();
			}
		}
		System.out.println(max + " max discovery duration");
	}
}