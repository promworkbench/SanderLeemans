package svn48healthcare.synthetic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.InductiveMiner.Triple;

import gnu.trove.map.hash.THashMap;

public class svn48Sx5Gather {
	public static void main(String[] args) throws IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getResultsDirectory().mkdirs();

		Map<Distribution, List<Triple<Double, Double, Double>>> measures = new THashMap<>();

		for (Distribution distribution : parameters.getDistributions()) {
			measures.put(distribution, new ArrayList<>());
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

				measures.get(call.getDistribution()).add(Triple.of(logCost, modelCost, error));
			}
		}

		//write
		for (Distribution distribution : measures.keySet()) {
			PrintWriter writer = new PrintWriter(parameters.getTestsResultsFile(distribution));

			writer.println("log,model,error");
			for (Triple<Double, Double, Double> t : measures.get(distribution)) {
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
		System.out.println("done");
	}
}